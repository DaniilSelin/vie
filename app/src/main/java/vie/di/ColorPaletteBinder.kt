package vie.di

import android.content.Context
import vie.core.domain.ColorResolver
import vie.core.impl.StandartPalette

class ColorPaletteBinder {

    fun bindDefault(context: Context) {
        val implPalette = StandartPalette(context)
        implPalette.load("")
        ColorResolver.setPalette(implPalette)
    }
}