package com.schoolnav.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.schoolnav.app.ui.screens.DemoScreen
import com.schoolnav.app.ui.screens.HomeScreen

/**
 * Wires every [Destination] into the [NavHost]. The Home destination renders the
 * real home dashboard; every other destination renders the shared [DemoScreen]
 * placeholder for now. The parent Scaffold (in `SchoolNavApp`) owns the top app
 * bar, bottom navigation and FAB — individual screens render content only.
 *
 * @param navController the host's nav controller
 * @param onNavigate called when a screen wants to navigate to another destination.
 *                   The implementation lives in `SchoolNavApp` so that it can apply
 *                   bottom-nav-style options (`popUpTo`, `launchSingleTop`, etc.).
 */
@Composable
fun SchoolNavGraph(
    navController: NavHostController,
    onNavigate: (Destination) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
    ) {
        composable(Destination.Home.route) {
            HomeScreen(onNavigate = onNavigate)
        }

        Destination.entries
            .filter { it != Destination.Home }
            .forEach { dest ->
                composable(dest.route) {
                    DemoScreen(title = dest.title)
                }
            }
    }
}
