package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.ChipRow
import org.example.project.components.DeckCardCompact
import org.example.project.components.DeckCardWide
import org.example.project.components.ProfileAvatar
import org.example.project.components.ProgressBar
import org.example.project.components.SearchField
import org.example.project.components.SectionHeader
import org.example.project.components.StatsCard
import org.example.project.data.SampleData
import org.example.project.models.Deck
import org.example.project.theme.AppColors

private const val CHIP_FAVORITES = "\u2764\uFE0F Favorites"
private const val CHIP_SEARCH = "\uD83D\uDD0D"

@Composable
fun HomeScreen(
    userName: String,
    profileImageBytes: ByteArray?,
    recentDeckIds: List<String> = emptyList(),
    deckProgress: (Deck) -> Float = { 0f },
    totalCardsReviewed: Int = 0,
    favoriteDeckIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    customDecks: List<Deck> = emptyList(),
    customDeckIds: Set<String> = emptySet(),
    onDeleteDeck: (String) -> Unit = {},
    onDeckSelected: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val allDecks = SampleData.decks + customDecks
    val allCategories = remember(customDecks.size) {
        allDecks.map { it.category }.distinct().sorted()
    }
    val chipItems = allCategories + CHIP_FAVORITES + CHIP_SEARCH

    var selectedChip by remember { mutableStateOf(allCategories.first()) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    val bottomDecks = remember(selectedChip, showSearch, searchQuery, favoriteDeckIds, customDecks.size) {
        when {
            showSearch && searchQuery.isNotBlank() -> allDecks.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
            selectedChip == CHIP_FAVORITES -> allDecks.filter { it.id in favoriteDeckIds }
            else -> allDecks.filter { it.category == selectedChip }
        }
    }

    val totalCards = allDecks.sumOf { it.cardCount }
    val masteryPercent = if (totalCards > 0) (totalCardsReviewed * 100 / totalCards).coerceAtMost(100) else 0
    val dailyGoal = 60
    val dailyPercent = (totalCardsReviewed * 100 / dailyGoal).coerceAtMost(100)
    val streakDays = if (totalCardsReviewed > 0) 1 else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.DarkChocolate)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            }
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Fixed header
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -30 }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hello,", color = AppColors.TextSecondary, fontSize = 14.sp)
                    Text(
                        "$userName \uD83D\uDC4B",
                        color = AppColors.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                ProfileAvatar(size = 40, name = userName, imageBytes = profileImageBytes)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

        // Chips
        ChipRow(
            items = chipItems,
            selected = if (showSearch) CHIP_SEARCH else selectedChip,
            onSelected = { item ->
                when (item) {
                    CHIP_SEARCH -> {
                        showSearch = true
                        searchQuery = ""
                    }
                    else -> {
                        showSearch = false
                        searchQuery = ""
                        selectedChip = item
                    }
                }
            }
        )

        // Search field
        AnimatedVisibility(visible = showSearch) {
            Column {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SearchField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Search by deck name..."
                        )
                    }
                    IconButton(onClick = {
                        showSearch = false
                        searchQuery = ""
                        selectedChip = allCategories.first()
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Rounded.Close, "Close search", tint = AppColors.TextMuted)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Daily progress
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, 100)) + slideInVertically(tween(500, 100)) { 40 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(AppColors.DeepRed, AppColors.BurntOrange)))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Daily Progress", color = AppColors.LightGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "$totalCardsReviewed cards reviewed today",
                        color = AppColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    ProgressBar(
                        totalCardsReviewed.toFloat() / dailyGoal.coerceAtLeast(1),
                        trackColor = AppColors.TextPrimary.copy(alpha = 0.2f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$dailyPercent% of daily goal", color = AppColors.TextPrimary.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("$totalCardsReviewed/$dailyGoal", color = AppColors.LightGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Stats
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, 200)) + slideInVertically(tween(500, 200)) { 40 }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard("Streak", "$streakDays days", Icons.Rounded.LocalFireDepartment, Modifier.weight(1f), AppColors.BurntOrange)
                StatsCard("Mastery", "$masteryPercent%", Icons.AutoMirrored.Rounded.TrendingUp, Modifier.weight(1f), AppColors.Amber)
                StatsCard("Learned", "$totalCardsReviewed", Icons.Rounded.School, Modifier.weight(1f), AppColors.Success)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Continue learning
        val continueLearningDecks = remember(recentDeckIds, customDecks.size) {
            recentDeckIds.mapNotNull { id -> allDecks.find { it.id == id } }
        }
        if (continueLearningDecks.isNotEmpty()) {
            SectionHeader("Continue Learning")
            Spacer(Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(continueLearningDecks, key = { it.id }) { deck ->
                    DeckCardCompact(
                        deck = deck.copy(progress = deckProgress(deck), isFavorite = deck.id in favoriteDeckIds),
                        onClick = { onDeckSelected(deck) }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Recommended — user-created first, then sample
        val recommendedDecks = (customDecks.filter { "Popular" in it.badges } +
                SampleData.decks.filter { "Popular" in it.badges || "AI" in it.badges }).take(8)
        SectionHeader("Recommended for You")
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(recommendedDecks, key = { it.id }) { deck ->
                DeckCardCompact(
                    deck = deck.copy(progress = deckProgress(deck), isFavorite = deck.id in favoriteDeckIds),
                    onClick = { onDeckSelected(deck) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Decks by category / favorites / search
        val sectionTitle = when {
            showSearch && searchQuery.isNotBlank() -> "Search Results"
            selectedChip == CHIP_FAVORITES -> "Favorites"
            else -> selectedChip
        }
        SectionHeader(sectionTitle)
        Spacer(Modifier.height(8.dp))
        if (bottomDecks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.CardSurface)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (selectedChip == CHIP_FAVORITES) "No favorites yet. Tap \u2764\uFE0F on a deck to add it!"
                    else "No decks found",
                    color = AppColors.TextMuted, fontSize = 14.sp
                )
            }
        } else {
            bottomDecks.forEach { deck ->
                DeckCardWide(
                    deck = deck.copy(progress = deckProgress(deck), isFavorite = deck.id in favoriteDeckIds),
                    onClick = { onDeckSelected(deck) },
                    onFavoriteToggle = { onToggleFavorite(deck.id) },
                    onDelete = if (deck.id in customDeckIds) {{ onDeleteDeck(deck.id) }} else null
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(100.dp))
        } // end scrollable
    }
}
