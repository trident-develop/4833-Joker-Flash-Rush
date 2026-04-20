package org.example.project.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberThemeSaver(): (Boolean) -> Unit

/** Returns saved dark-theme preference, or null if not set (defaults to dark). */
@Composable
expect fun loadSavedThemeIsDark(): Boolean?
