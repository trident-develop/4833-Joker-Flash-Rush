package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

private const val FILE_NAME = "app_state.txt"

private fun filePath(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
    val dir = paths.first() as String
    return "$dir/$FILE_NAME"
}

@Composable
actual fun rememberAppStateSaver(): (PersistedAppState) -> Unit {
    return remember {
        { state: PersistedAppState ->
            val text = state.serialize()
            @Suppress("CAST_NEVER_SUCCEEDS")
            @OptIn(ExperimentalForeignApi::class)
            (text as NSString).writeToFile(filePath(), atomically = true, encoding = NSUTF8StringEncoding, error = null)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun loadPersistedAppState(): PersistedAppState {
    return remember {
        val path = filePath()
        if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return@remember PersistedAppState()
        val content = NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null)
            ?: return@remember PersistedAppState()
        deserializeAppState(content)
    }
}
