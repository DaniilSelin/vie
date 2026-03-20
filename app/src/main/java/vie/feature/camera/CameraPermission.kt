package vie.feature.camera

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

@Composable
fun rememberCameraPermissionState(): CameraPermissionState {
    val (hasPermission, setHasPermission) = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        setHasPermission(isGranted)
    }

    return CameraPermissionState(
        hasPermission = hasPermission,
        launchPermissionRequest = { launcher.launch(Manifest.permission.CAMERA) }
    )
}

data class CameraPermissionState(
    val hasPermission: Boolean,
    val launchPermissionRequest: () -> Unit
)