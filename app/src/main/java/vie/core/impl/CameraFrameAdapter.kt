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

    override val width: Int
        get() = image.width

    override val height: Int
        get() = image.height

    override fun getBytes(): ByteArray {

        val bitmap = image.toBitmap()
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
        
        bitmap.recycle()
        return bytes
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