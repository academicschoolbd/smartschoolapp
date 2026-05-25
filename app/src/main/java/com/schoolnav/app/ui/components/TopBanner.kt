package com.schoolnav.app.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.schoolnav.app.data.BannerItem
import kotlinx.coroutines.delay

/**
 * Auto-scrolling banner pager. Loops infinitely by mapping the pager's
 * "virtual" page space onto the [items] list with modulo arithmetic.
 *
 * @param autoScrollMillis time between automatic transitions; pass 0 to disable.
 */
@Composable
fun TopBanner(
    items: List<BannerItem>,
    modifier: Modifier = Modifier,
    autoScrollMillis: Long = 4000L,
    onBannerClick: (BannerItem) -> Unit = {},
) {
    if (items.isEmpty()) return

    val pageCount = Int.MAX_VALUE
    val initialPage = pageCount / 2 - (pageCount / 2 % items.size)
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

    if (autoScrollMillis > 0L) {
        LaunchedEffect(pagerState, items.size) {
            while (true) {
                delay(autoScrollMillis)
                val next = (pagerState.currentPage + 1) % pageCount
                pagerState.animateScrollToPage(
                    page = next,
                    animationSpec = tween(durationMillis = 650),
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) { page ->
            val item = items[page % items.size]
            BannerCard(item = item, onClick = { onBannerClick(item) })
        }
        Spacer(modifier = Modifier.height(12.dp))
        PagerIndicator(
            count = items.size,
            currentIndex = pagerState.currentPage % items.size,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun BannerCard(item: BannerItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(item.accent.copy(alpha = 0.18f))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                .data(item.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.55f),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subtitle,
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun PagerIndicator(count: Int, currentIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val selected = index == currentIndex
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (selected) 18.dp else 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
            )
        }
    }
    Spacer(modifier = Modifier.size(0.dp))
}
