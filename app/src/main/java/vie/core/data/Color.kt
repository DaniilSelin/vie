package vie.core.data

class Color(
    val r: Int,
    val g: Int,
    val b: Int,
    val alpha: Int
) {
    fun hexColorFormat(): String {
        return "#%02X%02X%02X".format(r, g, b)
    }
}