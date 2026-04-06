package com.vjpro.tindow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vjpro.tindow.data.repository.MealRepository
import com.vjpro.tindow.domain.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val textInput: String = "",
    val recentMeals: List<Meal> = emptyList(),
    val recentHistory: List<String> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRecentHistory()
    }

    fun onTextChanged(text: String) {
        _uiState.update { it.copy(textInput = text) }
    }

    private fun loadRecentHistory() {
        viewModelScope.launch {
            try {
                val history = mealRepository.getHistory()
                val recentQueries = history.take(5).map { it.query }.distinct()
                val recentMeals = history
                    .flatMap { it.meals }
                    .distinctBy { it.name }
                    .take(6)
                _uiState.update {
                    it.copy(recentHistory = recentQueries, recentMeals = recentMeals)
                }
            } catch (_: Exception) { }
        }
    }

    fun refreshHistory() = loadRecentHistory()
}
