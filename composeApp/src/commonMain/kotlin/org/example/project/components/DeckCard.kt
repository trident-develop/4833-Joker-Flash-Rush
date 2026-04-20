package org.example.project.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.models.Deck
import org.example.project.theme.AppColors

@Composable
fun DeckCardWide(
    deck: Deck,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val accent = AppColors.accentColors.getOrElse(deck.accentIndex) { AppColors.Amber }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(AppColors.CardSurface, AppColors.CardSurface, accent.copy(alpha = 0.15f))
                )
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        deck.title,
                        color = AppColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${deck.category} \u2022 ${deck.cardCount} cards",
                        color = AppColors.TextMuted,
                        fontSize = 12.sp
                    )
                }
                Row {
                    if (onDelete != null) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = AppColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (deck.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (deck.isFavorite) AppColors.WarmRed else AppColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                deck.badges.forEach { badge -> Badge(badge) }
            }
            Spacer(Modifier.height(10.dp))
            ProgressBar(deck.progress)
            Spacer(Modifier.height(4.dp))
            Text(
                "${(deck.progress * 100).toInt()}% complete",
                color = AppColors.TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun DeckCardCompact(
    deck: Deck,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = AppColors.accentColors.getOrElse(deck.accentIndex) { AppColors.Amber }

    Box(
        modifier = modifier
            .width(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.CardSurface)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    deck.title.take(2).uppercase(),
                    color = accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                deck.title,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${deck.cardCount} cards",
                color = AppColors.TextMuted,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(10.dp))
            ProgressBar(deck.progress, height = 4)
            if (deck.badges.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Badge(deck.badges.first())
            }
        }
    }
}

@Composable
fun DeckCardGrid(
    deck: Deck,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val accent = AppColors.accentColors.getOrElse(deck.accentIndex) { AppColors.Amber }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.CardSurface)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        deck.title.take(1).uppercase(),
                        color = accent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (onDelete != null) {
                        Icon(
                            Icons.Default.Delete, contentDescription = "Delete",
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(16.dp).clickable { onDelete() }
                        )
                    }
                    Icon(
                        if (deck.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (deck.isFavorite) AppColors.WarmRed else AppColors.TextMuted,
                        modifier = Modifier.size(18.dp).clickable { onFavoriteToggle() }
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                deck.title,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                deck.category,
                color = AppColors.TextMuted,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(8.dp))
            ProgressBar(deck.progress, height = 4)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${deck.cardCount} cards", color = AppColors.TextMuted, fontSize = 10.sp)
                Text("${(deck.progress * 100).toInt()}%", color = AppColors.Amber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
