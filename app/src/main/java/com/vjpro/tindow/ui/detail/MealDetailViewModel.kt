package com.vjpro.tindow.ui.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor() : ViewModel() {

    private val _checkedIngredients = MutableStateFlow<Set<Int>>(emptySet())
    val checkedIngredients: StateFlow<Set<Int>> = _checkedIngredients.asStateFlow()

    fun toggleIngredient(index: Int) {
        _checkedIngredients.value = _checkedIngredients.value.let { current ->
            if (index in current) current - index else current + index
        }
    }
}
