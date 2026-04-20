package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.posix.memcpy

private const val FILE_NAME = "profile_image.jpg"

private fun profileImagePath(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
    val dir = paths.first() as String
    return "$dir/$FILE_NAME"
}

@Composable
actual fun rememberProfileImageSaver(): (ByteArray) -> Unit {
    return remember {
        { bytes: ByteArray ->
            @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
            val nsData = bytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
            }
            nsData.writeToFile(profileImagePath(), atomically = true)
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun loadSavedProfileImage(): ByteArray? {
    return remember {
        val path = profileImagePath()
        if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return@remember null
        val nsData = NSData.create(contentsOfFile = path) ?: return@remember null
        ByteArray(nsData.length.toInt()).also { arr ->
            arr.usePinned { pinned ->
                memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
            }
        }
    }
}
