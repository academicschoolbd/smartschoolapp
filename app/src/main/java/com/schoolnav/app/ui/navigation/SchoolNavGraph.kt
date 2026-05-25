package com.schoolnav.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.schoolnav.app.ui.screens.DemoScreen
import com.schoolnav.app.ui.screens.HomeScreen

@Composable
fun SchoolNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
    ) {
        composable(Destination.Home.route) {
            HomeScreen(onNavigate = { dest -> navController.navigate(dest.route) })
        }

        // Every other destination renders the shared DemoScreen for now. The real
        // implementations will be filled in incrementally; the navigation graph is
        // the single source of truth so screens can be replaced without touching
        // the home grid wiring.
        Destination.entries
            .filter { it != Destination.Home }
            .forEach { dest ->
                composable(dest.route) {
                    DemoScreen(
                        title = dest.title,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
    }
}
