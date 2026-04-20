package org.example.project.platform

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeImageBytes(bytes: ByteArray): ImageBitmap?
