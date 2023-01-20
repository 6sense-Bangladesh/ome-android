package com.ome.app.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.ome.Ome.BuildConfig
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


fun Uri.convertToFile(context: Context): File {
    val cacheDir = Uri.fromFile(context.cacheDir)

//                if (imageUri.toString().contains(cacheDir.toString())) {
//                    //File is already in cache dir, no need to copy
//                    File(imageUri.path)
//                }
//
//    val parcelFileDescriptor =
//        context.contentResolver.openFileDescriptor(this, "r", null)



        return if (this.toString().contains(cacheDir.toString())) {
            //File is already in cache dir, no need to copy
            File(this.path)
        } else {
            //val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val inputStream = context.contentResolver.openInputStream(this)
            val filename = context.contentResolver.getFileName(this)
            val file = File(context.cacheDir, filename)
            inputStream?.copyTo(file)
            inputStream?.close()
            file
        }

}


fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }

    return name
}


private fun InputStream.copyTo(file: File) {
    use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

fun Context.getTmpFileUri(fileName: String): Uri {
    val tmpFile =
        File.createTempFile(fileName, ".png", this.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

//file:///data/user/0/com.ome.Ome/cache/shaft1081006548749104418.png
    val uri = Uri.fromFile(tmpFile)


    //content://com.ome.Ome.provider/cache/shaft1081006548749104418.png  - FileProvider uri


//file:///data/user/0/com.ome.Ome/cache  - cacheDir


    return FileProvider.getUriForFile(
        this.applicationContext,
        "${BuildConfig.APPLICATION_ID}.provider",
        tmpFile
    )
}
