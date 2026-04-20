package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.ChipRow
import org.example.project.components.GradientButton
import org.example.project.components.SegmentedControl
import org.example.project.components.WarmSwitch
import org.example.project.data.SampleData
import org.example.project.models.Deck
import org.example.project.models.FlashCard
import org.example.project.theme.AppColors

private data class CardDraft(var front: String = "", var back: String = "")

@Composable
fun CreateScreen(
    onDeckCreated: (Deck) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var segmentIndex by remember { mutableStateOf(0) }
    var deckTitle by remember { mutableStateOf("") }
    var deckDescription by remember { mutableStateOf("") }
    val allCategories = remember { SampleData.decks.map { it.category }.distinct().sorted() }
    var selectedCategory by remember { mutableStateOf(allCategories.first()) }
    var selectedAccent by remember { mutableStateOf(0) }
    var isRecommended by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    val cardDrafts = remember { mutableStateListOf(CardDraft(), CardDraft(), CardDraft()) }
    var deckSaved by remember { mutableStateOf(false) }
    var savedDeckTitle by remember { mutableStateOf("") }

    val validCards = cardDrafts.filter { it.front.isNotBlank() && it.back.isNotBlank() }
    val canSaveDeck = deckTitle.isNotBlank() && deckDescription.isNotBlank() && validCards.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.DarkChocolate)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            }
            .padding(horizontal = 20.dp)
    ) {
        // Fixed header
        Spacer(Modifier.height(16.dp))
        Text("Create", color = AppColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(20.dp))

        // Scrollable content
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {

            // Success message
            AnimatedVisibility(visible = deckSaved) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppColors.Success.copy(alpha = 0.15f))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Check, "Done", tint = AppColors.Success, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("\"$savedDeckTitle\" created successfully!", color = AppColors.Success, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (segmentIndex == 0) {
                // Create Deck
                WarmTextField(deckTitle, { deckTitle = it }, "Deck Title", "e.g. French Vocabulary A2")
                Spacer(Modifier.height(12.dp))
                WarmTextField(deckDescription, { deckDescription = it }, "Description", "What's this deck about?", singleLine = false, minLines = 3)

                Spacer(Modifier.height(16.dp))
                Text("Category", color = AppColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                ChipRow(
                    items = allCategories,
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it }
                )

                Spacer(Modifier.height(16.dp))
                Text("Accent Color", color = AppColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppColors.accentColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (index == selectedAccent) Modifier.border(3.dp, AppColors.TextPrimary, CircleShape) else Modifier
                                )
                                .clickable { selectedAccent = index }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Make Recommended", color = AppColors.TextPrimary, fontSize = 14.sp)
                    WarmSwitch(isRecommended, { isRecommended = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Favorite by Default", color = AppColors.TextPrimary, fontSize = 14.sp)
                    WarmSwitch(isFavorite, { isFavorite = it })
                }

                Spacer(Modifier.height(16.dp))

                // Cards for this deck
                Text("Cards (${validCards.size})", color = AppColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                cardDrafts.forEachIndexed { index, draft ->
                    CardEditor(
                        index = index + 1,
                        front = draft.front,
                        back = draft.back,
                        onFrontChange = { cardDrafts[index] = draft.copy(front = it) },
                        onBackChange = { cardDrafts[index] = draft.copy(back = it) },
                        onRemove = if (cardDrafts.size > 1) {{ cardDrafts.removeAt(index) }} else null
                    )
                    Spacer(Modifier.height(10.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(2.dp, AppColors.Amber.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable { cardDrafts.add(CardDraft()) }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, "Add card", tint = AppColors.Amber, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Add Card", color = AppColors.Amber, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Live preview
                if (deckTitle.isNotBlank()) {
                    Text("Preview", color = AppColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppColors.CardSurface)
                            .padding(16.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(AppColors.accentColors[selectedAccent].copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(deckTitle.take(1).uppercase(), color = AppColors.accentColors[selectedAccent], fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(deckTitle, color = AppColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(selectedCategory, color = AppColors.TextMuted, fontSize = 12.sp)
                            if (deckDescription.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(deckDescription, color = AppColors.TextSecondary, fontSize = 12.sp, maxLines = 2)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("${validCards.size} cards", color = AppColors.Amber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                GradientButton(
                    "Save Deck",
                    onClick = {
                        if (canSaveDeck) {
                            val newDeck = Deck(
                                id = "custom_${kotlin.random.Random.nextInt(100000, 999999)}",
                                title = deckTitle.trim(),
                                category = selectedCategory,
                                description = deckDescription.trim(),
                                isFavorite = isFavorite,
                                badges = if (isRecommended) listOf("Popular") else emptyList(),
                                cards = validCards.mapIndexed { i, d ->
                                    FlashCard("custom_${kotlin.random.Random.nextInt(100000, 999999)}_$i", d.front.trim(), d.back.trim())
                                },
                                accentIndex = selectedAccent
                            )
                            onDeckCreated(newDeck)
                            savedDeckTitle = deckTitle.trim()
                            deckSaved = true
                            deckTitle = ""
                            deckDescription = ""
                            cardDrafts.clear()
                            cardDrafts.addAll(listOf(CardDraft(), CardDraft(), CardDraft()))
                            isRecommended = false
                            isFavorite = false
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canSaveDeck
                )

                Spacer(Modifier.height(20.dp))
                TipsPanel()
            } else {
                // Add Cards tab
                Text("Add cards to your deck", color = AppColors.TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))

                cardDrafts.forEachIndexed { index, draft ->
                    CardEditor(
                        index = index + 1,
                        front = draft.front,
                        back = draft.back,
                        onFrontChange = { cardDrafts[index] = draft.copy(front = it) },
                        onBackChange = { cardDrafts[index] = draft.copy(back = it) },
                        onRemove = if (cardDrafts.size > 1) {{ cardDrafts.removeAt(index) }} else null
                    )
                    Spacer(Modifier.height(12.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(2.dp, AppColors.Amber.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable { cardDrafts.add(CardDraft()) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, "Add card", tint = AppColors.Amber, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Add Card", color = AppColors.Amber, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(20.dp))
                GradientButton(
                    "Save ${validCards.size} Cards",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = validCards.isNotEmpty()
                )
            }

            Spacer(Modifier.height(100.dp))
        } // end scrollable
    }
}

@Composable
private fun WarmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = AppColors.TextMuted) },
        placeholder = { Text(placeholder, color = AppColors.TextMuted.copy(alpha = 0.5f)) },
        singleLine = singleLine, minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Amber, unfocusedBorderColor = AppColors.CardSurface,
            cursorColor = AppColors.Amber, focusedTextColor = AppColors.TextPrimary, unfocusedTextColor = AppColors.TextPrimary,
            focusedLabelColor = AppColors.Amber, unfocusedLabelColor = AppColors.TextMuted,
            focusedContainerColor = AppColors.CardSurface.copy(alpha = 0.5f), unfocusedContainerColor = AppColors.CardSurface.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun CardEditor(index: Int, front: String, back: String, onFrontChange: (String) -> Unit, onBackChange: (String) -> Unit, onRemove: (() -> Unit)?) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AppColors.CardSurface).padding(16.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Card $index", color = AppColors.Amber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                if (onRemove != null) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, "Remove", tint = AppColors.TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            WarmTextField(front, onFrontChange, "Front", "Question or term")
            Spacer(Modifier.height(8.dp))
            WarmTextField(back, onBackChange, "Back", "Answer or definition")
        }
    }
}

@Composable
private fun TipsPanel() {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(AppColors.Amber.copy(alpha = 0.08f))
            .border(1.dp, AppColors.Amber.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Text("Tips for Great Cards", color = AppColors.Amber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            TipItem("Keep cards concise and focused")
            TipItem("Use one concept per card")
            TipItem("Add context clues when helpful")
        }
    }
}

@Composable
private fun TipItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("\u2022", color = AppColors.TextMuted, fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        Text(text, color = AppColors.TextSecondary, fontSize = 12.sp)
    }
}
