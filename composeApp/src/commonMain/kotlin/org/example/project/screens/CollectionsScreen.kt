package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.ChipRow
import org.example.project.components.DeckCardGrid
import org.example.project.components.SearchField
import org.example.project.data.SampleData
import org.example.project.models.Deck
import org.example.project.theme.AppColors

private enum class SortOption(val label: String) {
    Name("Name"), Progress("Progress"), Cards("Card Count"), Recent("Recent")
}

@Composable
fun CollectionsScreen(
    deckProgress: (Deck) -> Float = { 0f },
    favoriteDeckIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    bookmarkedCardIds: Set<String> = emptySet(),
    customDecks: List<Deck> = emptyList(),
    customDeckIds: Set<String> = emptySet(),
    onDeleteDeck: (String) -> Unit = {},
    onDeckSelected: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }
    var sortOption by remember { mutableStateOf(SortOption.Name) }
    var showSortMenu by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    val allDecks = remember(customDecks.size) { SampleData.decks + customDecks }
    val tabs = listOf("All", "Favorites", "Bookmarked", "Trending")

    val bookmarkedDecks = remember(bookmarkedCardIds, customDecks.size) {
        allDecks.filter { deck -> deck.cards.any { it.id in bookmarkedCardIds } }
    }

    val filteredDecks = remember(selectedTab, searchQuery, sortOption, favoriteDeckIds, bookmarkedCardIds, customDecks.size) {
        val base = when (selectedTab) {
            "All" -> allDecks
            "Favorites" -> allDecks.filter { it.id in favoriteDeckIds }
            "Bookmarked" -> bookmarkedDecks
            "Trending" -> allDecks.sortedByDescending { it.cardCount }
            else -> allDecks
        }
        val searched = if (searchQuery.isBlank()) base
        else base.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }

        when (sortOption) {
            SortOption.Name -> searched.sortedBy { it.title }
            SortOption.Progress -> searched.sortedByDescending { deckProgress(it) }
            SortOption.Cards -> searched.sortedByDescending { it.cardCount }
            SortOption.Recent -> searched
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.DarkChocolate)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -20 }
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Collections", color = AppColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Rounded.SortByAlpha, "Sort", tint = AppColors.TextSecondary)
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            SortOption.entries.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.label, color = if (opt == sortOption) AppColors.Amber else AppColors.TextPrimary) },
                                    onClick = { sortOption = opt; showSortMenu = false }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                SearchField(searchQuery, { searchQuery = it }, placeholder = "Search collections...")
            }
        }

        Spacer(Modifier.height(14.dp))
        ChipRow(tabs, selectedTab, { selectedTab = it })
        Spacer(Modifier.height(6.dp))

        // Stats summary
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, 150)) + slideInVertically(tween(500, 150)) { 30 }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.CardSurface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CollectionStat("${filteredDecks.size}", "Decks")
                CollectionStat("${filteredDecks.sumOf { it.cardCount }}", "Total Cards")
                CollectionStat(
                    "${if (filteredDecks.isEmpty()) 0 else (filteredDecks.map { deckProgress(it) }.average() * 100).toInt()}%",
                    "Avg Progress"
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (filteredDecks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83D\uDCDA", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        when (selectedTab) {
                            "Favorites" -> "No favorite decks yet"
                            "Bookmarked" -> "No bookmarked cards yet"
                            else -> "No decks found"
                        },
                        color = AppColors.TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Medium
                    )
                    Text(
                        when (selectedTab) {
                            "Favorites" -> "Tap \u2764\uFE0F on a deck to add it"
                            "Bookmarked" -> "Tap \u2764\uFE0F on a flashcard while studying"
                            else -> "Try a different filter or search"
                        },
                        color = AppColors.TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredDecks, key = { it.id }) { deck ->
                    val bmCount = if (selectedTab == "Bookmarked") {
                        deck.cards.count { it.id in bookmarkedCardIds }
                    } else null
                    DeckCardGrid(
                        deck = deck.copy(progress = deckProgress(deck), isFavorite = deck.id in favoriteDeckIds),
                        onClick = { onDeckSelected(deck) },
                        onFavoriteToggle = { onToggleFavorite(deck.id) },
                        onDelete = if (deck.id in customDeckIds) {{ onDeleteDeck(deck.id) }} else null
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = AppColors.Amber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = AppColors.TextMuted, fontSize = 11.sp)
    }
}
