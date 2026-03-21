package vie.core.domain

import vie.core.data.Point

interface ImageFrame {
    fun getBytes(p: Point): ByteArray
    val width: Int
    val height: Int
}