package org.example.project.platform

import androidx.compose.runtime.Composable
import org.example.project.models.Deck
import org.example.project.models.FlashCard

data class TestResult(val score: Int, val total: Int)

data class PersistedAppState(
    val studyProgress: Map<String, Int> = emptyMap(),
    val recentDeckIds: List<String> = emptyList(),
    val favoriteDeckIds: Set<String> = emptySet(),
    val lastDeckId: String? = null,
    val cardDifficulties: Map<String, String> = emptyMap(),
    val bookmarkedCardIds: Set<String> = emptySet(),
    val testResults: Map<String, TestResult> = emptyMap(),
    val customDecks: List<Deck> = emptyList()
)

@Composable
expect fun rememberAppStateSaver(): (PersistedAppState) -> Unit

@Composable
expect fun loadPersistedAppState(): PersistedAppState

fun PersistedAppState.serialize(): String {
    val sb = StringBuilder()
    sb.appendLine("PROGRESS")
    studyProgress.forEach { (k, v) -> sb.appendLine("$k=$v") }
    sb.appendLine("RECENT")
    recentDeckIds.forEach { sb.appendLine(it) }
    sb.appendLine("FAVORITES")
    favoriteDeckIds.forEach { sb.appendLine(it) }
    sb.appendLine("LASTDECK")
    if (lastDeckId != null) sb.appendLine(lastDeckId)
    sb.appendLine("DIFFICULTY")
    cardDifficulties.forEach { (k, v) -> sb.appendLine("$k=$v") }
    sb.appendLine("BOOKMARKS")
    bookmarkedCardIds.forEach { sb.appendLine(it) }
    sb.appendLine("TESTS")
    testResults.forEach { (k, v) -> sb.appendLine("$k=${v.score}/${v.total}") }
    sb.appendLine("CUSTOMDECKS")
    customDecks.forEach { deck ->
        // Format: DECK|id|title|category|description|accentIndex|badge1,badge2|isFavorite
        val badges = deck.badges.joinToString(",").ifEmpty { "_" }
        sb.appendLine("DECK|${deck.id}|${esc(deck.title)}|${deck.category}|${esc(deck.description)}|${deck.accentIndex}|$badges|${deck.isFavorite}")
        deck.cards.forEach { card ->
            sb.appendLine("CARD|${card.id}|${esc(card.front)}|${esc(card.back)}")
        }
    }
    return sb.toString()
}

private fun esc(s: String): String = s.replace("|", "\\|").replace("\n", "\\n")
private fun unesc(s: String): String = s.replace("\\|", "|").replace("\\n", "\n")

fun deserializeAppState(text: String): PersistedAppState {
    val progress = mutableMapOf<String, Int>()
    val recent = mutableListOf<String>()
    val favorites = mutableSetOf<String>()
    var lastDeck: String? = null
    val difficulties = mutableMapOf<String, String>()
    val bookmarks = mutableSetOf<String>()
    val tests = mutableMapOf<String, TestResult>()
    val decks = mutableListOf<Deck>()
    var currentDeckCards = mutableListOf<FlashCard>()
    var currentDeckBuilder: DeckBuilder? = null
    var section = ""

    for (line in text.lines()) {
        val t = line.trim()
        when (t) {
            "PROGRESS" -> { flushDeck(currentDeckBuilder, currentDeckCards, decks); currentDeckBuilder = null; section = "P"; continue }
            "RECENT" -> { flushDeck(currentDeckBuilder, currentDeckCards, decks); currentDeckBuilder = null; section = "R"; continue }
            "FAVORITES" -> { section = "F"; continue }
            "LASTDECK" -> { section = "L"; continue }
            "DIFFICULTY" -> { section = "D"; continue }
            "BOOKMARKS" -> { section = "B"; continue }
            "TESTS" -> { section = "T"; continue }
            "CUSTOMDECKS" -> { section = "C"; continue }
        }
        if (t.isEmpty()) continue
        when (section) {
            "P" -> { val p = t.split("=", limit = 2); if (p.size == 2) p[1].toIntOrNull()?.let { progress[p[0]] = it } }
            "R" -> recent.add(t)
            "F" -> favorites.add(t)
            "L" -> lastDeck = t
            "D" -> { val p = t.split("=", limit = 2); if (p.size == 2) difficulties[p[0]] = p[1] }
            "B" -> bookmarks.add(t)
            "T" -> {
                val p = t.split("=", limit = 2)
                if (p.size == 2) {
                    val sr = p[1].split("/", limit = 2)
                    if (sr.size == 2) { val s = sr[0].toIntOrNull(); val to = sr[1].toIntOrNull(); if (s != null && to != null) tests[p[0]] = TestResult(s, to) }
                }
            }
            "C" -> {
                if (t.startsWith("DECK|")) {
                    flushDeck(currentDeckBuilder, currentDeckCards, decks)
                    currentDeckCards = mutableListOf()
                    val parts = t.removePrefix("DECK|").split("|")
                    if (parts.size >= 7) {
                        currentDeckBuilder = DeckBuilder(
                            id = parts[0], title = unesc(parts[1]), category = parts[2],
                            description = unesc(parts[3]), accentIndex = parts[4].toIntOrNull() ?: 0,
                            badges = if (parts[5] == "_") emptyList() else parts[5].split(","),
                            isFavorite = parts.getOrNull(7)?.toBooleanStrictOrNull() ?: false
                        )
                    }
                } else if (t.startsWith("CARD|")) {
                    val parts = t.removePrefix("CARD|").split("|")
                    if (parts.size >= 3) {
                        currentDeckCards.add(FlashCard(parts[0], unesc(parts[1]), unesc(parts[2])))
                    }
                }
            }
        }
    }
    flushDeck(currentDeckBuilder, currentDeckCards, decks)

    return PersistedAppState(progress, recent, favorites, lastDeck, difficulties, bookmarks, tests, decks)
}

private data class DeckBuilder(
    val id: String, val title: String, val category: String,
    val description: String, val accentIndex: Int, val badges: List<String>, val isFavorite: Boolean
)

private fun flushDeck(builder: DeckBuilder?, cards: List<FlashCard>, out: MutableList<Deck>) {
    if (builder != null) {
        out.add(Deck(
            id = builder.id, title = builder.title, category = builder.category,
            description = builder.description, accentIndex = builder.accentIndex,
            badges = builder.badges, isFavorite = builder.isFavorite, cards = cards
        ))
    }
}
