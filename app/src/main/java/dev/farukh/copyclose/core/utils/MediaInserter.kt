package dev.farukh.copyclose.core.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.InputStream

class MediaInserter(private val contentResolver: ContentResolver) {
    fun insertToMediaAndGetUri(data: ByteArray, mime: String, filename: String): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        return contentResolver.insert(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else if (mime.split("/").first() == "audio") {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            },
            contentValues
        )?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { fileOutputStream ->
                fileOutputStream.write(data)
            }
            uri.toString()
        }
    }

    fun insertToMediaAndGetUri(stream: InputStream, mime: String, filename: String): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        return contentResolver.insert(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else if (mime.split("/").first() == "audio") {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            },
            contentValues
        )?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { fileOutputStream ->
                stream.copyTo(fileOutputStream, DEFAULT_BUFFER_SIZE)
            }
            uri.toString()
        }
    }

    fun createMedia(mime: String, filename: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        return contentResolver.insert(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else if (mime.split("/").first() == "audio") {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            },
            contentValues
        )
    }

    fun getMediaOutStream(uri: Uri) = contentResolver.openOutputStream(uri)
}