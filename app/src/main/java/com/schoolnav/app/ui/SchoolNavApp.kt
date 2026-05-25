package com.schoolnav.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.schoolnav.app.notifications.FloatingNotificationCenter
import com.schoolnav.app.ui.components.AppDrawer
import com.schoolnav.app.ui.components.BottomNavBar
import com.schoolnav.app.ui.components.FloatingNotificationHost
import com.schoolnav.app.ui.components.isRootTabRoute
import com.schoolnav.app.ui.navigation.Destination
import com.schoolnav.app.ui.navigation.SchoolNavGraph
import com.schoolnav.app.ui.theme.LocalThemeState
import com.schoolnav.app.ui.theme.SchoolNavTheme
import com.schoolnav.app.ui.theme.ThemeMode
import com.schoolnav.app.ui.theme.rememberThemeState
import kotlinx.coroutines.launch

/**
 * Root composable. Owns the theme state, navigation controller, modal navigation
 * drawer, and the Scaffold chrome (top app bar, bottom navigation, FAB). The
 * navigation graph and the floating notification overlay live inside the scaffold
 * body.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolNavApp(onAppReady: () -> Unit = {}) {
    val themeState = rememberThemeState()
    CompositionLocalProvider(LocalThemeState provides themeState) {
        SchoolNavTheme(themeMode = themeState.mode) {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val currentDestination = Destination.fromRoute(currentRoute)
            val isRootTab = isRootTabRoute(currentRoute)
            val title = currentDestination?.title ?: "School Nav"

            fun navigateTo(destination: Destination) {
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = isRootTab,
                drawerContent = {
                    AppDrawer(
                        currentRoute = currentRoute,
                        onDestinationClick = { destination ->
                            scope.launch { drawerState.close() }
                            navigateTo(destination)
                        },
                    )
                },
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                },
                                navigationIcon = {
                                    if (isRootTab) {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(
                                                imageVector = Icons.Filled.Menu,
                                                contentDescription = "Open navigation drawer",
                                            )
                                        }
                                    } else {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { themeState.cycle() }) {
                                        Icon(
                                            imageVector = when (themeState.mode) {
                                                ThemeMode.System -> Icons.Filled.SettingsBrightness
                                                ThemeMode.Light -> Icons.Filled.LightMode
                                                ThemeMode.Dark -> Icons.Filled.DarkMode
                                            },
                                            contentDescription = "Toggle theme (current: ${themeState.mode.name})",
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            )
                        },
                        bottomBar = {
                            if (isRootTab) {
                                BottomNavBar(
                                    currentRoute = currentRoute,
                                    onTabSelected = { tab -> navigateTo(tab.destination) },
                                )
                            }
                        },
                        floatingActionButton = {
                            if (currentRoute == Destination.Home.route) {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        FloatingNotificationCenter.post(
                                            title = "Demo notification",
                                            body = "This is what an in-app floating notification looks like.",
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.NotificationsActive,
                                            contentDescription = null,
                                        )
                                    },
                                    text = { Text(text = "Notify") },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        ) {
                            SchoolNavGraph(
                                navController = navController,
                                onNavigate = ::navigateTo,
                            )
                            FloatingNotificationHost()
                        }
                    }
                }
            }

            LaunchedEffect(Unit) { onAppReady() }
        }
    }
}
