package vie.core.impl

import androidx.camera.core.ImageProxy
import vie.core.domain.ImageFrame

class CameraFrameAdapter(
    private val image: ImageProxy
) : ImageFrame {

    override val width: Int
        get() = image.width

    override val height: Int
        get() = image.height

    override fun getBytes(): ByteArray {

        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        return bytes
    }
}