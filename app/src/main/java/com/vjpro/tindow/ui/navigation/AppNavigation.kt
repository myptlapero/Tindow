package com.vjpro.tindow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vjpro.tindow.ui.cooking.CookingStepScreen
import com.vjpro.tindow.ui.detail.MealDetailScreen
import com.vjpro.tindow.ui.history.HistoryScreen
import com.vjpro.tindow.ui.home.HomeScreen
import com.vjpro.tindow.ui.suggest.SuggestScreen
import com.vjpro.tindow.ui.suggest.SuggestViewModel
import com.vjpro.tindow.ui.weeklyplan.WeeklyPlanScreen
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSuggest = { query ->
                    val encoded = URLEncoder.encode(query, "UTF-8")
                    navController.navigate(Screen.Suggest.createRoute(encoded))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onMealClick = { meal ->
                    NavigationState.selectedMeal = meal
                    navController.navigate(Screen.MealDetail.route)
                }
            )
        }

        composable(Screen.Suggest.route) { backStackEntry ->
            val query = URLDecoder.decode(
                backStackEntry.arguments?.getString("query") ?: "", "UTF-8"
            )
            val viewModel: SuggestViewModel = hiltViewModel()
            LaunchedEffect(query) {
                viewModel.suggestMeals(query)
            }
            SuggestScreen(
                onBack = { navController.popBackStack() },
                onMealClick = { meal ->
                    NavigationState.selectedMeal = meal
                    navController.navigate(Screen.MealDetail.route)
                },
                viewModel = viewModel
            )
        }

        composable(Screen.MealDetail.route) {
            val meal = NavigationState.selectedMeal
            if (meal != null) {
                MealDetailScreen(
                    meal = meal,
                    onBack = { navController.popBackStack() },
                    onCookingStepClick = { stepIndex ->
                        navController.navigate(Screen.CookingStep.createRoute(stepIndex))
                    }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable(Screen.CookingStep.route) { backStackEntry ->
            val stepIndex = backStackEntry.arguments?.getString("stepIndex")?.toIntOrNull() ?: 0
            val meal = NavigationState.selectedMeal
            if (meal != null) {
                CookingStepScreen(
                    meal = meal,
                    initialStep = stepIndex,
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable(Screen.WeeklyPlan.route) {
            WeeklyPlanScreen(
                onMealClick = { meal ->
                    NavigationState.selectedMeal = meal
                    navController.navigate(Screen.MealDetail.route)
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onHistoryClick = { history ->
                    val query = URLEncoder.encode(history.query, "UTF-8")
                    navController.navigate(Screen.Suggest.createRoute(query))
                }
            )
        }
    }
}
