package org.example.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.example.project.platform.PlatformBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.AnimatedBottomBar
import org.example.project.data.SampleData
import org.example.project.models.Deck
import org.example.project.navigation.BottomTab
import org.example.project.platform.PersistedAppState
import org.example.project.platform.TestResult
import org.example.project.platform.loadPersistedAppState
import org.example.project.platform.loadSavedProfileImage
import org.example.project.platform.loadSavedUserName
import org.example.project.platform.rememberAppStateSaver
import org.example.project.platform.rememberUserNameSaver
import org.example.project.screens.CreateScreen
import org.example.project.screens.JokeScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.OnboardingFlow
import org.example.project.screens.ProfileScreen
import org.example.project.screens.StudyScreen
import org.example.project.theme.AppColors

@Composable
fun AppNavGraph(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val savedName = loadSavedUserName()
    val savedImage = loadSavedProfileImage()
    val saveUserName = rememberUserNameSaver()
    val persistedState = loadPersistedAppState()
    val saveAppState = rememberAppStateSaver()

    var userName by remember { mutableStateOf(savedName ?: "") }
    var profileImageBytes by remember { mutableStateOf(savedImage) }
    var onboardingDone by remember { mutableStateOf(savedName != null) }

    val studyProgress = remember { mutableStateMapOf<String, Int>().apply { putAll(persistedState.studyProgress) } }
    val recentDeckIds = remember { mutableStateListOf<String>().apply { addAll(persistedState.recentDeckIds) } }
    val favoriteDeckIds = remember { mutableStateListOf<String>().apply { addAll(persistedState.favoriteDeckIds) } }
    var lastDeckId by remember { mutableStateOf(persistedState.lastDeckId) }
    val cardDifficulties = remember { mutableStateMapOf<String, String>().apply { putAll(persistedState.cardDifficulties) } }
    val bookmarkedCardIds = remember { mutableStateListOf<String>().apply { addAll(persistedState.bookmarkedCardIds) } }
    val testResults = remember { mutableStateMapOf<String, TestResult>().apply { putAll(persistedState.testResults) } }
    val customDecks = remember { mutableStateListOf<Deck>().apply { addAll(persistedState.customDecks) } }

    fun persistState() {
        saveAppState(PersistedAppState(
            studyProgress = studyProgress.toMap(),
            recentDeckIds = recentDeckIds.toList(),
            favoriteDeckIds = favoriteDeckIds.toSet(),
            lastDeckId = lastDeckId,
            cardDifficulties = cardDifficulties.toMap(),
            bookmarkedCardIds = bookmarkedCardIds.toSet(),
            testResults = testResults.toMap(),
            customDecks = customDecks.toList()
        ))
    }

    fun deckProgress(deck: Deck): Float {
        val idx = studyProgress[deck.id] ?: return 0f
        return (idx + 1).toFloat() / deck.cardCount.coerceAtLeast(1)
    }

    fun toggleFavorite(deckId: String) {
        if (deckId in favoriteDeckIds) favoriteDeckIds.remove(deckId) else favoriteDeckIds.add(deckId)
        persistState()
    }

    fun deleteCustomDeck(deckId: String) {
        customDecks.removeAll { it.id == deckId }
        favoriteDeckIds.remove(deckId)
        studyProgress.remove(deckId)
        recentDeckIds.remove(deckId)
        testResults.remove(deckId)
        persistState()
    }

    val customDeckIds = remember(customDecks.size) { customDecks.map { it.id }.toSet() }

    if (!onboardingDone) {
        OnboardingFlow(
            onComplete = { name ->
                userName = name
                saveUserName(name)
                onboardingDone = true
            }
        )
        return
    }

    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    var studyDeck by remember { mutableStateOf<Deck?>(null) }
    var webViewUrl by remember { mutableStateOf<String?>(null) }

    if (webViewUrl != null) {
        PlatformBackHandler(enabled = true) { webViewUrl = null }
        Box(modifier = Modifier.fillMaxSize().background(AppColors.DarkChocolate)) {
            PlatformWebView(url = webViewUrl!!, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 48.dp).size(40.dp)
                    .clip(RoundedCornerShape(12.dp)).background(AppColors.Espresso.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) { TextButton(onClick = { webViewUrl = null }) { Text("\u2190", color = AppColors.TextPrimary, fontSize = 18.sp) } }
        }
        return
    }

    PlatformBackHandler(enabled = selectedTab != BottomTab.Home) {
        selectedTab = BottomTab.Home
    }

    Scaffold(
        containerColor = AppColors.DarkChocolate,
        bottomBar = {
            AnimatedBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).background(AppColors.DarkChocolate)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
                label = "tabContent"
            ) { tab ->
                when (tab) {
                    BottomTab.Home -> HomeScreen(
                        userName = userName,
                        profileImageBytes = profileImageBytes,
                        recentDeckIds = recentDeckIds,
                        deckProgress = { deckProgress(it) },
                        totalCardsReviewed = studyProgress.values.sumOf { it + 1 },
                        favoriteDeckIds = favoriteDeckIds.toSet(),
                        onToggleFavorite = { toggleFavorite(it) },
                        customDecks = customDecks.toList(),
                        customDeckIds = customDeckIds,
                        onDeleteDeck = { deleteCustomDeck(it) },
                        onDeckSelected = { deck ->
                            studyDeck = deck
                            selectedTab = BottomTab.Study
                        }
                    )
                    BottomTab.Study -> StudyScreen(
                        initialDeck = studyDeck,
                        lastDeckId = lastDeckId,
                        studyProgress = studyProgress,
                        cardDifficulties = cardDifficulties,
                        bookmarkedCardIds = bookmarkedCardIds.toSet(),
                        customDecks = customDecks,
                        customDeckIds = customDeckIds,
                        onProgressChanged = { deckId, cardIndex ->
                            studyProgress[deckId] = cardIndex
                            lastDeckId = deckId
                            recentDeckIds.remove(deckId)
                            recentDeckIds.add(0, deckId)
                            if (recentDeckIds.size > 5) recentDeckIds.removeRange(5, recentDeckIds.size)
                            persistState()
                        },
                        onDifficultyRated = { cardId, difficulty ->
                            cardDifficulties[cardId] = difficulty
                            persistState()
                        },
                        onBookmarkToggle = { cardId ->
                            if (cardId in bookmarkedCardIds) bookmarkedCardIds.remove(cardId) else bookmarkedCardIds.add(cardId)
                            persistState()
                        },
                        onTestCompleted = { deckId, score, total ->
                            testResults[deckId] = TestResult(score, total)
                            persistState()
                        }
                    )
                    BottomTab.Create -> CreateScreen(
                        onDeckCreated = { deck ->
                            customDecks.add(deck)
                            if (deck.isFavorite) favoriteDeckIds.add(deck.id)
                            persistState()
                        }
                    )
                    BottomTab.Jokes -> JokeScreen()
                    BottomTab.Profile -> {
                        val allDecks = SampleData.decks + customDecks
                        val completedDecks = allDecks.count { d ->
                            val idx = studyProgress[d.id] ?: return@count false
                            idx >= d.cardCount - 1
                        }
                        val langDecksStudied = allDecks.count { it.category == "Languages" && it.id in studyProgress }
                        val bestTestPct = testResults.values.maxOfOrNull { if (it.total > 0) it.score * 100 / it.total else 0 } ?: 0
                        ProfileScreen(
                            userName = userName,
                            onNameChanged = { newName -> userName = newName; saveUserName(newName) },
                            onProfileImageChanged = { bytes -> profileImageBytes = bytes },
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = onThemeToggle,
                            totalCardsReviewed = studyProgress.values.sumOf { it + 1 },
                            totalDecksStudied = studyProgress.size,
                            totalBookmarked = bookmarkedCardIds.size,
                            cardDifficulties = cardDifficulties,
                            testResults = testResults,
                            completedDecks = completedDecks,
                            langDecksStudied = langDecksStudied,
                            bestTestPct = bestTestPct,
                            onNavigateToWebView = { url -> webViewUrl = url }
                        )
                    }
                }
            }
        }
    }
}
