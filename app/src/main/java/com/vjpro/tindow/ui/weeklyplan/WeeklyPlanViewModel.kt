package com.vjpro.tindow.ui.weeklyplan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vjpro.tindow.data.remote.GeminiService
import com.vjpro.tindow.data.repository.WeeklyPlanRepository
import com.vjpro.tindow.domain.model.WeeklyPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeeklyPlanUiState {
    data object Empty : WeeklyPlanUiState()
    data object Loading : WeeklyPlanUiState()
    data class Success(val plan: WeeklyPlan) : WeeklyPlanUiState()
    data class Error(val message: String) : WeeklyPlanUiState()
}

@HiltViewModel
class WeeklyPlanViewModel @Inject constructor(
    private val weeklyPlanRepository: WeeklyPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeeklyPlanUiState>(WeeklyPlanUiState.Loading)
    val uiState: StateFlow<WeeklyPlanUiState> = _uiState.asStateFlow()

    private val _selectedDay = MutableStateFlow(0)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    init {
        loadExistingPlan()
    }

    fun selectDay(index: Int) {
        _selectedDay.value = index
    }

    fun generateNewPlan() {
        Log.d("MyPTL", "WeeklyPlanViewModel.generateNewPlan() called")
        _uiState.value = WeeklyPlanUiState.Loading
        viewModelScope.launch {
            weeklyPlanRepository.generateWeeklyPlan()
                .onSuccess { plan ->
                    Log.d("MyPTL", "WeeklyPlanViewModel.generateNewPlan() SUCCESS: ${plan.days.size} days")
                    _uiState.value = if (plan.days.isEmpty()) {
                        WeeklyPlanUiState.Error("Failed to create meal plan")
                    } else {
                        WeeklyPlanUiState.Success(plan)
                    }
                }
                .onFailure { e ->
                    Log.e("MyPTL", "WeeklyPlanViewModel.generateNewPlan() ERROR: ${e.message}")
                    _uiState.value = WeeklyPlanUiState.Error(GeminiService.getUserFriendlyError(e))
                }
        }
    }

    private fun loadExistingPlan() {
        viewModelScope.launch {
            val plan = weeklyPlanRepository.getLatestPlan()
            Log.d("MyPTL", "WeeklyPlanViewModel.loadExistingPlan() plan=${plan != null}, days=${plan?.days?.size ?: 0}")
            _uiState.value = if (plan != null && plan.days.isNotEmpty()) {
                WeeklyPlanUiState.Success(plan)
            } else {
                WeeklyPlanUiState.Empty
            }
        }
    }
}
