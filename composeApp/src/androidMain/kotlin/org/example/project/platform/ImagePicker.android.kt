package org.example.project.platform

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.project.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ProfileImagePickerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onImageSelected: (ByteArray?) -> Unit
) {
    if (!show) return

    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(true) }
    var showCameraScreen by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var settingsMessage by remember { mutableStateOf("") }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    fun finish(bytes: ByteArray?) {
        if (bytes != null) onImageSelected(bytes)
        showSheet = false
        showCameraScreen = false
        onDismiss()
    }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val bytes = uri?.let { context.contentResolver.openInputStream(it)?.readBytes() }
        finish(bytes)
    }

    // Camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCameraScreen = true
        } else {
            val activity = context as? Activity
            if (activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                settingsMessage = "Camera permission was permanently denied. Please enable it in Settings to take photos."
                showSettingsDialog = true
            } else {
                settingsMessage = "Camera permission is required to take photos."
                showSettingsDialog = true
            }
        }
    }

    // Gallery permission (pre-API 33)
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            settingsMessage = "Storage permission is required to access photos."
            showSettingsDialog = true
            showSheet = true
        }
    }

    // Full-screen CameraX in a Dialog overlay (above Scaffold / bottom bar)
    if (showCameraScreen) {
        Dialog(
            onDismissRequest = {
                showCameraScreen = false
                showSheet = true
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            CameraScreen(
                onImageCaptured = { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                    finish(bytes)
                },
                onCancel = {
                    showCameraScreen = false
                    showSheet = true
                }
            )
        }
    }

    // Rationale dialog
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false; showSheet = true },
            title = { Text("Permission Needed", color = AppColors.TextPrimary) },
            text = { Text("Camera access is needed to take a profile photo. Grant permission?", color = AppColors.TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    pendingAction?.invoke()
                }) { Text("Grant", color = AppColors.Amber) }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false; showSheet = true }) {
                    Text("Cancel", color = AppColors.TextMuted)
                }
            },
            containerColor = AppColors.CardSurface
        )
    }

    // Settings dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false; showSheet = true },
            title = { Text("Permission Required", color = AppColors.TextPrimary) },
            text = { Text(settingsMessage, color = AppColors.TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                    onDismiss()
                }) { Text("Open Settings", color = AppColors.Amber) }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false; showSheet = true }) {
                    Text("Cancel", color = AppColors.TextMuted)
                }
            },
            containerColor = AppColors.CardSurface
        )
    }

    // Bottom sheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false; onDismiss() },
            sheetState = rememberModalBottomSheetState(),
            containerColor = AppColors.Espresso
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text("Change Profile Photo", color = AppColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))

                // Camera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppColors.CardSurface)
                        .clickable {
                            showSheet = false
                            val permission = Manifest.permission.CAMERA
                            when {
                                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                                    showCameraScreen = true
                                }
                                (context as? Activity)?.let {
                                    ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
                                } == true -> {
                                    pendingAction = { cameraPermissionLauncher.launch(permission) }
                                    showRationaleDialog = true
                                }
                                else -> {
                                    cameraPermissionLauncher.launch(permission)
                                }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.CameraAlt, "Camera", tint = AppColors.Amber, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Take Photo", color = AppColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("Use camera to capture a new photo", color = AppColors.TextMuted, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Gallery
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppColors.CardSurface)
                        .clickable {
                            showSheet = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } else {
                                val perm = Manifest.permission.READ_EXTERNAL_STORAGE
                                if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
                                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                } else {
                                    galleryPermissionLauncher.launch(perm)
                                }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Image, "Gallery", tint = AppColors.BurntOrange, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Choose from Gallery", color = AppColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("Select an existing photo", color = AppColors.TextMuted, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
