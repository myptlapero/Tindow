package com.vjpro.tindow.domain.model

data class CookingStep(
    val stepNumber: Int = 0,
    val title: String = "",
    val description: String = "",
    val duration: Int = 0,
    val ingredients: List<Ingredient> = emptyList()
)
