package vie.core.impl

import android.content.Context
import vie.core.data.Color
import vie.core.domain.ColorPalette

class StandartPalette(private val context: Context) : ColorPalette() {

    override fun matchColor(c: Color): String {
        var bestName: String? = null
        var bestDist = Float.MAX_VALUE

        for (n in names) {
            val dr = (c.r - n.r).toFloat()
            val dg = (c.g - n.g).toFloat()
            val db = (c.b - n.b).toFloat()
            val dist = dr*dr + dg*dg + db*db
            val radius2 = n.radius * n.radius

            if (dist <= radius2 && dist < bestDist) {
                bestDist = dist
                bestName = n.name
            }
        }

        return bestName ?: findClosestColor(c)
    }

    private fun findClosestColor(c: Color): String {
        var closestName = "Неизвестный цвет"
        var minDistance = Float.MAX_VALUE

        for (n in names) {
            val dr = (c.r - n.r).toFloat()
            val dg = (c.g - n.g).toFloat()
            val db = (c.b - n.b).toFloat()
            val dist = dr*dr + dg*dg + db*db

            if (dist < minDistance) {
                minDistance = dist
                closestName = n.name
            }
        }

        return closestName
    }

    override fun load(data: String) {
        val colors = JsonPaletteLoader.loadFromAssets(context)
        names.clear()
        names.addAll(colors)
    }
}