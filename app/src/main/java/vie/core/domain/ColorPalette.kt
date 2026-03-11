package vie.core.domain

import vie.core.data.Color

abstract class ColorPalette {

    protected val names: MutableList<ColorName> = mutableListOf()

    abstract fun matchColor(c: Color): String

    open fun load(data: String) {}
}

class ColorName(
    val name: String,
    val r: Int,
    val g: Int,
    val b: Int,
    val radius: Float
)