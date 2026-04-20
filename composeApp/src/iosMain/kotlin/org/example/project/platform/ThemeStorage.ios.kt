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

private const val FILE_NAME = "theme_is_dark.txt"

private fun themeFilePath(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
    val dir = paths.first() as String
    return "$dir/$FILE_NAME"
}

@Composable
actual fun rememberThemeSaver(): (Boolean) -> Unit {
    return remember {
        { isDark: Boolean ->
            val text = if (isDark) "1" else "0"
            @Suppress("CAST_NEVER_SUCCEEDS")
            @OptIn(ExperimentalForeignApi::class)
            (text as NSString).writeToFile(themeFilePath(), atomically = true, encoding = NSUTF8StringEncoding, error = null)
        }
    }
}

@Composable
actual fun loadSavedThemeIsDark(): Boolean? {
    return remember {
        val path = themeFilePath()
        if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return@remember null
        @OptIn(ExperimentalForeignApi::class)
        val content = NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null)
        content?.trim()?.let { it == "1" }
    }
}
