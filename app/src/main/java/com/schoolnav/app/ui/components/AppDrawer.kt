package com.schoolnav.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.unit.dp
import com.schoolnav.app.data.HomeData
import com.schoolnav.app.ui.navigation.BottomTabs
import com.schoolnav.app.ui.navigation.Destination

/**
 * Content of the modal navigation drawer. Lists every destination grouped by
 * the section it belongs to, plus the bottom-nav tabs at the top. The currently
 * selected route is highlighted using Material 3 `NavigationDrawerItem` semantics.
 */
@Composable
fun AppDrawer(
    currentRoute: String?,
    onDestinationClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            DrawerHeader()
            Spacer(modifier = Modifier.height(8.dp))

            DrawerGroupLabel("Main")
            BottomTabs.forEach { tab ->
                DrawerItem(
                    label = tab.destination.title,
                    icon = tab.icon,
                    selected = currentRoute == tab.destination.route,
                    onClick = { onDestinationClick(tab.destination) },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            DrawerGroupLabel(HomeData.academic.title)
            HomeData.academic.items.forEach { item ->
                DrawerItem(
                    label = item.label,
                    icon = item.icon,
                    selected = currentRoute == item.destination.route,
                    onClick = { onDestinationClick(item.destination) },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            DrawerGroupLabel(HomeData.teacher.title)
            HomeData.teacher.items.forEach { item ->
                DrawerItem(
                    label = item.label,
                    icon = item.icon,
                    selected = currentRoute == item.destination.route,
                    onClick = { onDestinationClick(item.destination) },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            DrawerGroupLabel(HomeData.important.title)
            HomeData.important.items.forEach { item ->
                DrawerItem(
                    label = item.label,
                    icon = item.icon,
                    selected = currentRoute == item.destination.route,
                    onClick = { onDestinationClick(item.destination) },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = "School Nav",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "Modern school dashboard",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            )
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
