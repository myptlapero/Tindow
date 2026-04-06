package com.vjpro.tindow.domain.model

data class SuggestionHistory(
    val id: Long = 0,
    val query: String = "",
    val mealNames: List<String> = emptyList(),
    val timestamp: Long = 0,
    val meals: List<Meal> = emptyList()
)
