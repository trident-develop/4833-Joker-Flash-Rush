package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

private const val FILE_NAME = "user_name.txt"

@Composable
actual fun rememberUserNameSaver(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { name: String -> File(context.filesDir, FILE_NAME).writeText(name) }
    }
}

@Composable
actual fun loadSavedUserName(): String? {
    val context = LocalContext.current
    return remember {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) file.readText().takeIf { it.isNotBlank() } else null
    }
}
