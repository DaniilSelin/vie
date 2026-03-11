package vie.di

import vie.core.domain.ColorPalette
import vie.core.impl.StandartPalette
import vie.core.domain.ColorResolver

class ColorPaletteBinder {
    fun bindDefault() {
        val implPalette = StandartPalette()
        implPalette.load("")
        ColorResolver.setPalette(implPalette)
    }
}