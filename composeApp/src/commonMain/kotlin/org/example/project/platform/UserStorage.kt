package org.example.project.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberUserNameSaver(): (String) -> Unit

@Composable
expect fun loadSavedUserName(): String?
