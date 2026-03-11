package vie.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import vie.app.ui.theme.VieTheme
import vie.feature.navigation.MainScreen
import vie.di.ColorPaletteBinder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ColorPaletteBinder().bindDefault()

        super.onCreate(savedInstanceState)
        setContent {
            VieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen()
                }
            }
        }
    }
}