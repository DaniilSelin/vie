package vie.feature.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import vie.feature.navigation.BottomNavigationBar
import vie.feature.navigation.Tab

@Composable
fun CameraScreen(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraPermissionState = rememberCameraPermissionState()

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // ВРЕМЕННЫЕ ЗНАЧЕНИЯ - ЗАМЕНИТЬ НА ЛОГИКУ ОПРЕДЕЛЕНИЯ ЦВЕТА
    val rgbValue = "128 128 128"
    val hexValue = "#808080"
    val colorName = "Светло-серый"

    // Запрос разрешения при первом запуске
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.hasPermission) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
        ) {
            // Верхняя панель с RGB и HEX
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Black),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RGB: $rgbValue",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = "HEX: $hexValue",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            // Камера
            if (cameraPermissionState.hasPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    // PreviewView для камеры
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()

                                    // Preview
                                    val preview = Preview.Builder().build()
                                    preview.surfaceProvider = surfaceProvider

                                    // Используем заднюю камеру по умолчанию
                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Квадрат для определения цвета (25x25 dp)
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.Center)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(0.dp)
                            )
                    )
                }
            } else {
                // Заглушка если нет разрешения
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Нет доступа к камере",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Нажмите чтобы разрешить",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        )
                    }
                }
            }

            // Название цвета
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = colorName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Кнопки управления камерой
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка галереи
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable {
                            // ЛОГИКА ОТКРЫТИЯ ГАЛЕРЕИ
                        }
                )

                // Кнопка сделать фото
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            // ЛОГИКА СЪЕМКИ ФОТО
                        }
                )

                // Кнопка разворота камеры
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .clickable {
                            // ЛОГИКА ПЕРЕКЛЮЧЕНИЯ КАМЕРЫ
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↻",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }

        // Нижняя панель навигации
        BottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Освобождаем ресурсы при уничтожении
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}