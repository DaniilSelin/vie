package vie.core.impl

import androidx.camera.core.ImageProxy
import vie.core.domain.ImageFrame
import vie.core.data.Point

class CameraFrameAdapter(
    private val image: ImageProxy
) : ImageFrame {

    override val width: Int
        get() = image.width

    override val height: Int
        get() = image.height

    override fun getBytes(p: Point): ByteArray {
        val plane = image.planes[0]
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride

        val x = p.x.coerceIn(0, width - 1)
        val y = p.y.coerceIn(0, height - 1)

        val index = y * rowStride + x * pixelStride
        buffer.position(index)
        val bytes = ByteArray(4)
        buffer.get(bytes)
        return bytes
    }
}