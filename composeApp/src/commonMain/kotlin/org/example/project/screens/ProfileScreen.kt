package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.CollapsibleSection
import org.example.project.components.ProfileAvatar
import org.example.project.components.ProgressBar
import org.example.project.components.SectionHeader
import org.example.project.components.SettingsRow
import org.example.project.components.WarmSwitch
import org.example.project.data.SampleData
import org.example.project.platform.LegalSection
import org.example.project.platform.ProfileImagePickerDialog
import org.example.project.platform.TestResult
import org.example.project.platform.decodeImageBytes
import org.example.project.platform.loadSavedProfileImage
import org.example.project.platform.rememberProfileImageSaver
import org.example.project.theme.AppColors

@Composable
fun ProfileScreen(
    userName: String,
    onNameChanged: (String) -> Unit,
    onProfileImageChanged: (ByteArray) -> Unit,
    isDarkTheme: Boolean = true,
    onThemeToggle: (Boolean) -> Unit = {},
    totalCardsReviewed: Int = 0,
    totalDecksStudied: Int = 0,
    totalBookmarked: Int = 0,
    cardDifficulties: Map<String, String> = emptyMap(),
    testResults: Map<String, TestResult> = emptyMap(),
    completedDecks: Int = 0,
    langDecksStudied: Int = 0,
    bestTestPct: Int = 0,
    onNavigateToWebView: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var dailyReminder by remember { mutableStateOf(true) }
    var visible by remember { mutableStateOf(false) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showNameEditor by remember { mutableStateOf(false) }
    val savedImage = loadSavedProfileImage()
    var profileImageBytes by remember { mutableStateOf(savedImage) }
    val saveImage = rememberProfileImageSaver()

    LaunchedEffect(Unit) { visible = true }

    val profileBitmap = remember(profileImageBytes) { profileImageBytes?.let { decodeImageBytes(it) } }

    // Compute stats
    val hardCount = cardDifficulties.values.count { it == "Hard" }
    val goodCount = cardDifficulties.values.count { it == "Good" }
    val easyCount = cardDifficulties.values.count { it == "Easy" }
    val totalRated = hardCount + goodCount + easyCount

    ProfileImagePickerDialog(
        show = showImagePicker,
        onDismiss = { showImagePicker = false },
        onImageSelected = { bytes ->
            if (bytes != null) { profileImageBytes = bytes; saveImage(bytes); onProfileImageChanged(bytes) }
        }
    )

    if (showNameEditor) {
        var editName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameEditor = false },
            title = { Text("Edit Name", color = AppColors.TextPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName, onValueChange = { if (it.length <= 10) editName = it },
                        singleLine = true, placeholder = { Text("Your name", color = AppColors.TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Amber, unfocusedBorderColor = AppColors.CardSurfaceLight,
                            cursorColor = AppColors.Amber, focusedTextColor = AppColors.TextPrimary, unfocusedTextColor = AppColors.TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("${editName.length}/10", color = AppColors.TextMuted, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { if (editName.isNotBlank()) { onNameChanged(editName.trim()); showNameEditor = false } }) {
                    Text("Save", color = AppColors.Amber)
                }
            },
            dismissButton = { TextButton(onClick = { showNameEditor = false }) { Text("Cancel", color = AppColors.TextMuted) } },
            containerColor = AppColors.CardSurface
        )
    }

    Column(
        modifier = modifier.fillMaxSize().background(AppColors.DarkChocolate).padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Fixed profile header
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -20 }) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    if (profileBitmap != null) {
                        Image(bitmap = profileBitmap, contentDescription = "Profile", modifier = Modifier.size(80.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        ProfileAvatar(size = 80, name = userName)
                    }
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).size(28.dp).clip(RoundedCornerShape(8.dp)).background(AppColors.Amber).clickable { showImagePicker = true },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Edit, "Edit", tint = AppColors.DarkChocolate, modifier = Modifier.size(16.dp)) }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(userName, color = AppColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Edit, "Edit name", tint = AppColors.TextMuted, modifier = Modifier.size(16.dp).clickable { showNameEditor = true })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Scrollable content
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {

        // Main stats
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(500, 100)) + slideInVertically(tween(500, 100)) { 30 }) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(AppColors.CardSurface).padding(18.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProfileStat(Icons.Rounded.School, "$totalCardsReviewed", "Cards Studied", AppColors.Amber)
                    ProfileStat(Icons.Rounded.LocalFireDepartment, "$totalDecksStudied", "Decks Started", AppColors.BurntOrange)
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProfileStat(Icons.Rounded.Favorite, "$totalBookmarked", "Bookmarked", AppColors.DeepRed)
                    ProfileStat(Icons.Rounded.CheckCircle, "$totalRated", "Cards Rated", AppColors.Success)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Difficulty breakdown
        CollapsibleSection("Difficulty Breakdown") {
            if (totalRated > 0) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AppColors.CardSurface).padding(16.dp)
                ) {
                    Column {
                        DifficultyRow("Easy", easyCount, totalRated, AppColors.Success)
                        Spacer(Modifier.height(10.dp))
                        DifficultyRow("Good", goodCount, totalRated, AppColors.Amber)
                        Spacer(Modifier.height(10.dp))
                        DifficultyRow("Hard", hardCount, totalRated, AppColors.DeepRed)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AppColors.CardSurface).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Rate cards with Hard / Good / Easy while studying", color = AppColors.TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Test results
        CollapsibleSection("Test Results") {
        if (testResults.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AppColors.CardSurface).padding(16.dp)
                ) {
                    Column {
                        testResults.entries.sortedByDescending { it.value.score.toFloat() / it.value.total }.forEachIndexed { idx, (deckId, result) ->
                            val deck = SampleData.decks.find { it.id == deckId }
                            val pct = if (result.total > 0) result.score * 100 / result.total else 0
                            if (idx > 0) {
                                HorizontalDivider(color = AppColors.Divider, modifier = Modifier.padding(vertical = 8.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(deck?.title ?: deckId, color = AppColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text("${result.score}/${result.total} correct", color = AppColors.TextMuted, fontSize = 12.sp)
                                }
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(
                                        when {
                                            pct >= 80 -> AppColors.Success
                                            pct >= 50 -> AppColors.Amber
                                            else -> AppColors.DeepRed
                                        }.copy(alpha = 0.2f)
                                    ).padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "$pct%",
                                        color = when {
                                            pct >= 80 -> AppColors.Success
                                            pct >= 50 -> AppColors.Amber
                                            else -> AppColors.DeepRed
                                        },
                                        fontSize = 14.sp, fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AppColors.CardSurface).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Complete a test in Study mode to see results here", color = AppColors.TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        }
        } // end CollapsibleSection
        Spacer(Modifier.height(16.dp))

        // Settings
        SectionHeader("Settings")
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AppColors.CardSurface).padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Column {
                SettingsRow(title = "Dark Theme", subtitle = "Switch between dark and light mode", icon = Icons.Rounded.DarkMode, trailing = { WarmSwitch(isDarkTheme, { onThemeToggle(it) }) })
                HorizontalDivider(color = AppColors.Divider)
                SettingsRow(title = "Daily Reminder", subtitle = "Get reminded to study every day", icon = Icons.Rounded.Schedule, trailing = { WarmSwitch(dailyReminder, { dailyReminder = it }) })
            }
        }

        Spacer(Modifier.height(24.dp))

        // Achievements — computed dynamically
        data class AchievementState(val icon: String, val title: String, val desc: String, val unlocked: Boolean, val current: Int, val target: Int)

        val achievementStates = listOf(
            AchievementState("\u2B50", "First Steps", "Study your first flashcard", totalCardsReviewed >= 1, totalCardsReviewed.coerceAtMost(1), 1),
            AchievementState("\uD83D\uDD25", "Week Warrior", "Study 7 days in a row", totalDecksStudied >= 7, totalDecksStudied.coerceAtMost(7), 7),
            AchievementState("\uD83C\uDCCF", "Card Collector", "Review 500 cards total", totalCardsReviewed >= 500, totalCardsReviewed.coerceAtMost(500), 500),
            AchievementState("\uD83C\uDFC6", "Deck Master", "Complete 5 full decks", completedDecks >= 5, completedDecks.coerceAtMost(5), 5),
            AchievementState("\uD83D\uDCA1", "Knowledge Seeker", "Rate 100 cards", totalRated >= 100, totalRated.coerceAtMost(100), 100),
            AchievementState("\uD83C\uDFAF", "Perfect Score", "Get 100% on any test", bestTestPct >= 100, bestTestPct.coerceAtMost(100), 100),
            AchievementState("\uD83C\uDF0D", "Polyglot", "Study 3 language decks", langDecksStudied >= 3, langDecksStudied.coerceAtMost(3), 3),
            AchievementState("\u2764\uFE0F", "Bookworm", "Bookmark 50 cards", totalBookmarked >= 50, totalBookmarked.coerceAtMost(50), 50),
            AchievementState("\u26A1", "Speed Demon", "Review 30 cards in one deck", totalCardsReviewed >= 30, totalCardsReviewed.coerceAtMost(30), 30),
            AchievementState("\uD83D\uDC51", "Completionist", "Finish all 28 decks", completedDecks >= 28, completedDecks.coerceAtMost(28), 28)
        )

        val unlockedCount = achievementStates.count { it.unlocked }
        CollapsibleSection("Achievements ($unlockedCount/${achievementStates.size})") {
        achievementStates.forEach { ach ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (ach.unlocked) AppColors.Amber.copy(alpha = 0.1f) else AppColors.CardSurface)
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(if (ach.unlocked) AppColors.Amber.copy(alpha = 0.2f) else AppColors.WarmBrown),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (ach.unlocked) ach.icon else "\uD83D\uDD12", fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ach.title, color = if (ach.unlocked) AppColors.TextPrimary else AppColors.TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(2.dp))
                        Text(ach.desc, color = AppColors.TextMuted, fontSize = 12.sp)
                        if (!ach.unlocked && ach.target > 1) {
                            Spacer(Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ProgressBar(
                                        progress = ach.current.toFloat() / ach.target,
                                        height = 5,
                                        trackColor = AppColors.WarmBrown
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("${ach.current}/${ach.target}", color = AppColors.TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                    if (ach.unlocked) {
                        Text("\u2705", fontSize = 18.sp)
                    }
                }
            }
        }
        } // end CollapsibleSection

        Spacer(Modifier.height(16.dp))

        // Legal
        SectionHeader("Legal")
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AppColors.CardSurface).padding(horizontal = 14.dp, vertical = 4.dp)
        ) { LegalSection(onNavigateToWebView = onNavigateToWebView) }

        Spacer(Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(org.example.project.platform.appName, color = AppColors.TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("Version 1.0.0", color = AppColors.TextMuted.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(100.dp))
        } // end scrollable
    }
}

@Composable
private fun ProfileStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(120.dp)) {
        Icon(icon, label, tint = color, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = AppColors.TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun DifficultyRow(label: String, count: Int, total: Int, color: androidx.compose.ui.graphics.Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = color, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(50.dp))
        Box(modifier = Modifier.weight(1f)) {
            ProgressBar(
                progress = count.toFloat() / total.coerceAtLeast(1),
                height = 8,
                trackColor = AppColors.WarmBrown
            )
        }
        Spacer(Modifier.width(12.dp))
        Text("$count", color = AppColors.TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
