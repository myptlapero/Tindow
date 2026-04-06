package com.vjpro.tindow.ui.cooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CookingStepViewModel @Inject constructor() : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    fun setStep(index: Int) {
        _currentStep.value = index
        stopTimer()
    }

    fun nextStep(totalSteps: Int) {
        if (_currentStep.value < totalSteps - 1) {
            _currentStep.value++
            stopTimer()
        }
    }

    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value--
            stopTimer()
        }
    }

    fun startTimer(durationMinutes: Int) {
        stopTimer()
        _timerSeconds.value = durationMinutes * 60
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _timerSeconds.value--
            }
            _isTimerRunning.value = false
        }
    }

    fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        _timerSeconds.value = 0
    }

    fun formatTimer(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }
}
