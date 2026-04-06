package com.vjpro.tindow.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Suggest : Screen("suggest/{query}") {
        fun createRoute(query: String) = "suggest/$query"
    }
    data object MealDetail : Screen("meal_detail")
    data object WeeklyPlan : Screen("weekly_plan")
    data object CookingStep : Screen("cooking_step/{stepIndex}") {
        fun createRoute(stepIndex: Int) = "cooking_step/$stepIndex"
    }
    data object History : Screen("history")
}
