package com.schoolnav.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Sweeps a diagonal highlight band across the modifier's bounds — the classic
 * "shimmer" placeholder effect. Pair with a solid background to get a skeleton
 * card that visibly animates while content loads.
 */
fun Modifier.shimmer(
    highlight: Color = Color.White.copy(alpha = 0.55f),
    durationMillis: Int = 1_200,
): Modifier = composed {
    var size by remember { mutableStateOf(Size.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer-transition")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer-progress",
    )

    val base = MaterialTheme.colorScheme.surfaceVariant
    val transparent = base.copy(alpha = 0f)

    this
        .onSizeChanged { size = Size(it.width.toFloat(), it.height.toFloat()) }
        .drawWithContent {
            drawContent()
            if (size.width <= 0f || size.height <= 0f) return@drawWithContent
            val band = size.width * 0.6f
            val start = -band + (size.width + band) * progress
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(transparent, highlight, transparent),
                    start = Offset(start, 0f),
                    end = Offset(start + band, size.height),
                ),
                size = size,
            )
        }
}
