package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

private const val FILE_NAME = "app_state.txt"

@Composable
actual fun rememberAppStateSaver(): (PersistedAppState) -> Unit {
    val context = LocalContext.current
    return remember {
        { state: PersistedAppState ->
            File(context.filesDir, FILE_NAME).writeText(state.serialize())
        }
    }
}

@Composable
actual fun loadPersistedAppState(): PersistedAppState {
    val context = LocalContext.current
    return remember {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) deserializeAppState(file.readText()) else PersistedAppState()
    }
}
