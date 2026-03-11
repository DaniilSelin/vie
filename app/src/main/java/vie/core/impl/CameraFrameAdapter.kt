package vie.core.impl

import androidx.camera.core.ImageProxy
import vie.core.domain.ImageFrame
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.graphics.Rect
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class CameraFrameAdapter(
    private val image: ImageProxy
) : ImageFrame {

    private var cachedBytes: ByteArray? = null
    private var cachedBitmap: Bitmap? = null

    override val width: Int
        get() = image.width

    override val height: Int
        get() = image.height

    override fun getBytes(): ByteArray {
        if (cachedBytes == null) {
            cachedBytes = convertToByteArray()
        }
        return cachedBytes!!
    }

    fun getBitmap(): Bitmap {
        if (cachedBitmap == null || cachedBitmap?.isRecycled == true) {
            cachedBitmap = image.toBitmap()
        }
        return cachedBitmap!!
    }

    fun getPixelColor(x: Int, y: Int): Int {
        val bitmap = getBitmap()
        return if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
            bitmap.getPixel(x, y)
        } else {
            0
        }
    }

    private fun convertToByteArray(): ByteArray {
        val bitmap = getBitmap()
        val bytes = ByteArray(bitmap.width * bitmap.height * 4)
        var index = 0
        
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                bytes[index++] = (pixel shr 16 and 0xFF).toByte()
                bytes[index++] = (pixel shr 8 and 0xFF).toByte()
                bytes[index++] = (pixel and 0xFF).toByte()
                bytes[index++] = (pixel shr 24 and 0xFF).toByte()
            }
        }
        
        return bytes
    }

    fun release() {
        cachedBitmap?.recycle()
        cachedBitmap = null
        cachedBytes = null
    }
    
    private fun ImageProxy.toBitmap(): Bitmap {
        val yuvBytes = ByteArray(planes[0].buffer.remaining())
        planes[0].buffer.get(yuvBytes)
        
        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val jpegData = out.toByteArray()
        
        return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
    }
}