package org.example.project.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.models.FlashCard
import org.example.project.theme.AppColors

@Composable
fun FlashCardView(
    card: FlashCard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onMarkLearned: () -> Unit,
    difficultyLabel: String? = null,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "cardFlip"
    )
    val scale by animateFloatAsState(
        targetValue = if (rotation in 60f..120f) 0.92f else 1f,
        animationSpec = tween(250),
        label = "cardScale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (rotation in 45f..135f) 20f else 10f,
        animationSpec = tween(250),
        label = "cardElevation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .graphicsLayer {
                rotationY = rotation
                scaleX = scale
                scaleY = scale
                cameraDistance = 12f * density
            }
            .shadow(elevation.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        AppColors.CardSurface,
                        AppColors.CardSurfaceLight.copy(alpha = 0.7f)
                    )
                )
            )
            .clickable { onFlip() },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            CardFace(
                text = card.front,
                label = "FRONT",
                card = card,
                onMarkLearned = onMarkLearned,
                accentColor = AppColors.Amber,
                difficultyLabel = difficultyLabel
            )
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }.fillMaxSize()) {
                CardFace(
                    text = card.back,
                    label = "BACK",
                    card = card,
                    onMarkLearned = onMarkLearned,
                    accentColor = AppColors.BurntOrange,
                    difficultyLabel = difficultyLabel
                )
            }
        }
    }
}

@Composable
private fun CardFace(
    text: String,
    label: String,
    card: FlashCard,
    onMarkLearned: () -> Unit,
    accentColor: Color,
    difficultyLabel: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(label, color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onMarkLearned, modifier = Modifier.size(32.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (card.isLearned) AppColors.Success.copy(alpha = 0.2f)
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Learned",
                            tint = if (card.isLearned) AppColors.Success else AppColors.TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (difficultyLabel != null) {
                val diffColor = when (difficultyLabel) {
                    "Hard" -> AppColors.DeepRed
                    "Good" -> AppColors.Amber
                    "Easy" -> AppColors.Success
                    else -> AppColors.TextMuted
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(diffColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(difficultyLabel, color = diffColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(Modifier)
            }
            Text("Tap to flip", color = AppColors.TextMuted, fontSize = 12.sp)
        }
    }
}
