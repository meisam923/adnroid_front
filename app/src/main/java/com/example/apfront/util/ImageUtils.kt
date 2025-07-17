package com.example.apfront.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.IOException

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}