package com.example.storyapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
private const val MAXIMAL_SIZE = 1000000

fun getImageUri(context: Context): Uri {
    val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    } else {
        null
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(picturesDir, "/$timeStamp.jpg")
    imageFile.parentFile?.takeIf { !it.exists() }?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun createCustomTempFile(context: Context): File {
    val cacheDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", cacheDir)
}

fun uriToFile(uri: Uri, context: Context): File? {
    return try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val tempFile = File(context.cacheDir, "temp_file")
        parcelFileDescriptor?.use {
            FileInputStream(it.fileDescriptor).use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        tempFile
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}

fun File.reduceFileImage(): File {
    val bitmap = BitmapFactory.decodeFile(this.path)
    var compressQuality = 100
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        compressQuality -= 5
    } while (bmpPicByteArray.size > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
    return this
}