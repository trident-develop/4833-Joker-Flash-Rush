package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.platform.loadSavedThemeIsDark
import org.example.project.platform.rememberThemeSaver
import org.example.project.screens.LoadingScreen
import org.example.project.screens.NoInternetScreen
import org.example.project.screens.privacy.Show3
import org.example.project.theme.JokerFlashTheme

@Composable
fun App() {
    val savedTheme = loadSavedThemeIsDark()
    var isDark by remember { mutableStateOf(savedTheme ?: true) }
    val saveTheme = rememberThemeSaver()

    JokerFlashTheme(darkTheme = isDark) {
        Gray(
            loading = { LoadingScreen() },
            noInternet = { NoInternetScreen(it) },
            white = {
                AppNavGraph(
                    isDarkTheme = isDark,
                    onThemeToggle = { dark ->
                        isDark = dark
                        saveTheme(dark)
                    }
                )
            }
        )
    }
}