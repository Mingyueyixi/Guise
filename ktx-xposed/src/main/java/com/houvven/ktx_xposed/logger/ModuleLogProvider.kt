package com.houvven.ktx_xposed.logger

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class ModuleLogProvider : ContentProvider() {

    private val uriMatcher by lazy {
        UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI("com.houvven.xposed.runtime.log", "module_log", 1)
        }
    }


    override fun onCreate(): Boolean = context?.let { ModuleLogDBHelper.init(it); true } ?: false

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null || uriMatcher.match(uri) != 1) return null
        values.let {
            val moduleLog = ModuleLog(
                time = System.currentTimeMillis(),
                type = it.getAsString("type").toCharArray()[0],
                source = it.getAsString("source"),
                message = it.getAsString("message")
            )
            Thread {
                ModuleLogDBHelper.moduleLogDao.insert(moduleLog)
            }.start()
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        TODO("Not yet implemented")
    }

}