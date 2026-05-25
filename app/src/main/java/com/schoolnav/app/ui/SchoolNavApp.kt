package com.schoolnav.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.schoolnav.app.ui.components.FloatingNotificationHost
import com.schoolnav.app.ui.navigation.SchoolNavGraph
import com.schoolnav.app.ui.theme.SchoolNavTheme

/**
 * Root composable. Hosts the navigation graph and the floating notification overlay
 * that sits on top of every screen.
 *
 * @param onAppReady invoked once after the first frame — used to dismiss the system
 *                   splash screen.
 */
@Composable
fun SchoolNavApp(onAppReady: () -> Unit = {}) {
    SchoolNavTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val navController = rememberNavController()
                SchoolNavGraph(navController = navController)
                FloatingNotificationHost()
            }
        }
        LaunchedEffect(Unit) { onAppReady() }
    }
}
