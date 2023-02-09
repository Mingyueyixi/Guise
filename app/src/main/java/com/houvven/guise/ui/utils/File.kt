package com.houvven.guise.ui.utils

import android.os.Environment
import java.io.File

fun saveFileToDownloadDir(fileName: String, content: String) = runCatching {
    File(
        Environment.getExternalStorageDirectory(), "Download/Guise/$fileName"
    ).also {
        if (!it.exists()) {
            var parent = it.parentFile
            while (parent != null && !parent.exists()) {
                parent.mkdirs()
                parent = parent.parentFile
            }
            it.createNewFile()
        }
        it.writeText(content)
    }
}