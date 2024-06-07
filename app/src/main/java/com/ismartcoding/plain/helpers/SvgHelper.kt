package com.ismartcoding.plain.helpers

import com.caverock.androidsvg.SVG
import java.io.File
import androidx.compose.ui.unit.IntSize
import com.ismartcoding.lib.logcat.LogCat

object SvgHelper {
    fun getSize(path:String): IntSize {
        try {
            val svg = SVG.getFromInputStream(File(path).inputStream())
            var width = svg.documentWidth.toInt()
            var height = svg.documentHeight.toInt()
            if (width <= 0) {
                width = 150
            }
            if (height <= 0) {
                height = 150
            }
            return IntSize(width, height)
        } catch (e: Exception) {
            LogCat.e(e.toString())
        }
        return IntSize(150, 150)
    }
}