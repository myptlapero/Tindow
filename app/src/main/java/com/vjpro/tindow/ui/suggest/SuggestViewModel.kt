package com.vjpro.tindow.ui.suggest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vjpro.tindow.data.remote.GeminiService
import com.vjpro.tindow.data.repository.MealRepository
import com.vjpro.tindow.domain.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SuggestUiState {
    data object Loading : SuggestUiState()
    data class Success(val meals: List<Meal>) : SuggestUiState()
    data class Error(val message: String) : SuggestUiState()
}

@HiltViewModel
class SuggestViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SuggestUiState>(SuggestUiState.Loading)
    val uiState: StateFlow<SuggestUiState> = _uiState.asStateFlow()

    private var lastQuery: String? = null

    fun suggestMeals(input: String) {
        // Skip if already loaded for this query
        if (input == lastQuery && _uiState.value is SuggestUiState.Success) return
        lastQuery = input
        Log.d("MyPTL", "SuggestViewModel.suggestMeals() input: $input")
        _uiState.value = SuggestUiState.Loading
        viewModelScope.launch {
            val result = mealRepository.suggestMeals(input)
            result.onSuccess { meals ->
                Log.d("MyPTL", "SuggestViewModel.suggestMeals() SUCCESS: ${meals.size} meals")
                _uiState.value = if (meals.isEmpty()) {
                    SuggestUiState.Error("No matching dishes found")
                } else {
                    SuggestUiState.Success(meals)
                }
            }.onFailure { e ->
                Log.e("MyPTL", "SuggestViewModel.suggestMeals() ERROR: ${e.message}")
                _uiState.value = SuggestUiState.Error(GeminiService.getUserFriendlyError(e))
            }
        }
    }
}
