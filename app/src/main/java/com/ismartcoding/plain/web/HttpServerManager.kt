package com.ismartcoding.plain.web

import android.content.Context
import android.content.Intent
import android.util.Base64
import com.ismartcoding.lib.channel.sendEvent
import com.ismartcoding.lib.helpers.CoroutinesHelper.coIO
import com.ismartcoding.lib.helpers.CryptoHelper
import com.ismartcoding.lib.helpers.JksHelper
import com.ismartcoding.lib.helpers.JsonHelper
import com.ismartcoding.lib.logcat.LogCat
import com.ismartcoding.plain.BuildConfig
import com.ismartcoding.plain.Constants
import com.ismartcoding.plain.MainApp
import com.ismartcoding.plain.TempData
import com.ismartcoding.plain.api.HttpClientManager
import com.ismartcoding.plain.data.HttpServerCheckResult
import com.ismartcoding.plain.enums.HttpServerState
import com.ismartcoding.plain.preference.PasswordPreference
import com.ismartcoding.plain.db.AppDatabase
import com.ismartcoding.plain.db.SessionClientTsUpdate
import com.ismartcoding.plain.features.ConfirmToAcceptLoginEvent
import com.ismartcoding.plain.features.HttpServerStateChangedEvent
import com.ismartcoding.plain.helpers.UrlHelper
import com.ismartcoding.plain.services.HttpServerService
import com.ismartcoding.plain.web.websocket.WebSocketSession
import io.ktor.client.plugins.websocket.ws
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.websocket.send
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.Timer
import kotlin.collections.set
import kotlin.concurrent.timerTask

object HttpServerManager {
    private const val SSL_KEY_ALIAS = Constants.SSL_NAME
    var tokenCache = mutableMapOf<String, ByteArray>() // cache the session token, format: <client_id>:<token>
    val clientIpCache = mutableMapOf<String, String>() // format: <client_id>:<client_ip>
    val wsSessions = Collections.synchronizedSet<WebSocketSession>(LinkedHashSet())
    val clientRequestTs = mutableMapOf<String, Long>()
    var httpServerError: String = ""
    val portsInUse = mutableSetOf<Int>()
    val httpsPorts = setOf(8043, 8143, 8243, 8343, 8443, 8543, 8643, 8743, 8843, 8943)
    val httpPorts = setOf(8080, 8180, 8280, 8380, 8480, 8580, 8680, 8780, 8880, 8980)

    suspend fun resetPasswordAsync(): String {
        val password = CryptoHelper.randomPassword(6)
        PasswordPreference.putAsync(MainApp.instance, password)
        return password
    }

    suspend fun stopServiceAsync(context: Context) {
        sendEvent(HttpServerStateChangedEvent(HttpServerState.STOPPING))
        try {
            val client = HttpClientManager.httpClient()
            val r = client.get(UrlHelper.getShutdownUrl())
            if (r.status == HttpStatusCode.Gone) {
                LogCat.d("http server is stopped")
            }
        } catch (ex: Exception) {
            LogCat.e(ex.toString())
            ex.printStackTrace()
        }
        context.stopService(Intent(context, HttpServerService::class.java))
        httpServerError = ""
        portsInUse.clear()
        sendEvent(HttpServerStateChangedEvent(HttpServerState.OFF))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun checkServerAsync(): HttpServerCheckResult {
        var websocket = false
        var http = false
        var retry = 3
        val client = HttpClientManager.httpClient()
        while (retry-- > 0) {
            try {
                client.ws(urlString = UrlHelper.getWsTestUrl()) {
                    val reason = this.closeReason.getCompleted()
                    LogCat.d("closeReason: $reason")
                    if (reason?.message == BuildConfig.APPLICATION_ID) {
                        websocket = true
                    }
                }
                retry = 0
            } catch (ex: Exception) {
                delay(1000)
                ex.printStackTrace()
                LogCat.e(ex.toString())
            }
        }

        try {
            val r = client.get(UrlHelper.getHealthCheckUrl())
            http = r.bodyAsText() == BuildConfig.APPLICATION_ID
        } catch (ex: Exception) {
            ex.printStackTrace()
            LogCat.e(ex.toString())
        }

        return HttpServerCheckResult(websocket, http)
    }

    private suspend fun passwordToToken(): ByteArray {
        return hashToToken(CryptoHelper.sha512(PasswordPreference.getAsync(MainApp.instance).toByteArray()))
    }

    fun hashToToken(hash: String): ByteArray {
        return hash.substring(0, 32).toByteArray()
    }

    suspend fun loadTokenCache() {
        tokenCache.clear()
        SessionList.getItemsAsync().forEach {
            tokenCache[it.clientId] = Base64.decode(it.token, Base64.NO_WRAP)
        }
    }

    private fun getSSLKeyStore(context: Context): KeyStore {
        val file = File(context.filesDir, "keystore2.jks")
        if (!file.exists()) {
            val keyStore = JksHelper.genJksFile(SSL_KEY_ALIAS, TempData.keyStorePassword, Constants.SSL_NAME)
            val out = FileOutputStream(file)
            keyStore.store(out, null)
            out.close()
        }

        return KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            file.inputStream().use {
                load(it, null)
            }
        }
    }

    fun createHttpServer(context: Context): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
        val password = TempData.keyStorePassword.toCharArray()
        val httpPort = TempData.httpPort
        val httpsPort = TempData.httpsPort
        val environment = applicationEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
        }

        return embeddedServer(Netty, environment, configure = {
            runningLimit = 1000
            tcpKeepAlive = true
            connector {
                port = httpPort
            }
            enableHttp2 = false
            sslConnector(
                keyStore = getSSLKeyStore(context),
                keyAlias = SSL_KEY_ALIAS,
                keyStorePassword = { password },
                privateKeyPassword = { password },
            ) {
                port = httpsPort
            }
            channelPipelineConfig = {
                addLast("cancellationDetector", AbortableRequestHandler())
            }
        }, HttpModule.module)
    }

    fun getSSLSignature(context: Context): ByteArray {
        val keystore = getSSLKeyStore(context)
        val cert = keystore.getCertificate(SSL_KEY_ALIAS) as X509Certificate
        return cert.signature
    }

    fun clientTsInterval() {
        val duration = 5000L
        Timer().scheduleAtFixedRate(
            timerTask {
                val now = System.currentTimeMillis()
                val updates =
                    clientRequestTs.filter { it.value + duration > now }
                        .map { SessionClientTsUpdate(it.key, Instant.fromEpochMilliseconds(it.value)) }
                if (updates.isNotEmpty()) {
                    coIO {
                        AppDatabase.instance.sessionDao().updateTs(updates)
                    }
                }
            },
            duration,
            duration,
        )
    }

    suspend fun respondTokenAsync(
        event: ConfirmToAcceptLoginEvent,
        clientIp: String,
    ) {
        val token = CryptoHelper.generateAESKey()
        SessionList.addOrUpdateAsync(event.clientId) {
            val r = event.request
            it.clientIP = clientIp
            it.osName = r.osName
            it.osVersion = r.osVersion
            it.browserName = r.browserName
            it.browserVersion = r.browserVersion
            it.token = token
        }
        HttpServerManager.loadTokenCache()
        event.session.send(
            CryptoHelper.aesEncrypt(
                HttpServerManager.passwordToToken(),
                JsonHelper.jsonEncode(
                    AuthResponse(
                        AuthStatus.COMPLETED,
                        token,
                    ),
                ),
            ),
        )
    }
}
