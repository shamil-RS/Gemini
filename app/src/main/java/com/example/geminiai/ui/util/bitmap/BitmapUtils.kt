package com.example.geminiai.ui.util.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.MediaStore
import java.io.IOException

internal fun loadBitmapWithCorrectOrientation(context: Context, uri: Uri): Bitmap? {
    return try {
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exifInterface = ExifInterface(inputStream)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL,
                )

                if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                    return@use bitmap
                }

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
                    ExifInterface.ORIENTATION_TRANSPOSE -> {
                        matrix.postRotate(90f)
                        matrix.preScale(-1.0f, 1.0f)
                    }

                    ExifInterface.ORIENTATION_TRANSVERSE -> {
                        matrix.postRotate(-90f)
                        matrix.preScale(-1.0f, 1.0f)
                    }
                }

                bitmap?.let {
                    Bitmap.createBitmap(
                        it, 0, 0, it.width, it.height, matrix, true,
                    )
                }
            } ?: bitmap
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
