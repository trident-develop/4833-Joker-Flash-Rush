package org.example.project.platform

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.example.project.theme.AppColors
import platform.posix.memcpy
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHPhotoLibrary
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.darwin.NSObject

private var currentImagePickerDelegate: NSObject? = null
private var currentPhPickerDelegate: NSObject? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ProfileImagePickerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onImageSelected: (ByteArray?) -> Unit
) {
    if (!show) return

    var showSheet by remember { mutableStateOf(true) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var settingsMessage by remember { mutableStateOf("") }

    fun finish(bytes: ByteArray?) {
        if (bytes != null) onImageSelected(bytes)
        showSheet = false
        onDismiss()
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false; showSheet = true },
            title = { Text("Permission Required", color = AppColors.TextPrimary) },
            text = { Text(settingsMessage, color = AppColors.TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    openAppSettings()
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
                            handleCameraAccess(
                                onGranted = {
                                    launchCamera { bytes ->
                                        NSOperationQueue.mainQueue.addOperationWithBlock {
                                            finish(bytes)
                                        }
                                    }
                                },
                                onDenied = {
                                    settingsMessage = "Camera access is required to take photos. Enable it in Settings."
                                    showSettingsDialog = true
                                }
                            )
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
                            handleGalleryAccess(
                                onGranted = {
                                    launchGallery { bytes ->
                                        NSOperationQueue.mainQueue.addOperationWithBlock {
                                            finish(bytes)
                                        }
                                    }
                                },
                                onDenied = {
                                    settingsMessage = "Photo library access is required. Enable it in Settings."
                                    showSettingsDialog = true
                                }
                            )
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

private fun handleCameraAccess(onGranted: () -> Unit, onDenied: () -> Unit) {
    when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
        AVAuthorizationStatusAuthorized -> onGranted()
        AVAuthorizationStatusNotDetermined -> {
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                NSOperationQueue.mainQueue.addOperationWithBlock {
                    if (granted) onGranted() else onDenied()
                }
            }
        }
        AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> onDenied()
        else -> onDenied()
    }
}

private fun handleGalleryAccess(onGranted: () -> Unit, onDenied: () -> Unit) {
    when (PHPhotoLibrary.authorizationStatus()) {
        PHAuthorizationStatusAuthorized, PHAuthorizationStatusLimited -> onGranted()
        PHAuthorizationStatusNotDetermined -> {
            PHPhotoLibrary.requestAuthorization { newStatus ->
                NSOperationQueue.mainQueue.addOperationWithBlock {
                    if (newStatus == PHAuthorizationStatusAuthorized || newStatus == PHAuthorizationStatusLimited) {
                        onGranted()
                    } else {
                        onDenied()
                    }
                }
            }
        }
        PHAuthorizationStatusDenied, PHAuthorizationStatusRestricted -> onDenied()
        else -> onDenied()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun launchCamera(onResult: (ByteArray?) -> Unit) {
    val vc = ViewControllerHolder.topViewController() ?: run { onResult(null); return }

    val picker = UIImagePickerController()
    picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera

    val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            val data = image?.let { UIImageJPEGRepresentation(it, 0.8) }
            val bytes = data?.let { nsData ->
                ByteArray(nsData.length.toInt()).also { arr ->
                    arr.usePinned { pinned ->
                        memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                    }
                }
            }
            picker.dismissViewControllerAnimated(true, null)
            currentImagePickerDelegate = null
            onResult(bytes)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
            currentImagePickerDelegate = null
            onResult(null)
        }
    }
    currentImagePickerDelegate = delegate
    picker.delegate = delegate
    vc.presentViewController(picker, animated = true, completion = null)
}

private fun launchGallery(onResult: (ByteArray?) -> Unit) {
    val vc = ViewControllerHolder.topViewController() ?: run { onResult(null); return }

    val config = PHPickerConfiguration()
    config.filter = PHPickerFilter.imagesFilter
    config.selectionLimit = 1

    val picker = PHPickerViewController(configuration = config)

    val delegate = object : NSObject(), PHPickerViewControllerDelegateProtocol {
        @OptIn(ExperimentalForeignApi::class)
        override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
            picker.dismissViewControllerAnimated(true, null)
            val result = didFinishPicking.firstOrNull() as? PHPickerResult
            val provider = result?.itemProvider
            if (provider != null && provider.hasItemConformingToTypeIdentifier("public.image")) {
                provider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
                    val bytes = data?.let { nsData ->
                        ByteArray(nsData.length.toInt()).also { arr ->
                            arr.usePinned { pinned ->
                                memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                            }
                        }
                    }
                    NSOperationQueue.mainQueue.addOperationWithBlock {
                        currentPhPickerDelegate = null
                        onResult(bytes)
                    }
                }
            } else {
                currentPhPickerDelegate = null
                onResult(null)
            }
        }
    }
    currentPhPickerDelegate = delegate
    picker.delegate = delegate
    vc.presentViewController(picker, animated = true, completion = null)
}

private fun openAppSettings() {
    val url = NSURL.URLWithString("app-settings:")
    if (url != null) {
        UIApplication.sharedApplication.openURL(url)
    }
}
