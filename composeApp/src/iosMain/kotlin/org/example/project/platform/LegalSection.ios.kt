package org.example.project.platform

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import org.example.project.components.SettingsRow
import org.example.project.theme.AppColors

@Composable
actual fun LegalSection(onNavigateToWebView: (url: String) -> Unit) {
    Column {
        SettingsRow(
            title = "Privacy Policy",
            icon = Icons.Rounded.Policy,
            onClick = { onNavigateToWebView("https://telegra.ph/Privacy-Policy-for-Joklet-04-20") }
        )
        HorizontalDivider(color = AppColors.Divider)
        SettingsRow(
            title = "Terms of Use",
            icon = Icons.Rounded.Description,
            onClick = { onNavigateToWebView("https://telegra.ph/Terms-and-Conditions-for-Joklet-04-20") }
        )
    }
}
