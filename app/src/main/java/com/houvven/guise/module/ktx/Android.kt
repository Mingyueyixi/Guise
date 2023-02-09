package com.houvven.guise.module.ktx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast

lateinit var toast: Toast
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).run {
        if (::toast.isInitialized) toast.cancel()
        show()
        toast = this
    }
}

fun String.toBitmap(): Bitmap {
    val bytes = Base64.decode(this.split(",")[1], Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}