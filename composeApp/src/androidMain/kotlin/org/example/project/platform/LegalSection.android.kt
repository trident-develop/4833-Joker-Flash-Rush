package org.example.project.platform

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.runtime.Composable
import org.example.project.components.SettingsRow

@Composable
actual fun LegalSection(onNavigateToWebView: (url: String) -> Unit) {
    Column {
        SettingsRow(
            title = "Privacy Policy",
            icon = Icons.Rounded.Policy,
            onClick = { onNavigateToWebView("https://telegra.ph/Privacy-Policy-for-Joker-Flash-Rush-04-20") }
        )
    }
}
