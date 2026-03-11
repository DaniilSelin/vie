package vie.feature.camera

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
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

    var selectorOffset by remember { mutableStateOf(Offset.Zero) }
    var selectorSize by remember { mutableStateOf(50) }
    var previewViewSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    val density = LocalDensity.current
    val selectorSizePx = with(density) { selectorSize.dp.toPx() }

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

                                    val centerX = if (selectorOffset.x == 0f) {
                                        frame.width / 2
                                    } else {
                                        (selectorOffset.x / previewViewSize.width * frame.width).toInt()
                                            .coerceIn(0, frame.width - 1)
                                    }

                                    val centerY = if (selectorOffset.y == 0f) {
                                        frame.height / 2
                                    } else {
                                        (selectorOffset.y / previewViewSize.height * frame.height).toInt()
                                            .coerceIn(0, frame.height - 1)
                                    }

                                    val p = Point(centerX, centerY)

                                    val name = ColorResolver.colorName(frame, p)
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
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { size ->
                                previewViewSize = androidx.compose.ui.geometry.Size(
                                    width = size.width.toFloat(),
                                    height = size.height.toFloat()
                                )
                                if (selectorOffset == Offset.Zero) {
                                    selectorOffset = Offset(
                                        x = size.width / 2f,
                                        y = size.height / 2f
                                    )
                                }
                            }
                    )

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (selectorOffset.x - selectorSizePx / 2).toInt(),
                                    (selectorOffset.y - selectorSizePx / 2).toInt()
                                )
                            }
                            .size(selectorSize.dp)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(0.dp)
                            )
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        selectorOffset = Offset(
                                            x = (selectorOffset.x + dragAmount.x)
                                                .coerceIn(selectorSizePx / 2, previewViewSize.width - selectorSizePx / 2),
                                            y = (selectorOffset.y + dragAmount.y)
                                                .coerceIn(selectorSizePx / 2, previewViewSize.height - selectorSizePx / 2)
                                        )
                                    }
                                )
                            }
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