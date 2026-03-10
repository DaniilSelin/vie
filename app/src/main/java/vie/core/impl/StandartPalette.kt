package vie.core.impl

import vie.core.data.Color
import vie.core.domain.ColorPalette
import vie.core.domain.ColorName

class StandartPalette : ColorPalette() {


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

        return bestName ?: ""
    }

    override fun load(data: String) {

        names.clear()

        names.add(ColorName("Black", 0, 0, 0, 80f))
        names.add(ColorName("White", 255, 255, 255, 80f))
        names.add(ColorName("Red", 255, 0, 0, 120f))
        names.add(ColorName("Green", 0, 255, 0, 120f))
        names.add(ColorName("Blue", 0, 0, 255, 120f))

        names.add(ColorName("Yellow", 255, 255, 0, 120f))
        names.add(ColorName("Cyan", 0, 255, 255, 120f))
        names.add(ColorName("Magenta", 255, 0, 255, 120f))

        names.add(ColorName("Orange", 255, 165, 0, 120f))
        names.add(ColorName("Purple", 128, 0, 128, 120f))
        names.add(ColorName("Pink", 255, 192, 203, 120f))
        names.add(ColorName("Brown", 150, 75, 0, 120f))
    }
}