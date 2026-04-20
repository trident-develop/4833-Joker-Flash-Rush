package org.example.project.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Refresh
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.FlashCardView
import org.example.project.components.GradientButton
import org.example.project.components.ProgressBar
import org.example.project.components.SegmentedControl
import org.example.project.components.WarmSwitch
import org.example.project.data.SampleData
import org.example.project.models.Deck
import org.example.project.models.FlashCard
import org.example.project.models.StudyMode
import org.example.project.theme.AppColors

@Composable
fun StudyScreen(
    initialDeck: Deck?,
    lastDeckId: String? = null,
    studyProgress: Map<String, Int> = emptyMap(),
    cardDifficulties: Map<String, String> = emptyMap(),
    bookmarkedCardIds: Set<String> = emptySet(),
    onProgressChanged: (deckId: String, cardIndex: Int) -> Unit = { _, _ -> },
    onDifficultyRated: (cardId: String, difficulty: String) -> Unit = { _, _ -> },
    onBookmarkToggle: (cardId: String) -> Unit = {},
    onTestCompleted: (deckId: String, score: Int, total: Int) -> Unit = { _, _, _ -> },
    customDecks: List<Deck> = emptyList(),
    customDeckIds: Set<String> = emptySet(),
    modifier: Modifier = Modifier
) {
    val decks = (SampleData.decks + customDecks).filter { it.cards.isNotEmpty() }

    // Resolve initial deck: explicit navigation > last studied > first
    val resolvedDeck = remember(initialDeck, lastDeckId) {
        initialDeck
            ?: lastDeckId?.let { id -> decks.find { it.id == id } }
            ?: decks.first()
    }

    var selectedDeck by remember { mutableStateOf(resolvedDeck) }
    var currentIndex by remember(selectedDeck.id) { mutableStateOf(studyProgress[selectedDeck.id] ?: 0) }
    var isFlipped by remember(currentIndex, selectedDeck) { mutableStateOf(false) }
    var showDeckPicker by remember { mutableStateOf(false) }
    var shuffleEnabled by remember { mutableStateOf(false) }
    var studyMode by remember { mutableStateOf(StudyMode.Flashcards) }

    // Update selectedDeck if navigated from Home/Collections
    LaunchedEffect(initialDeck) {
        if (initialDeck != null && initialDeck.id != selectedDeck.id) {
            selectedDeck = initialDeck
        }
    }

    val cards = remember(selectedDeck, shuffleEnabled) {
        if (shuffleEnabled) selectedDeck.cards.shuffled() else selectedDeck.cards
    }
    val currentCard = cards.getOrNull(currentIndex)

    LaunchedEffect(selectedDeck.id, currentIndex) {
        onProgressChanged(selectedDeck.id, currentIndex)
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val rootTapSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.DarkChocolate)
            .clickable(interactionSource = rootTapSource, indication = null) {
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Deck selector
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.CardSurface)
                    .clickable { showDeckPicker = true }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Currently studying", color = AppColors.TextMuted, fontSize = 11.sp)
                    Text(selectedDeck.title, color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Icon(Icons.Default.KeyboardArrowDown, "Change deck", tint = AppColors.Amber)
            }
            DropdownMenu(expanded = showDeckPicker, onDismissRequest = { showDeckPicker = false }) {
                decks.forEach { deck ->
                    DropdownMenuItem(
                        text = { Text("${deck.title} (${deck.cards.size})", color = if (deck.id == selectedDeck.id) AppColors.Amber else AppColors.TextPrimary) },
                        onClick = {
                            selectedDeck = deck
                            currentIndex = studyProgress[deck.id] ?: 0
                            showDeckPicker = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val isCustomDeck = selectedDeck.id in customDeckIds
        val availableModes = if (isCustomDeck) listOf(StudyMode.Flashcards, StudyMode.Write) else StudyMode.entries
        // Reset mode if Test was selected but deck switched to custom
        if (studyMode !in availableModes) studyMode = availableModes.first()

        SegmentedControl(
            items = availableModes.map { it.name },
            selectedIndex = availableModes.indexOf(studyMode).coerceAtLeast(0),
            onSelected = { studyMode = availableModes[it] },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Progress bar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${currentIndex + 1} / ${cards.size}", color = AppColors.Amber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("${(((currentIndex + 1).toFloat() / cards.size.coerceAtLeast(1)) * 100).toInt()}%", color = AppColors.TextMuted, fontSize = 14.sp)
        }
        Spacer(Modifier.height(6.dp))
        ProgressBar((currentIndex + 1).toFloat() / cards.size.coerceAtLeast(1))

        Spacer(Modifier.height(20.dp))

        when (studyMode) {
            StudyMode.Flashcards -> {
                if (currentCard != null) {
                    val difficulty = cardDifficulties[currentCard.id]
                    val isBookmarked = currentCard.id in bookmarkedCardIds

                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            (slideInHorizontally(tween(300)) { it / 3 } + fadeIn(tween(300)))
                                .togetherWith(slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(300)))
                        },
                        label = "cardSwipe"
                    ) { index ->
                        val card = cards.getOrNull(index) ?: return@AnimatedContent
                        val cardDiff = cardDifficulties[card.id]
                        val cardBm = card.id in bookmarkedCardIds
                        FlashCardView(
                            card = card.copy(isBookmarked = cardBm, isLearned = cardDiff != null),
                            isFlipped = isFlipped,
                            onFlip = { isFlipped = !isFlipped },
                            onMarkLearned = {},
                            difficultyLabel = cardDiff
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Nav controls
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (currentIndex > 0) { currentIndex--; isFlipped = false } }) {
                            Box(Modifier.size(48.dp).clip(CircleShape).background(AppColors.CardSurface), contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous", tint = AppColors.TextPrimary)
                            }
                        }
                        IconButton(onClick = { if (currentIndex < cards.size - 1) { currentIndex++; isFlipped = false } }) {
                            Box(Modifier.size(56.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(AppColors.DeepRed, AppColors.Amber))), contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next", tint = Color.White)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Difficulty buttons
                    Text("How well did you know this?", color = AppColors.TextMuted, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DifficultyButton("Hard", AppColors.DeepRed, Modifier.weight(1f), selected = difficulty == "Hard") {
                            onDifficultyRated(currentCard.id, "Hard")
                            if (currentIndex < cards.size - 1) { currentIndex++; isFlipped = false }
                        }
                        DifficultyButton("Good", AppColors.Amber, Modifier.weight(1f), selected = difficulty == "Good") {
                            onDifficultyRated(currentCard.id, "Good")
                            if (currentIndex < cards.size - 1) { currentIndex++; isFlipped = false }
                        }
                        DifficultyButton("Easy", AppColors.Success, Modifier.weight(1f), selected = difficulty == "Easy") {
                            onDifficultyRated(currentCard.id, "Easy")
                            if (currentIndex < cards.size - 1) { currentIndex++; isFlipped = false }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Shuffle only
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Refresh, "Shuffle", tint = AppColors.TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Shuffle", color = AppColors.TextMuted, fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    WarmSwitch(shuffleEnabled, { shuffleEnabled = it })
                }
            }
            StudyMode.Write -> WriteMode(cards, currentIndex)
            StudyMode.Test -> TestMode(cards, selectedDeck.id, onTestCompleted)
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun DifficultyButton(text: String, color: Color, modifier: Modifier = Modifier, selected: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.35f) else color.copy(alpha = 0.15f))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun WriteMode(cards: List<FlashCard>, currentIndex: Int) {
    val card = cards.getOrNull(currentIndex) ?: return
    var answer by remember(currentIndex) { mutableStateOf("") }
    var submitted by remember(currentIndex) { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(AppColors.CardSurface).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("What is the answer?", color = AppColors.TextMuted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Text(card.front, color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(20.dp))
        androidx.compose.material3.OutlinedTextField(
            value = answer, onValueChange = { answer = it },
            modifier = Modifier.fillMaxWidth().onFocusChanged { state ->
                if (!state.isFocused) keyboardController?.hide()
            },
            placeholder = { Text("Type your answer...", color = AppColors.TextMuted) },
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Amber, unfocusedBorderColor = AppColors.CardSurface,
                cursorColor = AppColors.Amber, focusedTextColor = AppColors.TextPrimary, unfocusedTextColor = AppColors.TextPrimary,
                focusedContainerColor = AppColors.CardSurface, unfocusedContainerColor = AppColors.CardSurface
            ),
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.height(16.dp))
        if (!submitted) {
            GradientButton("Check Answer", onClick = { submitted = true }, modifier = Modifier.fillMaxWidth())
        } else {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(AppColors.Success.copy(alpha = 0.15f)).padding(16.dp)) {
                Column {
                    Text("Correct answer:", color = AppColors.Success, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(card.back, color = AppColors.TextPrimary, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun TestMode(
    cards: List<FlashCard>,
    deckId: String,
    onTestCompleted: (deckId: String, score: Int, total: Int) -> Unit
) {
    val testCards = remember(deckId) { cards.shuffled().take(10) }
    val totalQ = testCards.size
    var currentQ by remember(deckId) { mutableStateOf(0) }
    var score by remember(deckId) { mutableStateOf(0) }
    var selectedAnswer by remember(currentQ, deckId) { mutableStateOf(-1) }
    var testDone by remember(deckId) { mutableStateOf(false) }

    val card = testCards.getOrNull(currentQ)

    if (testDone || card == null) {
        // Results
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))
            Text("\uD83C\uDF89", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("Test Complete!", color = AppColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("$score / $totalQ correct", color = AppColors.Amber, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            val pct = if (totalQ > 0) score * 100 / totalQ else 0
            Text("$pct%", color = AppColors.TextMuted, fontSize = 16.sp)
            Spacer(Modifier.height(24.dp))
            GradientButton("Retake Test", onClick = { currentQ = 0; score = 0; testDone = false }, modifier = Modifier.fillMaxWidth())
        }
        return
    }

    val options = remember(currentQ, deckId) {
        val wrong = cards.filter { it.id != card.id }.shuffled().take(3).map { it.back }
        (wrong + card.back).shuffled()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(16.dp))
        Text("Question ${currentQ + 1} of $totalQ", color = AppColors.TextMuted, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(AppColors.CardSurface).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(card.front, color = AppColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(16.dp))
        options.forEachIndexed { idx, option ->
            val isSelected = idx == selectedAnswer
            val isCorrect = option == card.back
            val bgColor = when {
                !isSelected && selectedAnswer == -1 -> AppColors.WarmBrown
                isSelected && isCorrect -> AppColors.Success.copy(alpha = 0.2f)
                isSelected && !isCorrect -> AppColors.DeepRed.copy(alpha = 0.2f)
                !isSelected && isCorrect && selectedAnswer != -1 -> AppColors.Success.copy(alpha = 0.1f)
                else -> AppColors.WarmBrown
            }
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).background(bgColor)
                    .clickable { if (selectedAnswer == -1) { selectedAnswer = idx; if (isCorrect) score++ } }.padding(16.dp)
            ) {
                Text(option, color = AppColors.TextPrimary, fontSize = 14.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
        if (selectedAnswer != -1) {
            Spacer(Modifier.height(12.dp))
            if (currentQ < totalQ - 1) {
                GradientButton("Next Question", onClick = { currentQ++ }, modifier = Modifier.fillMaxWidth())
            } else {
                GradientButton("See Results", onClick = {
                    testDone = true
                    onTestCompleted(deckId, score, totalQ)
                }, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Score: $score / ${(currentQ + 1).coerceAtMost(totalQ)}", color = AppColors.Amber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
