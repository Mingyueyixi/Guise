package com.houvven.guise.xposed.other

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.utils.getTypeArgIndexOfFirst
import com.houvven.ktx_xposed.hook.beforeHookAllMethods
import com.houvven.ktx_xposed.utils.setNullResult

class BlankPass : LoadPackageHandler {

    override fun onHook() {
        mapOf(
            config.passAudio to MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            config.passVideo to MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            config.passPhoto to MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            config.passContacts to ContactsContract.Contacts.CONTENT_URI
        ).forEach { (enable, uri) ->
            if (enable) contentResolverQuery(uri)
        }
    }

    private fun contentResolverQuery(uri: Uri) {
        ContentResolver::class.java.beforeHookAllMethods("query") { param ->
            val index = param.getTypeArgIndexOfFirst(Uri::class.java)
            if (index == -1 || uri != param.args[index]) return@beforeHookAllMethods
            param.setNullResult()
        }
    }


}