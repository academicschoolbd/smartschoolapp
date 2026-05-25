package com.schoolnav.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolnav.app.data.HomeData
import com.schoolnav.app.ui.navigation.BottomTabs
import com.schoolnav.app.ui.navigation.Destination
import com.schoolnav.app.web.isVisibleWithoutAuth

/**
 * Content of the modal navigation drawer. Shows a school-branding header at
 * the top and lists every destination grouped by the section it belongs to,
 * plus the bottom-nav tabs at the top. The currently selected route is
 * highlighted using Material 3 `NavigationDrawerItem` semantics.
 *
 * A small **account section** at the very top reflects whether the user is
 * signed into the Ramom Smart School portal — tapping it routes to the
 * native [com.schoolnav.app.ui.screens.LoginScreen] (or signs out).
 */
@Composable
fun AppDrawer(
    currentRoute: String?,
    onDestinationClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    schoolName: String = "School Nav",
    schoolTagline: String = "Public school dashboard",
    isSignedIn: Boolean = false,
    signedInDisplayName: String? = null,
    onSignInClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onRateClick: () -> Unit = {},
) {
    ModalDrawerSheet(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            DrawerHeader(name = schoolName, tagline = schoolTagline)
            Spacer(modifier = Modifier.height(8.dp))

            DrawerGroupLabel("Account")
            if (isSignedIn) {
                DrawerItem(
                    label = signedInDisplayName ?: "Signed in",
                    icon = Icons.Filled.AccountCircle,
                    selected = false,
                    onClick = { onDestinationClick(Destination.Profile) },
                )
                DrawerItem(
                    label = "Sign out",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    selected = false,
                    onClick = onSignOutClick,
                )
            } else {
                DrawerItem(
                    label = "Sign in",
                    icon = Icons.AutoMirrored.Filled.Login,
                    selected = currentRoute == Destination.Login.route,
                    onClick = onSignInClick,
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            DrawerGroupLabel("Main")
            BottomTabs.forEach { tab ->
                // Hide bottom-tab destinations that require auth from signed-out users
                // (e.g. a personal-dashboard tab). The Home tab itself is always public.
                if (isSignedIn || tab.destination.isVisibleWithoutAuth()) {
                    DrawerItem(
                        label = tab.destination.title,
                        icon = tab.icon,
                        selected = currentRoute == tab.destination.route,
                        onClick = { onDestinationClick(tab.destination) },
                    )
                }
            }

            HomeData.sectionsFor(isSignedIn = isSignedIn).forEach { section ->
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                DrawerGroupLabel(section.title)
                section.items.forEach { item ->
                    DrawerItem(
                        label = item.label,
                        icon = item.icon,
                        selected = currentRoute == item.destination.route,
                        onClick = { onDestinationClick(item.destination) },
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            DrawerGroupLabel("More")
            DrawerItem(
                label = "Rate us",
                icon = Icons.Filled.Star,
                selected = false,
                onClick = onRateClick,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DrawerHeader(name: String, tagline: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun DrawerGroupLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        label = { Text(text = label) },
        icon = { Icon(imageVector = icon, contentDescription = null) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    )
}
