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
import com.schoolnav.app.ui.components.NoticeStrip
import com.schoolnav.app.ui.components.QuickStatsRow
import com.schoolnav.app.ui.components.TopBanner
import com.schoolnav.app.ui.components.WelcomeHeader
import com.schoolnav.app.ui.navigation.Destination

/**
 * Home dashboard. From top to bottom:
 *
 * 1. Greeting header with avatar + role badge
 * 2. Auto-scrolling banner pager
 * 3. Quick-stats horizontal row
 * 4. Recent Notices preview strip
 * 5. Academic / Teacher / Student Corner / Communication / Resources /
 *    Important grid sections (each renders via [GridSection])
 *
 * Scrolling is owned by this LazyColumn; the parent Scaffold supplies all
 * surrounding chrome (top app bar, bottom nav, FAB).
 */
@Composable
fun HomeScreen(onNavigate: (Destination) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { WelcomeHeader() }
        item { TopBanner(items = HomeData.bannerItems) }
        item { QuickStatsRow(onItemClick = onNavigate) }
        item {
            NoticeStrip(
                onSeeAllClick = { onNavigate(Destination.NoticeBoard) },
                onNoticeClick = { onNavigate(Destination.NoticeBoard) },
            )
        }
        items(HomeData.allSections) { section ->
            GridSection(
                section = section,
                onItemClick = { item -> onNavigate(item.destination) },
            )
        }
    }
}
