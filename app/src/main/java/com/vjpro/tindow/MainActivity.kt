package com.vjpro.tindow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vjpro.tindow.core.base.view.BaseComposeActivity
import com.vjpro.tindow.ui.navigation.AppNavigation
import com.vjpro.tindow.ui.navigation.BottomNavBar
import com.vjpro.tindow.ui.navigation.Screen
import com.vjpro.tindow.ui.theme.TindowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {

    @Composable
    override fun ThemeContent(content: @Composable () -> Unit) {
        TindowTheme { content() }
    }

    @Composable
    override fun ContentView() {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Only show bottom bar on top-level tabs
        val showBottomBar = currentRoute in listOf(
            Screen.Home.route,
            Screen.WeeklyPlan.route
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onItemClick = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
