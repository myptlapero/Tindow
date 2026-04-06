package com.vjpro.tindow.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vjpro.tindow.data.repository.MealRepository
import com.vjpro.tindow.domain.model.SuggestionHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<SuggestionHistory>>(emptyList())
    val history: StateFlow<List<SuggestionHistory>> = _history.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _history.value = mealRepository.getHistory()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            mealRepository.clearHistory()
            _history.value = emptyList()
        }
    }
}
