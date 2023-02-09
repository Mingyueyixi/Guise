package com.houvven.guise.util.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import com.houvven.guise.ContextAmbient


@SuppressLint("StaticFieldLeak")
object IntentUtils {

    private val context = ContextAmbient.current

    const val FILE_TYPE_JSON = "application/json"
    const val FILE_TYPE_ZIP = "application/zip"
    const val FILE_TYPE_APK = "application/vnd.android.package-archive"
    const val FILE_TYPE_IMAGE = "image/*"
    const val FILE_TYPE_AUDIO = "audio/*"
    const val FILE_TYPE_VIDEO = "video/*"
    const val FILE_TYPE_TEXT = "text/plain"

    fun openBrowser(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = uri
        context.startActivity(intent)
    }


    fun openEmail(email: String) {
        val uri = Uri.parse("mailto:$email")
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = uri
        context.startActivity(intent)
    }

    fun openFileChooser(type: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = type
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        context.startActivity(intent)
    }

    fun buildFileChooserIntent(type: String): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = type
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }

    fun buildFolderChooserIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

}