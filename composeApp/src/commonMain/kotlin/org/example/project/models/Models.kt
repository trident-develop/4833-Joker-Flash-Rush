package org.example.project.models

data class Deck(
    val id: String,
    val title: String,
    val category: String,
    val description: String = "",
    val progress: Float = 0f,
    val isFavorite: Boolean = false,
    val badges: List<String> = emptyList(),
    val cards: List<FlashCard> = emptyList(),
    val accentIndex: Int = 0
) {
    val cardCount: Int get() = cards.size
}

data class FlashCard(
    val id: String,
    val front: String,
    val back: String,
    val isLearned: Boolean = false,
    val isBookmarked: Boolean = false
)

data class UserProfile(
    val name: String = "",
    val totalCardsLearned: Int = 0,
    val streakDays: Int = 0,
    val studyMinutes: Int = 0,
    val decksCompleted: Int = 0
)

data class Achievement(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val icon: String
)

enum class StudyMode { Flashcards, Write, Test }
