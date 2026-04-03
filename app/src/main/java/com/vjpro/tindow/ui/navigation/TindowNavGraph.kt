package com.vjpro.tindow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vjpro.tindow.ui.GameViewModel
import com.vjpro.tindow.ui.home.HomeScreen
import com.vjpro.tindow.ui.input.InputScreen
import com.vjpro.tindow.ui.result.ResultScreen
import com.vjpro.tindow.ui.swipe.SwipeScreen
import com.vjpro.tindow.ui.topic.TopicScreen

@Composable
fun TindowNavGraph(
    navController: NavHostController,
    viewModel: GameViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = TindowRoute.Home.route
    ) {
        composable(TindowRoute.Home.route) {
            HomeScreen(
                onStartGame = { mode ->
                    viewModel.setMode(mode)
                    navController.navigate(TindowRoute.Topic.route)
                }
            )
        }

        composable(TindowRoute.Topic.route) {
            TopicScreen(
                onTopicSelected = { topic ->
                    viewModel.setTopic(topic)
                    navController.navigate(TindowRoute.Input.route)
                },
                onCustomTopic = { custom ->
                    viewModel.setCustomTopic(custom)
                    navController.navigate(TindowRoute.Input.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(TindowRoute.Input.route) {
            InputScreen(
                viewModel = viewModel,
                onStartSwiping = {
                    viewModel.startGame()
                    navController.navigate(TindowRoute.Swipe.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(TindowRoute.Swipe.route) {
            SwipeScreen(
                viewModel = viewModel,
                onFinished = {
                    navController.navigate(TindowRoute.Result.route) {
                        popUpTo(TindowRoute.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(TindowRoute.Result.route) {
            ResultScreen(
                viewModel = viewModel,
                onPlayAgain = {
                    viewModel.reset()
                    navController.navigate(TindowRoute.Home.route) {
                        popUpTo(TindowRoute.Home.route) { inclusive = true }
                    }
                },
                onHome = {
                    viewModel.reset()
                    navController.navigate(TindowRoute.Home.route) {
                        popUpTo(TindowRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
