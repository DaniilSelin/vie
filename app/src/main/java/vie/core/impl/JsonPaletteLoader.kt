package vie.core.impl

import android.content.Context
import org.json.JSONArray
import vie.core.domain.ColorName
import java.io.IOException

object JsonPaletteLoader {

    fun loadFromAssets(context: Context, fileName: String = "color_palette.json"): List<ColorName> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            parseJson(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun parseJson(jsonString: String): List<ColorName> {
        val colors = mutableListOf<ColorName>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val name = item.getString("name")
            val r = item.getInt("r")
            val g = item.getInt("g")
            val b = item.getInt("b")
            val radius = if (item.has("radius")) item.getDouble("radius").toFloat() else 60f

            colors.add(ColorName(name, r, g, b, radius))
        }

        return colors
    }
}