package com.ome.app.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.IOException


fun Uri.getRealPathFromUri(context: Context): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(this, proj, null, null, null)
        cursor?.let{
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return@let cursor.getString(columnIndex)
        }
    } finally {
        cursor?.close()
    }
}

fun Context.savePhotoToExternalStorage(name: String, bmp: Bitmap?): Uri? {

    val imageCollection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues().apply {

        put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (bmp != null) {
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }

    }
    return try {
        val uri = this.contentResolver.insert(imageCollection, contentValues)?.also {
            this.contentResolver.openOutputStream(it).use { outputStream ->
                if (bmp != null) {
                    if (!bmp.compress(Bitmap.CompressFormat.PNG, 95, outputStream)) {
                        throw IOException("Failed to save Bitmap")
                    }
                }
            }

        } ?: throw IOException("Failed to create Media Store entry")
        uri
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

}
