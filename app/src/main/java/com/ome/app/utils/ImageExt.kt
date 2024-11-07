@file:Suppress("unused")

package com.ome.app.utils

import android.R.attr.defaultValue
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.target
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer

typealias Base64Img = String

fun Base64Img.getBitmap(): Bitmap {
//    val encodedString = "data:image/jpg;base64, ....";
    val pureBase64Encoded = this.substring(this.indexOf(",") + 1)
    val decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
    val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    return decodedBitmap
}

fun String?.getDrawable(success: (Drawable?) -> Unit) {
    MainScope().launch(Dispatchers.IO) {
        try {
            val `is` = URL(this@getDrawable).content as InputStream
            val drawable = Drawable.createFromStream(`is`, "dp")
            //val x = BitmapFactory.decodeStream(`is`)
            withContext(Dispatchers.Main) {
                success.invoke(drawable)
            }
        } catch (e: Exception) {
            Log.d("getDrawable", "$e")
            withContext(Dispatchers.Main) {
                success.invoke(null)
            }
        }
    }
}

fun Context.getBitmap(drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

/**
 * Set the data to load.
 *
 * The default supported data types are:
 * - [String] (mapped to a [Uri])
 * - [Uri] ("android.resource", "content", "file", "http", and "https" schemes only)
 * - [HttpUrl]
 * - [File]
 * - [DrawableRes]
 * - [Drawable]
 * - [Bitmap]
 * - [ByteArray]
 * - [ByteBuffer]
 */
fun ImageView.loadDrawable(data: Any?, @DrawableRes defaultSrc: Int? = null) {
    val request = ImageRequest.Builder(this.context)
//        .transformations(CircleCropTransformation())
//        .placeholder(defaultSrc ?: R.drawable.ic_profile_small)
//        .error(defaultSrc ?: R.drawable.ic_profile_small)
        .crossfade(true)
//        .httpHeaders(NetworkHeaders.Builder().add("Cache-Control", "max-age=31536000")
        .data(data)
        .target(this)
        .build()

    context.imageLoader.enqueue(request)
}

fun ImageView.loadWithFallback(
    data: Any?,
    fallbackData: Any?,
    @DrawableRes defaultSrc: Int? = null
) {
    val context = this.context
    val imageLoader = context.imageLoader

    // First image request
    val primaryRequest = ImageRequest.Builder(context)
        .data(data)
//        .placeholder(defaultSrc ?: R.drawable.ic_profile_small)
        .target(
            onSuccess = { drawable ->
                this.setImageDrawable(drawable as Drawable) // Set the first image if successful
            },
            onError = {
                // If the first image fails, load the fallback image
                val fallbackRequest = ImageRequest.Builder(context)
                    .data(fallbackData)
//                    .placeholder(defaultSrc ?: R.drawable.ic_profile_small)
//                    .error(defaultSrc ?: R.drawable.ic_profile_small) // Fallback to default if second image fails
                    .target(this)
                    .build()

                imageLoader.enqueue(fallbackRequest)
            }
        )
        .build()

    imageLoader.enqueue(primaryRequest)
}



/**
 * Set the data to load.
 *
 * The default supported data types are:
 * - [String] (mapped to a [Uri])
 * - [Uri] ("android.resource", "content", "file", "http", and "https" schemes only)
 * - [HttpUrl]
 * - [File]
 * - [DrawableRes]
 * - [Drawable]
 * - [Bitmap]
 * - [ByteArray]
 * - [ByteBuffer]
 */
fun Context?.saveImage(data: Any?) {
    if (this == null || data == null) return
    CoroutineScope(Dispatchers.Default).launch {
        val fileName = "IMG_" + System.currentTimeMillis().toTimeDate() + ".png"
        val bitmap = data.toBitmap(this@saveImage)
        val result = saveImageToMediaStore(fileName, bitmap)
        withContext(Dispatchers.Main) {
            if (result != null)
                this@saveImage.toast("Image Saved")
            else
                this@saveImage.toast("Image Save Failed")
        }
    }
}

/**
 * Set the data to load.
 *
 * The default supported data types are:
 * - [String] (mapped to a [Uri])
 * - [Uri] ("android.resource", "content", "file", "http", and "https" schemes only)
 * - [HttpUrl]
 * - [File]
 * - [DrawableRes]
 * - [Drawable]
 * - [Bitmap]
 * - [ByteArray]
 * - [ByteBuffer]
 */
suspend fun Any.toBitmap(context: Context): Bitmap {
    return withContext(Dispatchers.IO){
        val request = ImageRequest.Builder(context)
            .data(this@toBitmap)
            .build()
        val drawable = context.imageLoader.execute(request).image
        (drawable as BitmapDrawable).bitmap
    }
}
fun File.toBitmap(): Bitmap {
    val bitmap = BitmapFactory.decodeFile(this.absolutePath)
    val exif = ExifInterface(absolutePath)
    val orientation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    // Apply the necessary rotation
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotateBitmap(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotateBitmap(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotateBitmap(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> bitmap.flipBitmap(
            horizontal = true,
            vertical = false
        )

        ExifInterface.ORIENTATION_FLIP_VERTICAL -> bitmap.flipBitmap(
            horizontal = false,
            vertical = true
        )

        else -> bitmap
    }
}

// Helper function to rotate the bitmap
fun Bitmap.rotateBitmap(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

// Helper function to flip the bitmap
fun Bitmap.flipBitmap(horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix().apply {
        postScale(
            if (horizontal) -1f else 1f,
            if (vertical) -1f else 1f,
            width / 2f,
            height / 2f
        )
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun File.getImageSize(): Pair<Int, Int> = Pair(imageWidth, imageHeight)

val File.imageHeight: Int
    get() = ExifInterface(absolutePath).getAttributeInt(
        ExifInterface.TAG_IMAGE_LENGTH,
        defaultValue
    )

val File.imageWidth: Int
    get() = ExifInterface(absolutePath).getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, defaultValue)


@Suppress("DEPRECATION")
val webp: Bitmap.CompressFormat
    get() =
        if (Build.VERSION.SDK_INT >= 30)
            Bitmap.CompressFormat.WEBP_LOSSY
        else
            Bitmap.CompressFormat.WEBP

fun Context.saveImageToMediaStore(displayName: String, bitmap: Bitmap): Uri? {
    val imageCollections = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val imageDetails = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = applicationContext.contentResolver
    val imageContentUri = resolver.insert(imageCollections, imageDetails) ?: return null

    return try {
        resolver.openOutputStream(imageContentUri, "w")?.use { os ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageContentUri, imageDetails, null, null)
        }

        imageContentUri
    } catch (e: FileNotFoundException) {
        // Some legacy devices won't create directory for the Uri if dir not exist, resulting in
        // a FileNotFoundException. To resolve this issue, we should use the File API to save the
        // image, which allows us to create the directory ourselves.

        null
    }
}

fun Context.uriToFile(uri: Uri): File? {
    if (uri.scheme == "file") {
        val filePath = uri.path
        return filePath?.let { File(it) }
    } else {
        val cursor =
            contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
        val filePath = idx?.let { cursor.getString(it) }
        cursor?.close()
        return filePath?.let { File(it) }
    }

}
