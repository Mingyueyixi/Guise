package com.houvven.guise.util.android

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object UriUtil {

    @JvmStatic
    fun getFilePath(context: Context, contentUri: Uri): String? {
        val rootDir = context.getExternalFilesDir(null)
        val fileName = getFileName(contentUri)
        if (!fileName.isNullOrBlank()) {
            val copyFile = File("${rootDir.toString()}${File.separator}$fileName")
            copyFile(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    @JvmStatic
    fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val path = uri.path
        val cut = path?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    @JvmStatic
    fun copyFile(context: Context, sourceUri: Uri, targetFile: File): Boolean {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return false
            val outputStream: OutputStream = FileOutputStream(targetFile)
            copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    @JvmStatic
    @Throws(Exception::class, IOException::class)
    fun copyStream(input: InputStream?, output: OutputStream?): Int {
        val size = 1024 * 2
        val buffer = ByteArray(size)
        return BufferedInputStream(input, size).use { input ->
            BufferedOutputStream(output, size).use { output ->
                var count = 0
                var n = 0
                while (-1 != input.read(buffer, 0, size).also { n = it }) {
                    output.write(buffer, 0, n)
                    count += n
                }
                output.flush()
                count
            }
        }
    }

    fun getFileRealPath(context: Context, imageUri: Uri): String? {
        if (!DocumentsContract.isDocumentUri(context, imageUri)) {
            return uriToFileApiQ(context, imageUri)
        }

        when {
            isExternalStorageDocument(imageUri) -> {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            }

            isDownloadsDocument(imageUri) -> {
                val id = DocumentsContract.getDocumentId(imageUri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            }

            isMediaDocument(imageUri) -> {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }

        return uriToFileApiQ(context, imageUri)

    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Android 10 以上适配 另一种写法
     *
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("Range")
    private fun getFileFromContentUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val filePath: String
        val filePathColumn =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            uri, filePathColumn, null,
            null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                return filePath
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }
        }
        return ""
    }

    /**
     * Android 10 以上适配
     *
     * @param context
     * @param uri
     * @return
     */
    private fun uriToFileApiQ(context: Context, uri: Uri): String {
        var file: File? = null
        // android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            // 把文件复制到沙盒目录
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor!!.moveToFirst()) {
                @SuppressLint("Range") val displayName = cursor.getString(
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                )
                try {
                    val `is` = contentResolver.openInputStream(uri)
                    val cache = File(
                        context.externalCacheDir!!.absolutePath,
                        Math.round((Math.random() + 1) * 1000).toString() + displayName
                    )
                    val fos = FileOutputStream(cache)
                    FileUtils.copy(`is`!!, fos)
                    file = cache
                    fos.close()
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        assert(file != null)
        return file!!.absolutePath
    }
}