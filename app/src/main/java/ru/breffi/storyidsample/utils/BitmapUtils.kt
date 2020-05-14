package ru.breffi.storyidsample.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import kotlin.math.roundToInt

fun Uri.handleSamplingAndRotationBitmap(context: Context): Bitmap {
    val maxHeight = 1024
    val maxWidth = 1024

    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    var imageStream = context.contentResolver.openInputStream(this)
    BitmapFactory.decodeStream(imageStream, null, options)
    imageStream!!.close()

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    imageStream = context.contentResolver.openInputStream(this)
    val img = BitmapFactory.decodeStream(imageStream, null, options) as Bitmap

    return img.rotateImageIfRequired(this)
}

/**
 * Calculate an inSampleSize for use in a [BitmapFactory.Options] object when decoding
 * bitmaps using the decode* methods from [BitmapFactory]. This implementation calculates
 * the closest inSampleSize that will result in the final decoded bitmap having a width and
 * height equal to or larger than the requested width and height. This implementation does not
 * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
 * results in a larger bitmap which isn't as useful for caching purposes.
 *
 * @param options   An options object with out* params already populated (run through a decode*
 * method with inJustDecodeBounds==true
 * @param reqWidth  The requested width of the resulting bitmap
 * @param reqHeight The requested height of the resulting bitmap
 * @return The value to be used for inSampleSize
 */
private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int, reqHeight: Int
): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
        val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()

        // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
        // with both dimensions larger than or equal to the requested height and width.
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

        // This offers some additional logic in case the image has a strange
        // aspect ratio. For example, a panorama may have a much larger
        // width than height. In these cases the total pixels might still
        // end up being too large to fit comfortably in memory, so we should
        // be more aggressive with sample down the image (=larger inSampleSize).

        val totalPixels = (width * height).toFloat()

        // Anything more than 2x the requested pixels we'll sample down further
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
    }
    return inSampleSize
}

/**
 * Rotate an image if required.
 *
 * @param selectedImage Image URI
 * @return The resulted Bitmap after manipulation
 */
@Throws(IOException::class)
private fun Bitmap.rotateImageIfRequired(selectedImage: Uri): Bitmap {

    val ei = ExifInterface(selectedImage.path!!)

    return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(270)
        else -> this
    }
}

private fun Bitmap.rotateImage(degree: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    val rotatedImg = Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    this.recycle()
    return rotatedImg
}
