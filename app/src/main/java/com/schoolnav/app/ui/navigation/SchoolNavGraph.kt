package com.schoolnav.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.schoolnav.app.ui.screens.HomeScreen
import com.schoolnav.app.ui.screens.LoginScreen
import com.schoolnav.app.ui.screens.WebFeatureScreen
import com.schoolnav.app.web.webPage

/**
 * Wires every [Destination] into the [NavHost].
 *
 * - [Destination.Home] renders the existing home dashboard (banner pager,
 *   quick-stats, recent-notices strip, 5 grid sections).
 * - [Destination.Login] renders the native sign-in form.
 * - **Every other destination** renders a dedicated [WebFeatureScreen] that
 *   opens the matching Ramom Smart School page (mapping lives in
 *   [com.schoolnav.app.web.webPage]). Auth gating, CSS injection, skeleton
 *   loader, pull-to-refresh and the error state all live inside
 *   `WebFeatureScreen` — this graph stays a thin wrapper.
 *
 * @param navController the host's nav controller
 * @param onNavigate called when a screen wants to navigate to another
 *  destination. The implementation lives in `SchoolNavApp` so that it can
 *  apply bottom-nav-style options (`popUpTo`, `launchSingleTop`, etc.).
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
        composable(Destination.Login.route) {
            LoginScreen(onSuccess = { navController.popBackStack() })
        }

        Destination.entries
            .filter { it != Destination.Home && it != Destination.Login }
            .forEach { dest ->
                composable(dest.route) {
                    WebFeatureScreen(
                        page = dest.webPage(),
                        onRequestLogin = { onNavigate(Destination.Login) },
                    )
                }
            }
    }
}
