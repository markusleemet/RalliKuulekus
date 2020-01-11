package cs.ut.ee.rallikuulekus.functions

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore.Images
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream



/**
 * Android internals have been modified to store images in the media folder with
 * the correct date meta data
 * @author samuelkirton
 */
object CapturePhotoUtils {
    /**
     * A copy of the Android internals  insertImage method, this method populates the
     * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
     * that is inserted manually gets saved at the end of the gallery (because date is not populated).
     * @see android.provider.MediaStore.Images.Media.insertImage
     */
    fun insertImage(cr: ContentResolver, source: Bitmap?, title: String?, description: String?): String? {
        val values = ContentValues()
        values.put(Images.Media.TITLE, title)
        values.put(Images.Media.DISPLAY_NAME, title)
        values.put(Images.Media.DESCRIPTION, description)
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
        var url: Uri? = null
        var stringUrl: String? = null /* value to be returned */
        try {
            url = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
            if (source != null) {
                val imageOut: OutputStream? = cr.openOutputStream(url!!)
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut)
                } finally {
                    imageOut!!.close()
                }
                val id = ContentUris.parseId(url)
                // Wait until MINI_KIND thumbnail is generated.
                val miniThumb = Images.Thumbnails.getThumbnail(cr, id, Images.Thumbnails.MINI_KIND, null)
                // This is for backward compatibility.
                storeThumbnail(
                    cr,
                    miniThumb,
                    id,
                    50f,
                    50f,
                    Images.Thumbnails.MICRO_KIND
                )
            } else {
                cr.delete(url!!, null, null)
                url = null
            }
        } catch (e: Exception) {
            if (url != null) {
                cr.delete(url, null, null)
                url = null
            }
        }
        if (url != null) {
            stringUrl = url.toString()
        }
        return stringUrl
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     * @see android.provider.MediaStore.Images.Media
     */
    private fun storeThumbnail(
        cr: ContentResolver,
        source: Bitmap,
        id: Long,
        width: Float,
        height: Float,
        kind: Int
    ): Bitmap? { // create the matrix to scale it
        val matrix = Matrix()
        val scaleX = width / source.width
        val scaleY = height / source.height
        matrix.setScale(scaleX, scaleY)
        val thumb = Bitmap.createBitmap(
            source, 0, 0,
            source.width,
            source.height, matrix,
            true
        )
        val values = ContentValues(4)
        values.put(Images.Thumbnails.KIND, kind)
        values.put(Images.Thumbnails.IMAGE_ID, id.toInt())
        values.put(Images.Thumbnails.HEIGHT, thumb.height)
        values.put(Images.Thumbnails.WIDTH, thumb.width)
        val url: Uri? = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values)
        return try {
            val thumbOut: OutputStream? = cr.openOutputStream(url!!)
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut)
            thumbOut!!.close()
            thumb
        } catch (ex: FileNotFoundException) {
            null
        } catch (ex: IOException) {
            null
        }
    }
}