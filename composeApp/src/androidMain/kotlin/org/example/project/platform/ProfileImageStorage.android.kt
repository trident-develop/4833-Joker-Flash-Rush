package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

private const val FILE_NAME = "profile_image.jpg"

@Composable
actual fun rememberProfileImageSaver(): (ByteArray) -> Unit {
    val context = LocalContext.current
    return remember {
        { bytes: ByteArray ->
            File(context.filesDir, FILE_NAME).writeBytes(bytes)
        }
    }
}

@Composable
actual fun loadSavedProfileImage(): ByteArray? {
    val context = LocalContext.current
    return remember {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) file.readBytes() else null
    }
}
