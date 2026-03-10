package vie.di

import vie.core.domain.ColorPalette
import vie.core.impl.StandartPalette

class ColorPaletteBinder {
    fun bindDefault(): ColorPalette {
        val implPalette = StandartPalette()
        implPalette.load("")
        return implPalette
    }
}