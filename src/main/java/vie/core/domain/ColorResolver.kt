package vie.core.domain

import vie.core.data.Color
import vie.core.data.Point

class ColorResolver private constructor() {

    private var palette: ColorPalette? = null

    fun colorName(i: ImageFrame, p: Point): String {
        val color = extractColor(i, p)
        val name = palette?.matchColor(color)
        return name ?: ""
    }

    private fun extractColor(frame: ImageFrame, p: Point): Color {
        val bytes = frame.getBytes(p)

        val r = bytes[0].toInt() and 0xFF
        val g = bytes[1].toInt() and 0xFF
        val b = bytes[2].toInt() and 0xFF
        val a = bytes[3].toInt() and 0xFF

        return Color(r, g, b, a)
    }

    companion object {

        private val instance = ColorResolver()

        fun setPalette(palette: ColorPalette) {
            instance.palette = palette
        }

        fun colorName(frame: ImageFrame, point: Point): String {
            return instance.colorName(frame, point)
        }
    }
}