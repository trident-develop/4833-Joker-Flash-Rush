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
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

private const val FILE_NAME = "user_name.txt"

private fun userNamePath(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
    val dir = paths.first() as String
    return "$dir/$FILE_NAME"
}

@Composable
actual fun rememberUserNameSaver(): (String) -> Unit {
    return remember {
        { name: String ->
            @Suppress("CAST_NEVER_SUCCEEDS")
            @OptIn(ExperimentalForeignApi::class)
            (name as NSString).writeToFile(userNamePath(), atomically = true, encoding = NSUTF8StringEncoding, error = null)
        }
    }
}

@Composable
actual fun loadSavedUserName(): String? {
    return remember {
        val path = userNamePath()
        if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return@remember null
        @OptIn(ExperimentalForeignApi::class)
        NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null)?.takeIf { it.isNotBlank() }
    }
}
