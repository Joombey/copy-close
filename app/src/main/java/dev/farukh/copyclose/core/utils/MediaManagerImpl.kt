package dev.farukh.copyclose.core.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dev.farukh.copyclose.core.data.models.MediaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

interface MediaManager {
    fun insertToMediaAndGetUri(data: ByteArray, mime: String, filename: String): String?
    fun insertToMediaAndGetUri(stream: InputStream, mime: String, filename: String): String?
    suspend fun getMediaByUri(uri: Uri): ByteArray?
    fun createMedia(mime: String, filename: String): Uri?
    fun getMediaOutStream(uri: Uri): OutputStream?
    fun getMediaInputStream(uri: Uri): InputStream?
    fun getMediaInfo(uri: Uri): MediaInfo?
}

class MediaManagerImpl(private val contentResolver: ContentResolver) : MediaManager {
    override fun insertToMediaAndGetUri(data: ByteArray, mime: String, filename: String): String? {
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

    override fun insertToMediaAndGetUri(stream: InputStream, mime: String, filename: String): String? {
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

    override suspend fun getMediaByUri(uri: Uri) =
        if (uri.scheme != "content") null
        else withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            }
        }


    override fun createMedia(mime: String, filename: String): Uri? {
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

    override fun getMediaOutStream(uri: Uri) = contentResolver.openOutputStream(uri)
    override fun getMediaInputStream(uri: Uri) = contentResolver.openInputStream(uri)

    override fun getMediaInfo(uri: Uri): MediaInfo? {
        return contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
            val mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
            MediaStore.MediaColumns.DATA

            MediaInfo(
                name = cursor.getString(nameIndex)!!,
                size = cursor.getLong(sizeIndex),
                mimeType = cursor.getString(mimeTypeIndex),
                extensions = cursor.getString(nameIndex)!!.split(".").last(),
                uri = uri,
            )
        }
    }
}