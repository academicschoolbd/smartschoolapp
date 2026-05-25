package com.schoolnav.app.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import com.schoolnav.app.auth.SessionState
import com.schoolnav.app.auth.rememberAuthViewModel
import com.schoolnav.app.rating.AppRating
import com.schoolnav.app.tenant.ActiveTenant
import com.schoolnav.app.ui.components.AppDrawer
import com.schoolnav.app.ui.components.BottomNavBar
import com.schoolnav.app.ui.components.FloatingNotificationHost
import com.schoolnav.app.ui.components.QuickAction
import com.schoolnav.app.ui.components.QuickActionsFab
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
            val title = currentDestination?.title ?: ActiveTenant.displayName

            val authVm = rememberAuthViewModel()
            val session by authVm.session.collectAsState()
            val isSignedIn = session is SessionState.SignedIn
            val context = LocalContext.current

            // Double-tap-to-exit: while on Home, the first back press shows a
            // toast; a second press within 2 s actually exits. Anywhere else,
            // back falls through to the navigation stack as usual.
            var lastBackPressMillis by remember { mutableLongStateOf(0L) }
            BackHandler(enabled = currentRoute == Destination.Home.route) {
                val now = System.currentTimeMillis()
                if (now - lastBackPressMillis < 2_000L) {
                    (context as? Activity)?.finish()
                } else {
                    lastBackPressMillis = now
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }

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
                        isSignedIn = isSignedIn,
                        signedInDisplayName = (session as? SessionState.SignedIn)?.displayName,
                        onDestinationClick = { destination ->
                            scope.launch { drawerState.close() }
                            navigateTo(destination)
                        },
                        onSignInClick = {
                            scope.launch { drawerState.close() }
                            navigateTo(Destination.Login)
                        },
                        onSignOutClick = {
                            scope.launch { drawerState.close() }
                            authVm.signOut()
                        },
                        onRateClick = {
                            scope.launch { drawerState.close() }
                            (context as? Activity)?.let { AppRating.request(it) }
                        },
                        schoolName = ActiveTenant.displayName,
                        schoolTagline = ActiveTenant.tagline,
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
                                    if (isSignedIn) {
                                        IconButton(onClick = { authVm.signOut() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                                contentDescription = "Sign out",
                                            )
                                        }
                                    } else if (currentRoute != Destination.Login.route) {
                                        IconButton(onClick = { navigateTo(Destination.Login) }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Login,
                                                contentDescription = "Sign in",
                                            )
                                        }
                                    }
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
                        // The speed-dial FAB is rendered as part of the body
                        // (see below) so it can paint its scrim on top of the
                        // grid without being clipped by the Scaffold's FAB slot.
                        floatingActionButton = { },
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

                            if (currentRoute == Destination.Home.route) {
                                val quickActions = remember(isSignedIn, session) {
                                    buildQuickActions(
                                        isSignedIn = isSignedIn,
                                        onNavigate = ::navigateTo,
                                        onRate = {
                                            (context as? Activity)?.let { AppRating.request(it) }
                                        },
                                    )
                                }
                                QuickActionsFab(
                                    actions = quickActions,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(Unit) { onAppReady() }
        }
    }
}

/**
 * Builds the speed-dial entries shown on Home. The set depends on whether the
 * user is signed in so they always see the most relevant primary actions.
 */
private fun buildQuickActions(
    isSignedIn: Boolean,
    onNavigate: (Destination) -> Unit,
    onRate: () -> Unit,
): List<QuickAction> {
    val rate = QuickAction(label = "Rate the app", icon = Icons.Filled.Star, onClick = onRate)
    val notice = QuickAction(
        label = "Notices",
        icon = Icons.Filled.Campaign,
        onClick = { onNavigate(Destination.NoticeBoard) },
    )
    val contact = QuickAction(
        label = "Contact school",
        icon = Icons.Filled.Phone,
        onClick = { onNavigate(Destination.Contact) },
    )
    val signIn = QuickAction(
        label = "Sign in",
        icon = Icons.AutoMirrored.Filled.Login,
        onClick = { onNavigate(Destination.Login) },
    )
    val profile = QuickAction(
        label = "My profile",
        icon = Icons.Filled.Person,
        onClick = { onNavigate(Destination.Profile) },
    )
    val dashboard = QuickAction(
        label = "Dashboard",
        icon = Icons.Filled.Dashboard,
        onClick = { onNavigate(Destination.Dashboard) },
    )
    return if (isSignedIn) {
        listOf(notice, dashboard, profile, rate)
    } else {
        listOf(notice, contact, signIn, rate)
    }
}
