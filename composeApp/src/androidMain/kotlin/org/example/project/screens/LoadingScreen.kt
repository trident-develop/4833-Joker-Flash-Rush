package org.example.project.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farbridge.astrodu.R
import kotlinx.coroutines.delay
import kotlin.random.Random

private object LoadingColors {
    val DarkChocolate = Color(0xFF1A0F0A)
    val Espresso = Color(0xFF2A1A12)
    val WarmBrown = Color(0xFF3D2B1E)
    val Amber = Color(0xFFE6A030)
    val DeepRed = Color(0xFFB33A3A)
    val BurntOrange = Color(0xFFE07030)
    val TextSecondary = Color(0xFFB8A090)
}

private data class Particle(
    val x: Float,
    val speed: Float,
    val particleSize: Float,
    val alpha: Float
)

@Composable
fun LoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    BackHandler(enabled = true) {}
    // Card animations
    val card1Rot by infiniteTransition.animateFloat(
        initialValue = -15f, targetValue = -5f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "c1r"
    )
    val card2Rot by infiniteTransition.animateFloat(
        initialValue = 5f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "c2r"
    )
    val card3Rot by infiniteTransition.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "c3r"
    )
    val card1Y by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -12f,
        animationSpec = infiniteRepeatable(tween(1600, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "c1y"
    )
    val card2Y by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "c2y"
    )
    val card3Y by infiniteTransition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "c3y"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(1300, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "glow"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutCubic), RepeatMode.Reverse),
        label = "glowS"
    )

    // Particles
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "particles"
    )
    val particles = remember {
        List(25) {
            Particle(
                x = Random.nextFloat(),
                speed = 0.3f + Random.nextFloat() * 0.7f,
                particleSize = 1.5f + Random.nextFloat() * 3f,
                alpha = 0.15f + Random.nextFloat() * 0.35f
            )
        }
    }

    // Progress bar — fast to 60%, then slow to 99% (never 100%)
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(0.60f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        progress.animateTo(0.99f, animationSpec = tween(5000, easing = LinearEasing))
    }

    // Rotating text
    val messages = listOf("Preparing your study space", "Loading decks", "Syncing progress")
    var msgIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(750)
            msgIndex = (msgIndex + 1) % messages.size
        }
    }

    // Entrance
    val entranceAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entranceAlpha.animateTo(1f, tween(600))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LoadingColors.DarkChocolate, LoadingColors.Espresso, LoadingColors.DarkChocolate))),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.bg_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Particles layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val y = size.height * (1f - ((particleProgress * p.speed + p.x) % 1f))
                val x = size.width * p.x + (size.width * 0.05f * kotlin.math.sin((particleProgress + p.x) * 6.28f))
                val fadeAlpha = p.alpha * (1f - ((particleProgress * p.speed + p.x) % 1f))
                drawCircle(
                    color = LoadingColors.Amber.copy(alpha = fadeAlpha.coerceIn(0f, 1f)),
                    radius = p.particleSize * density,
                    center = Offset(x.coerceIn(0f, size.width), y)
                )
            }
        }

        Column(
            modifier = Modifier.alpha(entranceAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App title
            Text(
                org.example.project.platform.appName,
                color = LoadingColors.Amber,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(48.dp))

            // Card stack with glow
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow behind cards
                Canvas(modifier = Modifier.size(180.dp).graphicsLayer { scaleX = glowScale; scaleY = glowScale }) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(LoadingColors.Amber.copy(alpha = glowAlpha), Color.Transparent),
                            radius = size.minDimension / 2
                        ),
                        radius = size.minDimension / 2
                    )
                }

                // Card 3 (back)
                LoadingCardShape(
                    modifier = Modifier.graphicsLayer {
                        rotationZ = card1Rot
                        translationY = card1Y
                        translationX = -8f
                    },
                    color = LoadingColors.DeepRed
                )
                // Card 2 (middle)
                LoadingCardShape(
                    modifier = Modifier.graphicsLayer {
                        rotationZ = card2Rot
                        translationY = card2Y
                        translationX = 6f
                    },
                    color = LoadingColors.BurntOrange
                )
                // Card 1 (front)
                LoadingCardShape(
                    modifier = Modifier.graphicsLayer {
                        rotationZ = card3Rot
                        translationY = card3Y
                    },
                    color = LoadingColors.Amber
                )
            }

            Spacer(Modifier.height(40.dp))

            // Animated text
            AnimatedContent(
                targetState = msgIndex,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "loadingText"
            ) { index ->
                Text(
                    text = messages[index],
                    color = LoadingColors.TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(20.dp))

            // Progress percentage
            Text(
                text = "${(progress.value * 100).toInt()}%",
                color = LoadingColors.Amber,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(10.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LoadingColors.WarmBrown)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.horizontalGradient(listOf(LoadingColors.DeepRed, LoadingColors.Amber)))
                )
            }
        }
    }
}

@Composable
private fun LoadingCardShape(modifier: Modifier, color: Color) {
    Canvas(
        modifier = modifier.size(130.dp, 85.dp)
    ) {
        // Card body
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(16f * density, 16f * density),
            size = size
        )
        // Shadow-like darker edge at bottom
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.15f),
            topLeft = Offset(0f, size.height * 0.85f),
            size = Size(size.width, size.height * 0.15f),
            cornerRadius = CornerRadius(0f, 16f * density)
        )
        // Line decorations
        drawRoundRect(
            color = Color.White.copy(alpha = 0.3f),
            topLeft = Offset(14f * density, 12f * density),
            size = Size(55f * density, 5f * density),
            cornerRadius = CornerRadius(3f * density)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.18f),
            topLeft = Offset(14f * density, 24f * density),
            size = Size(35f * density, 5f * density),
            cornerRadius = CornerRadius(3f * density)
        )
        // Small circle accent
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = 8f * density,
            center = Offset(size.width - 22f * density, 20f * density)
        )
    }
}