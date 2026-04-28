package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.model.ScoreResult
import org.example.project.state.SolutionUseCase

class LoadingViewModel(
    private val solutionUseCase: SolutionUseCase
) : ViewModel() {

    private val _scoreState = MutableStateFlow<ScoreResult?>(null)
    val scoreState: StateFlow<ScoreResult?> = _scoreState

    fun loadScore() {
        viewModelScope.launch {
            val result = solutionUseCase()
            _scoreState.value = result
        }
    }
}