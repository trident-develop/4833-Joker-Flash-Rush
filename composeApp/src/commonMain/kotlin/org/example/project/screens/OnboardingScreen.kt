package org.example.project.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.GradientButton
import org.example.project.platform.PlatformBackHandler
import org.example.project.platform.rememberAppExiter
import org.example.project.theme.AppColors

private data class OnboardingPage(
    val icon: String,
    val title: String,
    val subtitle: String
)

private val pages = listOf(
    OnboardingPage(
        icon = "\uD83C\uDCCF",
        title = "Learn Smarter",
        subtitle = "Study flashcards with spaced repetition and smart review sessions tailored to you"
    ),
    OnboardingPage(
        icon = "\uD83D\uDE80",
        title = "Track Progress",
        subtitle = "Watch your knowledge grow with streaks, stats, and mastery indicators across all your decks"
    ),
    OnboardingPage(
        icon = "\u2728",
        title = "Create & Share",
        subtitle = "Build your own decks or explore thousands of community collections on any topic"
    )
)

@Composable
fun OnboardingFlow(onComplete: (String) -> Unit) {
    var step by remember { mutableStateOf(0) } // 0,1,2 = pages; 3 = name input
    val totalPages = pages.size

    val exitApp = rememberAppExiter()
    PlatformBackHandler(enabled = true) {
        if (step > 0) step -= 1 else exitApp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppColors.DarkChocolate, AppColors.Espresso)))
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (slideInHorizontally(tween(350)) { it / 2 } + fadeIn(tween(350)))
                    .togetherWith(slideOutHorizontally(tween(300)) { -it / 2 } + fadeOut(tween(300)))
            },
            label = "onboarding"
        ) { currentStep ->
            if (currentStep < totalPages) {
                OnboardingPageContent(
                    page = pages[currentStep],
                    currentIndex = currentStep,
                    totalPages = totalPages,
                    onNext = { step = currentStep + 1 }
                )
            } else {
                NameInputContent(onDone = { name -> onComplete(name) })
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    currentIndex: Int,
    totalPages: Int,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        Text(page.icon, fontSize = 64.sp)
        Spacer(Modifier.height(32.dp))

        Text(
            page.title,
            color = AppColors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        Text(
            page.subtitle,
            color = AppColors.TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(Modifier.weight(1f))

        // Dots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(totalPages) { i ->
                val width by animateFloatAsState(
                    if (i == currentIndex) 24f else 8f,
                    tween(300), label = "dot"
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width.dp)
                        .clip(CircleShape)
                        .background(
                            if (i == currentIndex) AppColors.Amber else AppColors.CardSurfaceLight
                        )
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        GradientButton(
            text = if (currentIndex < totalPages - 1) "Continue" else "Get Started",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun NameInputContent(onDone: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(AppColors.DeepRed, AppColors.BurntOrange, AppColors.Amber))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (name.isNotBlank()) name.take(1).uppercase() else "?",
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "What's your name?",
            color = AppColors.TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "We'll use it to personalize your experience",
            color = AppColors.TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 10) name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Your name", color = AppColors.TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Amber,
                unfocusedBorderColor = AppColors.CardSurface,
                cursorColor = AppColors.Amber,
                focusedTextColor = AppColors.TextPrimary,
                unfocusedTextColor = AppColors.TextPrimary,
                focusedContainerColor = AppColors.CardSurface.copy(alpha = 0.5f),
                unfocusedContainerColor = AppColors.CardSurface.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "${name.length}/10",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(Modifier.weight(1f))

        GradientButton(
            text = "Let's Go!",
            onClick = { onDone(name.trim()) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank()
        )

        Spacer(Modifier.height(48.dp))
    }
}
