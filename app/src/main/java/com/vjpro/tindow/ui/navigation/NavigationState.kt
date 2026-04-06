package com.vjpro.tindow.ui.navigation

import com.vjpro.tindow.domain.model.Meal

/**
 * In-memory holder for passing Meal data between screens.
 * Avoids URL-encoding large JSON as nav arguments which can hit size limits.
 */
object NavigationState {
    var selectedMeal: Meal? = null
}
