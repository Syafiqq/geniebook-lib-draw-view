package com.divyanshu.draw.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.InputStream

object ImageUtil {
    @JvmStatic
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (height / inSampleSize > reqHeight && width / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    @JvmStatic
    fun decodeSampledBitmapFromStream(image: InputStream, reqWidth: Int, reqHeight: Int): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val image = if(image.markSupported()) image else BufferedInputStream(image)
        image.mark(1)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(image, null, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        image.reset()
        val bitmap = BitmapFactory.decodeStream(image, null, options)
        image.close()
        return bitmap
    }
}