package com.schoolnav.app.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FloatingNotificationData(
    val title: String,
    val body: String,
)

/**
 * App-wide pub-sub for in-app floating notifications. Anyone — including the
 * Firebase Messaging Service — can call [post] and the
 * `FloatingNotificationHost` composable will render the banner on whichever
 * screen is foregrounded.
 */
object FloatingNotificationCenter {
    private val _current = MutableStateFlow<FloatingNotificationData?>(null)
    val current: StateFlow<FloatingNotificationData?> = _current.asStateFlow()

    fun post(title: String, body: String) {
        _current.value = FloatingNotificationData(title = title, body = body)
    }

    fun dismiss() {
        _current.value = null
    }
}
