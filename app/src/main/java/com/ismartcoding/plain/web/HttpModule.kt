package com.ismartcoding.plain.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import com.google.common.io.ByteStreams
import com.google.common.io.FileWriteMode
import com.google.common.io.Files
import com.ismartcoding.lib.channel.sendEvent
import com.ismartcoding.lib.extensions.compress
import com.ismartcoding.lib.extensions.getFinalPath
import com.ismartcoding.lib.extensions.isImageFast
import com.ismartcoding.lib.extensions.newFile
import com.ismartcoding.lib.extensions.parse
import com.ismartcoding.lib.extensions.scanFileByConnection
import com.ismartcoding.lib.extensions.toThumbBytesAsync
import com.ismartcoding.lib.extensions.urlEncode
import com.ismartcoding.lib.helpers.CoroutinesHelper.coIO
import com.ismartcoding.lib.helpers.CoroutinesHelper.withIO
import com.ismartcoding.lib.helpers.CryptoHelper
import com.ismartcoding.lib.helpers.JsonHelper
import com.ismartcoding.lib.helpers.JsonHelper.jsonDecode
import com.ismartcoding.lib.helpers.ZipHelper
import com.ismartcoding.lib.isSPlus
import com.ismartcoding.lib.logcat.LogCat
import com.ismartcoding.lib.upnp.UPnPController
import com.ismartcoding.plain.BuildConfig
import com.ismartcoding.plain.MainApp
import com.ismartcoding.plain.TempData
import com.ismartcoding.plain.data.DownloadFileItem
import com.ismartcoding.plain.data.DownloadFileItemWrap
import com.ismartcoding.plain.data.UploadInfo
import com.ismartcoding.plain.enums.DataType
import com.ismartcoding.plain.enums.PasswordType
import com.ismartcoding.plain.preference.AuthTwoFactorPreference
import com.ismartcoding.plain.preference.PasswordPreference
import com.ismartcoding.plain.preference.PasswordTypePreference
import com.ismartcoding.plain.features.ConfirmToAcceptLoginEvent
import com.ismartcoding.plain.features.audio.AudioMediaStoreHelper
import com.ismartcoding.plain.features.file.FileSortBy
import com.ismartcoding.plain.features.image.ImageMediaStoreHelper
import com.ismartcoding.plain.features.media.CastPlayer
import com.ismartcoding.plain.features.PackageHelper
import com.ismartcoding.plain.features.video.VideoMediaStoreHelper
import com.ismartcoding.plain.helpers.TempHelper
import com.ismartcoding.plain.helpers.UrlHelper
import com.ismartcoding.plain.web.websocket.WebSocketSession
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.http.content.EntityTagVersion
import io.ktor.http.content.LastModifiedVersion
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.http.content.SPAConfig
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.conditionalheaders.ConditionalHeaders
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.forwardedheaders.ForwardedHeaders
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.partialcontent.PartialContent
import io.ktor.server.request.header
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveText
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondOutputStream
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.core.use
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.send
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.collections.set

object HttpModule {
    @SuppressLint("SuspiciousIndentation")
    val module: Application.() -> Unit = {
        install(CachingHeaders) {
            options { _, outgoingContent ->
                when (outgoingContent.contentType?.withoutParameters()) {
                    ContentType.Text.CSS, ContentType.Application.JavaScript ->
                        CachingOptions(
                            CacheControl.MaxAge(maxAgeSeconds = 3600 * 24 * 30),
                        )

                    else -> null
                }
            }
        }

        install(CORS) {
            if (BuildConfig.DEBUG) {
                allowHost("*")
            } else {
                allowHost("localhost:3000")
                allowHost("127.0.0.1:3000")
            }
            allowHeadersPrefixed("c-")
            allowHeader("x-box-api")
        }

        install(ConditionalHeaders)
        install(WebSockets)
//        install(Compression) // this will slow down the download speed
        install(ForwardedHeaders)
        install(PartialContent)
        install(AutoHeadResponse)
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                },
            )
        }

        intercept(ApplicationCallPipeline.Plugins) {
            if (!TempData.webEnabled) {
                call.respond(HttpStatusCode.NotFound)
                return@intercept finish()
            }
        }

        routing {
            val config = SPAConfig()
            config.filesPath = "web"
            staticResources(config.applicationRoute, config.filesPath, index = config.defaultPage) {
                cacheControl {
                    arrayListOf(
                        CacheControl.NoCache(CacheControl.Visibility.Public),
                        CacheControl.NoStore(CacheControl.Visibility.Public),
                    )
                }
                default(config.defaultPage)
            }

            get("/health_check") {
                call.respond(HttpStatusCode.OK, BuildConfig.APPLICATION_ID)
            }

            get("/shutdown") {
                val ip = call.request.origin.remoteHost
                LogCat.d("$ip is shutting down the server")
                if (ip != "localhost") {
                    call.respond(HttpStatusCode.Forbidden)
                    return@get
                }

                HttpServerManager.wsSessions.forEach {
                    it.session.close()
                }
                HttpServerManager.wsSessions.clear()
                val latch = CompletableDeferred<Nothing>()
                val application = call.application
                val environment = application.environment
                application.launch {
                    latch.join()
                    application.monitor.raise(ApplicationStopPreparing, environment)
                    application.dispose()
                }

                try {
                    call.respond(HttpStatusCode.Gone)
                } finally {
                    latch.cancel()
                }
            }

            get("/media/{id}") {
                val id = call.parameters["id"]?.split(".")?.get(0) ?: ""
                if (id.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val path = UrlHelper.getMediaPath(id)
                    if (path.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }

                    if (path.startsWith("content://")) {
                        val bytes = MainApp.instance.contentResolver.openInputStream(Uri.parse(path))?.buffered()?.use { it.readBytes() }
                        call.respondBytes(bytes!!)
                    } else if (path.isImageFast()) {
                        call.respondFile(File(path))
                    } else {
                        val file = File(path)
                        call.response.run {
                            header("realTimeInfo.dlna.org", "DLNA.ORG_TLAG=*")
                            header("contentFeatures.dlna.org", "")
                            header("transferMode.dlna.org", "Streaming")
                            header("Connection", "keep-alive")
                            header(
                                "Server",
                                "DLNADOC/1.50 UPnP/1.0 Plain/1.0 Android/" + Build.VERSION.RELEASE,
                            )

                            EntityTagVersion(file.lastModified().hashCode().toString())
                            LastModifiedVersion(Date(file.lastModified()))
                            status(HttpStatusCode.PartialContent) // some TV os only accepts 206
                        }
                        call.respondFile(file)
                    }
                } catch (ex: Exception) {
                    // ex.printStackTrace()
                    call.respondText("File is expired or does not exist. $ex", status = HttpStatusCode.Forbidden)
                }
            }

            get("/zip/dir") {
                val q = call.request.queryParameters
                val id = q["id"] ?: ""
                if (id.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val path = UrlHelper.decrypt(id)
                val folder = File(path)
                if (!folder.exists() || !folder.isDirectory) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                val fileName = (q["name"] ?: "${folder.name}.zip").urlEncode().replace("+", "%20")
                call.response.header("Content-Disposition", "attachment;filename=\"${fileName}\";filename*=utf-8''\"${fileName}\"")
                call.response.header(HttpHeaders.ContentType, ContentType.Application.Zip.toString())
                call.respondOutputStream(ContentType.Application.Zip) {
                    ZipOutputStream(this).use { zip ->
                        ZipHelper.zipFolderToStreamAsync(folder, zip)
                    }
                }
            }

            get("/zip/files") {
                val query = call.request.queryParameters
                val id = query["id"] ?: ""
                if (id.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val json = JSONObject(UrlHelper.decrypt(id))
                    var paths: List<DownloadFileItem> = arrayListOf()
                    val type = json.optString("type")
                    if (type.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }

                    val q = json.optString("query")
                    val context = MainApp.instance
                    when (type) {
                        DataType.PACKAGE.name -> {
                            paths = PackageHelper.search(q, Int.MAX_VALUE, 0, FileSortBy.NAME_ASC).map { DownloadFileItem(it.path, "${it.name.replace(" ", "")}-${it.id}.apk") }
                        }

                        DataType.VIDEO.name -> {
                            paths = VideoMediaStoreHelper.search(context, q, Int.MAX_VALUE, 0, FileSortBy.DATE_DESC).map { DownloadFileItem(it.path, "") }
                        }

                        DataType.AUDIO.name -> {
                            paths = AudioMediaStoreHelper.search(context, q, Int.MAX_VALUE, 0, FileSortBy.DATE_DESC).map { DownloadFileItem(it.path, "") }
                        }

                        DataType.IMAGE.name -> {
                            paths = ImageMediaStoreHelper.search(context, q, Int.MAX_VALUE, 0, FileSortBy.DATE_DESC).map { DownloadFileItem(it.path, "") }
                        }

                        DataType.FILE.name -> {
                            val tmpId = json.optString("id")
                            val value = TempHelper.getValue(tmpId)
                            TempHelper.clearValue(tmpId)
                            if (value.isEmpty()) {
                                call.respond(HttpStatusCode.NotFound)
                                return@get
                            }

                            paths = JSONArray(value).parse { DownloadFileItem(it.optString("path"), it.optString("name")) }
                        }
                    }

                    val items = paths.map { DownloadFileItemWrap(File(it.path), it.name) }.filter { it.file.exists() }
                    val dirs = items.filter { it.file.isDirectory }
                    val fileName = (json.optString("name").ifEmpty { "download.zip" }).urlEncode().replace("+", "%20")
                    call.response.header("Content-Disposition", "attachment;filename=\"${fileName}\";filename*=utf-8''\"${fileName}\"")
                    call.response.header(HttpHeaders.ContentType, ContentType.Application.Zip.toString())
                    call.respondOutputStream(ContentType.Application.Zip) {
                        ZipOutputStream(this).use { zip ->
                            items.forEach { item ->
                                if (dirs.any { item.file.absolutePath != it.file.absolutePath && item.file.absolutePath.startsWith(it.file.absolutePath) }) {
                                } else {
                                    val filePath = item.name.ifEmpty { item.file.name }
                                    if (item.file.isDirectory) {
                                        zip.putNextEntry(ZipEntry("$filePath/"))
                                        ZipHelper.zipFolderToStreamAsync(item.file, zip, filePath)
                                    } else {
                                        zip.putNextEntry(ZipEntry(filePath))
                                        item.file.inputStream().copyTo(zip)
                                    }
                                    zip.closeEntry()
                                }
                            }
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
                }
            }

            get("/fs") {
                val q = call.request.queryParameters
                val id = q["id"] ?: ""
                if (id.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val context = MainApp.instance
                    val path = UrlHelper.decrypt(id).getFinalPath(context)
                    if (path.startsWith("content://")) {
                        val bytes = context.contentResolver.openInputStream(Uri.parse(path))?.buffered()?.use { it.readBytes() }
                        if (bytes != null) {
                            call.respondBytes(bytes)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else if (path.startsWith("pkgicon://")) {
                        val packageName = path.substring(10)
                        val bitmap = PackageHelper.getIcon(packageName)
                        val bytes = ByteArrayOutputStream().use {
                            bitmap.compress(80, it)
                            it.toByteArray()
                        }
                        call.respond(bytes)
                    } else {
                        val file = File(path)
                        if (!file.exists()) {
                            call.respond(HttpStatusCode.NotFound)
                            return@get
                        }

                        val fileName = (q["name"] ?: file.name).urlEncode().replace("+", "%20")
                        if (q["dl"] == "1") {
                            call.response.header("Content-Disposition", "attachment;filename=\"${fileName}\";filename*=utf-8''\"${fileName}\"")
                        } else {
                            call.response.header("Content-Disposition", "inline;filename=\"${fileName}\";filename*=utf-8''\"${fileName}\"")
                        }

                        val w = q["w"]?.toIntOrNull()
                        val h = q["h"]?.toIntOrNull()
                        val centerCrop = q["cc"]?.toBooleanStrictOrNull() ?: true
                        // get video/image thumbnail
                        if (w != null && h != null) {
                            val bytes = file.toThumbBytesAsync(MainApp.instance, w, h, centerCrop)
                            if (bytes != null) {
                                call.respondBytes(bytes)
                            }
                            return@get
                        }

                        call.respondFile(file)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    call.respondText("File is expired or does not exist. $ex", status = HttpStatusCode.Forbidden)
                }
            }

            route("/callback/cast", HttpMethod("NOTIFY")) {
                handle {
                    val xml = call.receiveText()
                    LogCat.d(xml)
                    // the TV could send the callback twice in short time, the second one should be ignore if it has AVTransportURIMetaData field.
                    if (xml.contains("TransportState val=\"STOPPED\"") && !xml.contains("AVTransportURIMetaData")) {
                        withIO {
                            if (CastPlayer.items?.isNotEmpty() == true) {
                                CastPlayer.currentDevice?.let { device ->
                                    val currentUri = CastPlayer.currentUri
                                    var index = CastPlayer.items!!.indexOfFirst { it.path == currentUri }
                                    index++
                                    if (index > CastPlayer.items!!.size - 1) {
                                        index = 0
                                    }
                                    val current = CastPlayer.items!![index]
                                    if (current.path != currentUri) {
                                        LogCat.d(current.path)
                                        UPnPController.setAVTransportURIAsync(device, UrlHelper.getMediaHttpUrl(current.path))
                                        CastPlayer.currentUri = current.path
                                    }
                                }
                            }
                        }
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }

            post("/upload") {
                val clientId = call.request.header("c-id") ?: ""
                if (clientId.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val token = HttpServerManager.tokenCache[clientId]
                if (token == null) {
                    call.response.status(HttpStatusCode.Unauthorized)
                    return@post
                }
                try {
                    lateinit var info: UploadInfo
                    var fileName = ""
                    call.receiveMultipart().forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                when (part.name) {
                                    "info" -> {
                                        var requestStr = ""
                                        val decryptedBytes = CryptoHelper.aesDecrypt(token, part.streamProvider().readBytes())
                                        if (decryptedBytes != null) {
                                            requestStr = decryptedBytes.decodeToString()
                                        }
                                        if (requestStr.isEmpty()) {
                                            call.response.status(HttpStatusCode.Unauthorized)
                                            return@forEachPart
                                        }

                                        info = jsonDecode<UploadInfo>(requestStr)
                                    }

                                    "file" -> {
                                        fileName = part.originalFileName as String
                                        if (info.dir.isEmpty() || fileName.isEmpty()) {
                                            call.respond(HttpStatusCode.BadRequest)
                                            return@forEachPart
                                        }
                                        File(info.dir).mkdirs()
                                        var destFile = File("${info.dir}/$fileName")
                                        if (info.index == 0 && destFile.exists()) {
                                            if (info.replace) {
                                                destFile.delete()
                                            } else {
                                                destFile = destFile.newFile()
                                                fileName = destFile.name
                                            }
                                        }

                                        // use append file way
                                        val noSplitFiles = false
                                        if (noSplitFiles) {
                                            part.streamProvider().use { input ->
                                                Files.asByteSink(destFile).writeFrom(input)
                                            }
                                            if (info.total - 1 == info.index) {
                                                MainApp.instance.scanFileByConnection(destFile, null)
                                            }
                                        } else {
                                            if (info.total > 1) {
                                                destFile = File("${info.dir}/$fileName.part${String.format("%03d", info.index)}")
//                                                if (destFile.exists() && destFile.length() == info.size) {
//                                                    // skip if the part file is already uploaded
//                                                } else {
                                                part.streamProvider().use { input ->
                                                    Files.asByteSink(destFile).writeFrom(input)
                                                }
                                                //  }
                                            } else {
                                                part.streamProvider().use { input ->
                                                    Files.asByteSink(destFile).writeFrom(input)
                                                }
                                            }

                                            if (info.total - 1 == info.index) {
                                                if (info.total > 1) {
                                                    // merge part files into original file
                                                    destFile = File("${info.dir}/$fileName")
                                                    val partFiles = File(info.dir).listFiles()?.filter { it.name.startsWith("$fileName.part") }?.sortedBy { it.name } ?: arrayListOf()
                                                    Files.asByteSink(destFile, FileWriteMode.APPEND).openStream().use { fos ->
                                                        partFiles.forEach { partFile ->
                                                            Files.asByteSource(partFile).openStream().use { input ->
                                                                ByteStreams.copy(input, fos)
                                                            }
                                                            partFile.delete()
                                                        }
                                                    }
                                                }
                                                MainApp.instance.scanFileByConnection(destFile, null)
                                            }
                                        }
                                    }

                                    else -> {}
                                }
                            }

                            else -> {
                            }
                        }
                        part.dispose()
                    }
                    call.respond(HttpStatusCode.Created, fileName)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
                }
            }

            // this api is to fix the websocket takes 10s to get remoteAddress on some phones.
            post("/init") {
                val clientId = call.request.headers["c-id"] ?: ""
                if (clientId.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "`c-id` is missing in the headers")
                    return@post
                }
                if (!TempData.webEnabled) {
                    call.respond(HttpStatusCode.Forbidden, "web_access_disabled")
                    return@post
                }
                HttpServerManager.clientIpCache[clientId] = call.request.origin.remoteAddress
                if (PasswordTypePreference.getValueAsync(MainApp.instance) == PasswordType.NONE) {
                    call.respondText(HttpServerManager.resetPasswordAsync())
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            webSocket("/") {
                val q = call.request.queryParameters
                if (q["test"] == "1") {
                    close(CloseReason(CloseReason.Codes.NORMAL, BuildConfig.APPLICATION_ID))
                    return@webSocket
                }
                val clientId = q["cid"] ?: ""
                if (clientId.isEmpty()) {
                    LogCat.e("ws: `cid` is missing")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "`cid` is missing"))
                    return@webSocket
                }

                val session = WebSocketSession(System.currentTimeMillis(), clientId, this)
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Binary -> {
                                if (q["auth"] == "1") {
                                    var r: AuthRequest? = null
                                    val hash = CryptoHelper.sha512(PasswordPreference.getAsync(MainApp.instance).toByteArray())
                                    val token = HttpServerManager.hashToToken(hash)
                                    val decryptedBytes = CryptoHelper.aesDecrypt(token, frame.readBytes())
                                    if (decryptedBytes != null) {
                                        r = jsonDecode<AuthRequest>(decryptedBytes.decodeToString())
                                    }
                                    if (r?.password == hash) {
                                        val event = ConfirmToAcceptLoginEvent(this, clientId, r)
                                        if (AuthTwoFactorPreference.getAsync(MainApp.instance)) {
                                            send(CryptoHelper.aesEncrypt(token, JsonHelper.jsonEncode(AuthResponse(AuthStatus.PENDING))))
                                            sendEvent(event)
                                        } else {
                                            coIO {
                                                val clientIp = HttpServerManager.clientIpCache[event.clientId] ?: ""
                                                HttpServerManager.respondTokenAsync(event, clientIp)
                                            }
                                        }
                                    } else {
                                        LogCat.e("ws: invalid_password")
                                        close(CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "invalid_password"))
                                    }
                                } else {
                                    val token = HttpServerManager.tokenCache[clientId]
                                    if (token != null) {
                                        val decryptedBytes = CryptoHelper.aesDecrypt(token, frame.readBytes())
                                        if (decryptedBytes != null) {
                                            LogCat.d("ws: add session ${session.id}, ts: ${decryptedBytes.decodeToString()}")
                                            HttpServerManager.wsSessions.add(session)
                                        } else {
                                            LogCat.d("ws: invalid_request")
                                            close(CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "invalid_request"))
                                        }
                                    } else {
                                        LogCat.d("ws: invalid_request")
                                        close(CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "invalid_request"))
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (ex: Exception) {
                    LogCat.e("ws: $ex")
                } finally {
                    LogCat.d("ws: remove session ${session.id}")
                    HttpServerManager.wsSessions.removeIf { it.id == session.id }
                }
            }
        }
        install(SXGraphQL) {
            init()
        }
    }
}
