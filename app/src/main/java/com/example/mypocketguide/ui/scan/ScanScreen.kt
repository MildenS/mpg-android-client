package com.example.mypocketguide.ui.scan

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypocketguide.MyPocketGuideApp
import com.example.mypocketguide.R
import com.example.mypocketguide.domain.Exhibit
import com.example.mypocketguide.ui.common.PrimaryButton
import com.example.mypocketguide.ui.theme.Gray
import com.example.mypocketguide.ui.theme.Ink
import com.example.mypocketguide.ui.theme.White
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    onExhibitRecognized: (Exhibit) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyPocketGuideApp
    val viewModel: ScanViewModel = viewModel(
        factory = ScanViewModelFactory(
            app.container.exhibitRepository,
            app.container.historyRepository
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Переход на экран экспоната при успехе
    LaunchedEffect(state) {
        val s = state
        if (s is ScanUiState.Success) {
            onExhibitRecognized(s.exhibit)
            viewModel.reset()
        }
    }

    // Разрешение камеры
    val cameraPermission = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // Галерея — как fallback
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            readBytes(context, uri)?.let { viewModel.recognize(it) }
        }
    }

    Box(Modifier.fillMaxSize()) {
        when (val s = state) {
            ScanUiState.Idle -> {
                if (cameraPermission.status.isGranted) {
                    CameraContent(
                        onCaptured = { bytes -> viewModel.recognize(bytes) },
                        onPickFromGallery = {
                            pickImage.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                } else {
                    PermissionRequestContent(
                        onRequest = { cameraPermission.launchPermissionRequest() },
                        onPickFromGallery = {
                            pickImage.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                }
            }
            ScanUiState.Loading -> ScanLoading()
            is ScanUiState.Error -> ScanError(
                message = s.message,
                onRetry = { viewModel.reset() }
            )
            is ScanUiState.Success -> ScanLoading()
        }
    }
}

@Composable
private fun CameraContent(
    onCaptured: (ByteArray) -> Unit,
    onPickFromGallery: () -> Unit
) {
    val context = LocalContext.current
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setOutputFormat(ImageCapture.OUTPUT_FORMAT_JPEG)
            .build()
    }

    Box(Modifier.fillMaxSize()) {
        CameraPreview(
            imageCapture = imageCapture,
            modifier = Modifier.fillMaxSize()
        )

        // Кнопки снизу
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Ink.copy(alpha = 0.55f))
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка «Снять»
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        imageCapture.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(
                                    image: androidx.camera.core.ImageProxy
                                ) {
                                    val bytes = image.toJpegByteArray()
                                    image.close()
                                    if (bytes != null) onCaptured(bytes)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    // Игнорируем — пользователь просто нажмёт ещё раз
                                }
                            }
                        )
                    },
                    modifier = Modifier.size(84.dp)
                ) {
                    Icon(
                        Icons.Filled.PhotoCamera,
                        contentDescription = stringResource(R.string.scan_capture),
                        tint = Ink,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onPickFromGallery,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Icon(
                    Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.scan_pick_from_gallery),
                    color = White
                )
            }
        }
    }
}

@Composable
private fun PermissionRequestContent(
    onRequest: () -> Unit,
    onPickFromGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.PhotoCamera,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Gray
        )
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.scan_permission_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.scan_permission_message),
            style = MaterialTheme.typography.bodyLarge,
            color = Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        PrimaryButton(
            text = stringResource(R.string.scan_permission_grant),
            modifier = Modifier.fillMaxWidth(0.85f),
            onClick = onRequest
        )
        Spacer(Modifier.height(12.dp))
        TextButton(
            onClick = onPickFromGallery,
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(stringResource(R.string.scan_pick_from_gallery))
        }
    }
}

@Composable
private fun ScanLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(56.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.scan_recognizing),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScanError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .semantics { liveRegion = LiveRegionMode.Assertive },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.scan_error_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton(
            text = stringResource(R.string.scan_retry),
            modifier = Modifier.fillMaxWidth(0.7f),
            onClick = onRetry
        )
    }
}

private fun readBytes(context: Context, uri: Uri): ByteArray? = try {
    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
} catch (e: Exception) {
    null
}