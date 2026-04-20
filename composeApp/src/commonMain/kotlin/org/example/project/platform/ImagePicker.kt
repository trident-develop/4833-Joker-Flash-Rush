package org.example.project.platform

import androidx.compose.runtime.Composable

enum class PermissionStatus { Granted, Denied, NotDetermined }

@Composable
expect fun ProfileImagePickerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onImageSelected: (ByteArray?) -> Unit
)
