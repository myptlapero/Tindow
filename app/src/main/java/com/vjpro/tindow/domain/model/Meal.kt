package com.vjpro.tindow.domain.model

data class Meal(
    val name: String = "",
    val imageUrl: String = "",
    val calories: Int = 0,
    val difficulty: String = "",
    val cookingTime: Int = 0,
    val nutrition: NutritionInfo = NutritionInfo(),
    val ingredients: List<Ingredient> = emptyList(),
    val cookingSteps: List<CookingStep> = emptyList()
)
