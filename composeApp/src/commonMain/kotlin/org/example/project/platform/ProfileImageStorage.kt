package org.example.project.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberProfileImageSaver(): (ByteArray) -> Unit

@Composable
expect fun loadSavedProfileImage(): ByteArray?
