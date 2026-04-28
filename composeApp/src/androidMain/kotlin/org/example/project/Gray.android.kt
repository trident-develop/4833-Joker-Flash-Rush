package org.example.project

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import org.example.project.model.ScoreSource
import org.example.project.navigation.RouteBus
import org.example.project.navigation.isGame
import org.example.project.navigation.isLoading
import org.example.project.navigation.isRules
import org.example.project.navigation.rememberRouteToken
import org.example.project.screens.JokeScreen
import org.example.project.screens.LoadingScreen
import org.example.project.screens.isFlowersConnected
import org.example.project.utils.ShiftCodec
import org.example.project.utils.ShiftCodec.DM
import org.example.project.viewmodel.LoadingViewModel
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("ContextCastToActivity")
@Composable
actual fun Gray(
    loading: @Composable (() -> Unit),
    noInternet: @Composable ((onRetry: () -> Unit) -> Unit),
    white: @Composable (() -> Unit)
) {
    var showContent by remember { mutableStateOf(false) }
    val context = LocalContext.current as MainActivity
    var retryKey by remember { mutableIntStateOf(0) }
    val isConnected = remember(retryKey) { context.isFlowersConnected() }

    Crossfade(
        targetState = showContent,
        animationSpec = tween(600),
        label = "grayTransition"
    ) { isContent ->
        if (isContent) {
            white()
        } else {
            if (isConnected){
                loading()
                val viewModel: LoadingViewModel = koinViewModel()
                val scoreState = viewModel.scoreState.collectAsState()
                val route = rememberRouteToken()

                LaunchedEffect(Unit) { viewModel.loadScore() }

                LaunchedEffect(scoreState.value) {
                    val result = scoreState.value
//                    log("result = $result")

                    val score = result?.score
                    val source = result?.source

//                    log("score = $score, source = $source")
//                    log("score $score")
                    if (!score.isNullOrBlank()) {
                        if (source == ScoreSource.BUILT){
                            context.show3.loadUrl(score)
                        } else {
                            if (!score.startsWith("${ShiftCodec.decode(DM)}/")) {
                                context.show3.loadUrl(score)
                            } else {
                                RouteBus.game()
                            }
                        }
                    }
                }

                when {
                    route.isLoading() -> loading()
                    route.isGame() -> {
                        LaunchedEffect(Unit) {
                            showContent = true
                        }
                    }
                    route.isRules() -> { JokeScreen() }
                }
            } else {
                noInternet {
                    retryKey++
                }
            }
        }
    }
}
