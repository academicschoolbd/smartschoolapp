package com.schoolnav.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schoolnav.app.data.GridItem
import com.schoolnav.app.data.HomeSection

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Renders a [HomeSection] as a header + non-scrollable grid of [GridButton]s.
 * The grid uses [LazyVerticalGrid] with `userScrollEnabled = false` so the parent
 * `LazyColumn` in `HomeScreen` owns vertical scrolling for the whole page.
 */
@Composable
fun GridSection(
    section: HomeSection,
    onItemClick: (GridItem) -> Unit,
    columns: Int = 3,
    tileSize: androidx.compose.ui.unit.Dp = 116.dp,
    modifier: Modifier = Modifier,
) {
    val rows = (section.items.size + columns - 1) / columns
    val gap = 12.dp
    val totalHeight = tileSize * rows + gap * (rows - 1).coerceAtLeast(0)

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        SectionHeader(title = section.title)
        Spacer(modifier = Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalArrangement = Arrangement.spacedBy(gap),
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight),
        ) {
            items(section.items) { item ->
                GridButton(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
fun GridButton(
    item: GridItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.tint.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = item.tint,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                textAlign = TextAlign.Center,
            )
        }
    }
}
