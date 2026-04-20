package org.example.project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.School
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Study("Study", Icons.Rounded.School),
    Create("Create", Icons.Default.Add),
    Jokes("Jokes", Icons.Rounded.EmojiEmotions),
    Profile("Profile", Icons.Default.Person)
}
