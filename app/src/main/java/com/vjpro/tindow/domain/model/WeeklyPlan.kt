package com.vjpro.tindow.domain.model

data class WeeklyPlan(
    val days: List<DayPlan> = emptyList()
)

data class DayPlan(
    val dayName: String = "",
    val breakfast: Meal = Meal(),
    val lunch: Meal = Meal(),
    val dinner: Meal = Meal(),
    val totalCalories: Int = 0
)
