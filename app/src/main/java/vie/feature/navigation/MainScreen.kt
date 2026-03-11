package vie.feature.navigation

import androidx.compose.runtime.*
import vie.feature.camera.CameraScreen
import vie.feature.test.TestScreen
import vie.feature.simulation.SimulationScreen

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(Tab.CAMERA) }

    when (selectedTab) {
        Tab.CAMERA -> CameraScreen(
            selectedTab = selectedTab,
            onTabSelected = { tab -> selectedTab = tab }
        )
        Tab.TEST -> TestScreen(
            selectedTab = selectedTab,
            onTabSelected = { tab -> selectedTab = tab }
        )
        Tab.SIMULATION -> SimulationScreen(
            selectedTab = selectedTab,
            onTabSelected = { tab -> selectedTab = tab }
        )
    }
}

enum class Tab {
    TEST, CAMERA, SIMULATION
}