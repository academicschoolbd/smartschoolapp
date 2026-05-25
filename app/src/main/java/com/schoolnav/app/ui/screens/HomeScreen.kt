package com.schoolnav.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolnav.app.data.HomeData
import com.schoolnav.app.ui.components.GridSection
import com.schoolnav.app.ui.components.TopBanner
import com.schoolnav.app.ui.navigation.Destination

/**
 * Home dashboard: top auto-scrolling banner and the Academic / Teacher /
 * Important grids. Scrolling is owned by this LazyColumn; the parent Scaffold
 * supplies all surrounding chrome (top app bar, bottom nav, FAB).
 */
@Composable
fun HomeScreen(onNavigate: (Destination) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { TopBanner(items = HomeData.bannerItems) }
        items(HomeData.allSections) { section ->
            GridSection(
                section = section,
                onItemClick = { item -> onNavigate(item.destination) },
            )
        }
    }
}
