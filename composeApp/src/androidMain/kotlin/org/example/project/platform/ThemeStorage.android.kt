package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

private const val FILE_NAME = "theme_is_dark.txt"

@Composable
actual fun rememberThemeSaver(): (Boolean) -> Unit {
    val context = LocalContext.current
    return remember {
        { isDark: Boolean -> File(context.filesDir, FILE_NAME).writeText(if (isDark) "1" else "0") }
    }
}

@Composable
actual fun loadSavedThemeIsDark(): Boolean? {
    val context = LocalContext.current
    return remember {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) file.readText().trim() == "1" else null
    }
}
