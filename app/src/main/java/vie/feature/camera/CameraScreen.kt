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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

import vie.feature.navigation.BottomNavigationBar
import vie.feature.navigation.Tab

import vie.core.impl.CameraFrameAdapter
import vie.core.domain.ColorResolver
import vie.core.data.Point

@Composable
fun CameraScreen(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraPermissionState = rememberCameraPermissionState()

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var rgbValue by remember { mutableStateOf("") }
    var hexValue by remember { mutableStateOf("") }
    var colorName by remember { mutableStateOf("") }

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
                    .padding(vertical = 8.dp)
                    .background(Color.Black),
                horizontalArrangement = Arrangement.SpaceBetween
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

            if (cameraPermissionState.hasPermission) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {

                    AndroidView(
                        factory = { ctx ->

                            val previewView = PreviewView(ctx)

                            val cameraProviderFuture =
                                ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({

                                val cameraProvider = cameraProviderFuture.get()

                                val preview = Preview.Builder().build()
                                preview.setSurfaceProvider(previewView.surfaceProvider)

                                val analysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                    )
                                    .build()

                                analysis.setAnalyzer(cameraExecutor) { image ->

                                    val frame = CameraFrameAdapter(image)

                                    val p = Point(
                                        frame.width / 2,
                                        frame.height / 2
                                    )

                                    val name =
                                        ColorResolver.colorName(frame, p)

                                    colorName = name

                                    image.close()
                                }

                                try {

                                    cameraProvider.unbindAll()

                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
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
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray),
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