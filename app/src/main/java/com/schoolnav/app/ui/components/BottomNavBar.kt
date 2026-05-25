package com.schoolnav.app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.schoolnav.app.ui.navigation.BottomTab
import com.schoolnav.app.ui.navigation.BottomTabs

/**
 * Material 3 bottom navigation bar with one item per [BottomTab].
 * The selected indicator is driven by the current route from the navController.
 */
@Composable
fun BottomNavBar(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit,
) {
    NavigationBar {
        BottomTabs.forEach { tab ->
            val selected = currentRoute == tab.destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.destination.title) },
                label = { Text(text = tab.destination.title) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

/** Convenience: returns true if [route] is one of the bottom-nav root routes. */
fun isRootTabRoute(route: String?): Boolean =
    BottomTabs.any { it.destination.route == route }
