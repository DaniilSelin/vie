package vie.core.domain

interface ImageFrame {
    fun getBytes(): ByteArray
    val width: Int
    val height: Int
}