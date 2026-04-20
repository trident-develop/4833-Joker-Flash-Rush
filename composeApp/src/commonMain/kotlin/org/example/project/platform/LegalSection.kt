package org.example.project.platform

import androidx.compose.runtime.Composable

@Composable
expect fun LegalSection(onNavigateToWebView: (url: String) -> Unit)
