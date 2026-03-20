package vie.feature.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationButton(
                text = "Тест",
                isSelected = selectedTab == Tab.TEST,
                onClick = { onTabSelected(Tab.TEST) },
                modifier = Modifier.weight(1f)
            )
            NavigationButton(
                text = "Камера",
                isSelected = selectedTab == Tab.CAMERA,
                onClick = { onTabSelected(Tab.CAMERA) },
                modifier = Modifier.weight(1f)
            )
            NavigationButton(
                text = "Симуляция",
                isSelected = selectedTab == Tab.SIMULATION,
                onClick = { onTabSelected(Tab.SIMULATION) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NavigationButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(2.dp)
                    .background(Color.White)
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}