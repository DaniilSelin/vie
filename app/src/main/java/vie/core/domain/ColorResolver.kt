package vie.core.domain

import vie.core.data.Color
import vie.core.data.Point

class ColorResolver {

    private var palette: ColorPalette? = null

    fun setPalette(p: ColorPalette) {
        palette = p
    }

    fun colorName(i: ImageFrame, p: Point): String {
        val color = extractColor(i, p)

        val name = palette?.matchColor(color)
        return name
    }

    private fun extractColor(frame: ImageFrame, p: Point): Color {

        val bytes = frame.getBytes()
        val width = frame.width

        val x = p.x.coerceIn(0, width - 1)
        val y = p.y.coerceIn(0, frame.height - 1)

        val pixelIndex = (y * width + x) * 4

        val r = bytes[pixelIndex].toInt() and 0xFF
        val g = bytes[pixelIndex + 1].toInt() and 0xFF
        val b = bytes[pixelIndex + 2].toInt() and 0xFF
        val a = bytes[pixelIndex + 3].toInt() and 0xFF

        return Color(r, g, b, a)
    }
}