package com.ismartcoding.plain.helpers

import android.util.Base64
import com.ismartcoding.lib.extensions.getFilenameExtension
import com.ismartcoding.lib.helpers.CryptoHelper
import com.ismartcoding.lib.helpers.NetworkHelper
import com.ismartcoding.plain.TempData

object UrlHelper {
    private val mediaPathMap = mutableMapOf<String, String>() // format: <short_path>:<raw_path>

    fun getMediaHttpUrl(path: String): String {
        // cast screen only only supports http in local network and some TV OS only supports simple file name with extension
        val id = System.currentTimeMillis().toString()
        mediaPathMap[id] = path
        val extension = path.getFilenameExtension()
        return "http://${NetworkHelper.getDeviceIP4()}:${TempData.httpPort}/media/$id.$extension"
    }

    fun getCastCallbackUrl(): String {
        return "http://${NetworkHelper.getDeviceIP4()}:${TempData.httpPort}/callback/cast"
    }

    fun getMediaPath(id: String): String {
        return mediaPathMap[id] ?: ""
    }

    fun decrypt(id: String): String {
        val bytes = Base64.decode(id, Base64.NO_WRAP)
        return CryptoHelper.aesDecrypt(TempData.urlToken, bytes)?.decodeToString() ?: ""
    }
}
