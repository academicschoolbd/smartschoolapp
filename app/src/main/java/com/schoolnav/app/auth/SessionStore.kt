package com.schoolnav.app.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.sessionDataStore by preferencesDataStore(name = "school_nav_session")

/**
 * Persists the authenticated user's identity and the raw session cookies
 * returned by Ramom Smart School's login endpoint.
 *
 * The cookies are stored as a single newline-separated string in the form
 * `<name>=<value>` so that we can replay them through Android's
 * [android.webkit.CookieManager] on app start, before the first WebView is
 * created. The browser is the source of truth for cookies during a session —
 * we only persist them across cold starts.
 */
class SessionStore(private val context: Context) {

    private val displayNameKey = stringPreferencesKey("user_display_name")
    private val usernameKey = stringPreferencesKey("username")
    private val cookieDomainKey = stringPreferencesKey("cookie_domain")
    private val cookieBlobKey = stringPreferencesKey("cookie_blob")

    val state: Flow<SessionState> = context.sessionDataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            val username = prefs[usernameKey]
            if (username.isNullOrBlank()) {
                SessionState.SignedOut
            } else {
                SessionState.SignedIn(
                    username = username,
                    displayName = prefs[displayNameKey] ?: username,
                    cookieDomain = prefs[cookieDomainKey] ?: "",
                    cookieBlob = prefs[cookieBlobKey] ?: "",
                )
            }
        }

    suspend fun saveSignIn(
        username: String,
        displayName: String,
        cookieDomain: String,
        cookies: List<Pair<String, String>>,
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[usernameKey] = username
            prefs[displayNameKey] = displayName
            prefs[cookieDomainKey] = cookieDomain
            prefs[cookieBlobKey] = cookies.joinToString("\n") { (name, value) -> "$name=$value" }
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }
}

sealed class SessionState {
    data object SignedOut : SessionState()
    data class SignedIn(
        val username: String,
        val displayName: String,
        val cookieDomain: String,
        val cookieBlob: String,
    ) : SessionState() {
        /** Parses [cookieBlob] back into a list of `(name, value)` pairs. */
        val cookies: List<Pair<String, String>>
            get() = cookieBlob
                .lineSequence()
                .filter { it.isNotBlank() }
                .mapNotNull { line ->
                    val eq = line.indexOf('=')
                    if (eq <= 0) null else line.substring(0, eq) to line.substring(eq + 1)
                }
                .toList()
    }
}
