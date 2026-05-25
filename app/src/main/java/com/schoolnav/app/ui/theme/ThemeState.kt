package com.schoolnav.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/** Three-state theme preference: System, Light, or Dark. */
enum class ThemeMode { System, Light, Dark }

/**
 * App-wide observable holder for the user's theme preference. Read [mode] to
 * branch on the current setting; call [cycle] to advance to the next mode
 * (System → Light → Dark → System).
 */
@Stable
class ThemeStateHolder(initial: ThemeMode = ThemeMode.System) {
    var mode by mutableStateOf(initial)

    fun cycle() {
        mode = when (mode) {
            ThemeMode.System -> ThemeMode.Light
            ThemeMode.Light -> ThemeMode.Dark
            ThemeMode.Dark -> ThemeMode.System
        }
    }
}

@Composable
fun rememberThemeState(initial: ThemeMode = ThemeMode.System): ThemeStateHolder =
    remember { ThemeStateHolder(initial) }

/** CompositionLocal so any composable can read the current theme preference. */
val LocalThemeState = staticCompositionLocalOf<ThemeStateHolder> {
    error("ThemeStateHolder not provided. Wrap your content in SchoolNavApp().")
}
