package vie.feature.camera

import androidx.camera.core.*
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.view.MotionEvent
import androidx.core.content.ContextCompat

import vie.feature.navigation.BottomNavigationBar
import vie.feature.navigation.Tab
import vie.core.impl.CameraFrameAdapter
import vie.core.domain.ColorResolver
import vie.core.data.Point
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberCameraPermissionState()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var rgbValue by remember { mutableStateOf("") }
    var hexValue by remember { mutableStateOf("") }
    var colorName by remember { mutableStateOf("") }
    var currentColor by remember { mutableStateOf(Color.White) }
    var isFrontCamera by remember { mutableStateOf(false) }

    var cameraSelectorIndex by remember { mutableStateOf(0) }
    var selectedPoint by remember { mutableStateOf<Point?>(null) }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(currentColor)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "RGB: $rgbValue",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "HEX: $hexValue",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (cameraPermissionState.hasPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    key(cameraSelectorIndex) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()

                                    val preview = Preview.Builder().build()
                                    preview.setSurfaceProvider(previewView.surfaceProvider)

                                    val analysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                        .build()

                                    analysis.setAnalyzer(cameraExecutor) { image ->
                                        val frame = CameraFrameAdapter(image)
                                        val point = selectedPoint ?: Point(frame.width / 2, frame.height / 2)
                                        val color = ColorResolver.extractColor(frame, point)
                                        val name = ColorResolver.colorName(frame, point)

                                        val r = color.r;
                                        val g = color.g;
                                        val b = color.b;

                                        android.os.Handler(ctx.mainLooper).post {
                                            rgbValue = "$r, $g, $b"
                                            hexValue = color.hexColorFormat()
                                            colorName = name
                                            currentColor = Color(r, g, b)
                                        }

                                        image.close()
                                    }

                                    try {
                                        cameraProvider.unbindAll()

                                        val cameraSelector = if (isFrontCamera) {
                                            CameraSelector.DEFAULT_FRONT_CAMERA
                                        } else {
                                            CameraSelector.DEFAULT_BACK_CAMERA
                                        }

                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            analysis
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable {
                            // TODO: Открыть галерею
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            // TODO: Сохранить текущий цвет
                        }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .clickable {
                            isFrontCamera = !isFrontCamera
                            cameraSelectorIndex++
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

        BottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}
