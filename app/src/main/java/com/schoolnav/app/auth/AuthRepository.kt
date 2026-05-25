package com.schoolnav.app.auth

import android.util.Log
import android.webkit.CookieManager
import com.schoolnav.app.tenant.ActiveTenant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "AuthRepository"

private const val MOBILE_UA =
    "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

private const val CSRF_TOKEN_FIELD = "school_csrf_name"

/**
 * Wraps the native login flow against the Ramom Smart School backend
 * (`POST /authentication` with email/username + password + a CSRF cookie
 * that's echoed back as a hidden form field).
 *
 * After a successful login this also pushes the resulting session cookies
 * into Android's global [CookieManager] so the embedded WebViews see the
 * user as logged in without any additional handshake.
 */
class AuthRepository(private val sessionStore: SessionStore) {

    /** Per-base-host cookie jar keeps the login `POST` self-contained. */
    private val jar = InMemoryCookieJar()

    private val http: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(jar)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    sealed class Result {
        data class Success(val displayName: String) : Result()
        data class Failure(val message: String) : Result()
    }

    /**
     * Restore cookies persisted from a previous session into [CookieManager]
     * so the very first WebView opened after a cold start is already logged in.
     */
    fun primeCookieManagerFromSavedSession(state: SessionState.SignedIn) {
        if (state.cookieDomain.isBlank()) return
        val cm = CookieManager.getInstance()
        cm.setAcceptCookie(true)
        val urlScheme = "https://${state.cookieDomain}"
        state.cookies.forEach { (name, value) ->
            cm.setCookie(urlScheme, "$name=$value; Path=/; Secure")
        }
        cm.flush()
    }

    /**
     * Performs a complete email/password authentication against
     * `${ActiveTenant.baseUrl}/authentication`. On success the session
     * cookies are persisted to [sessionStore] **and** pushed into the
     * Android [CookieManager].
     */
    suspend fun signIn(emailOrUsername: String, password: String): Result =
        withContext(Dispatchers.IO) {
            jar.clear()
            val loginUrl = ActiveTenant.loginUrl
            try {
                // 1. GET the login page to obtain the CSRF cookie + token.
                val getReq = Request.Builder()
                    .url(loginUrl)
                    .header("User-Agent", MOBILE_UA)
                    .build()
                val getResp = http.newCall(getReq).execute()
                val getBody = getResp.use { it.body?.string().orEmpty() }
                val csrfToken = extractCsrfToken(getBody)
                if (csrfToken.isNullOrBlank()) {
                    return@withContext Result.Failure("Could not load login page")
                }

                // 2. POST credentials. Ramom uses 302 → /dashboard on success
                //    and a 302 → /authentication?... on failure.
                val form = FormBody.Builder()
                    .add(CSRF_TOKEN_FIELD, csrfToken)
                    .add("email", emailOrUsername)
                    .add("password", password)
                    .add("rememberme", "")
                    .build()
                val postReq = Request.Builder()
                    .url(loginUrl)
                    .header("User-Agent", MOBILE_UA)
                    .header("Referer", loginUrl)
                    .post(form)
                    .build()

                val postResp = http.newCall(postReq).execute()
                val finalUrl = postResp.request.url.toString()
                postResp.close()

                val landedOnDashboard = finalUrl.contains("/dashboard", ignoreCase = true)
                if (!landedOnDashboard) {
                    return@withContext Result.Failure("Invalid email/username or password")
                }

                // 3. Capture every cookie the server gave us and persist them.
                val cookies = jar
                    .allFor(ActiveTenant.baseUrl.toHttpUrlOrNullSafe())
                    .filter { it.name != CSRF_TOKEN_FIELD }
                    .map { it.name to it.value }
                val host = ActiveTenant.host
                sessionStore.saveSignIn(
                    username = emailOrUsername,
                    displayName = emailOrUsername,
                    cookieDomain = host,
                    cookies = cookies,
                )
                val cm = CookieManager.getInstance()
                cm.setAcceptCookie(true)
                cookies.forEach { (name, value) ->
                    cm.setCookie(ActiveTenant.baseUrl, "$name=$value; Path=/; Secure")
                }
                cm.flush()
                Result.Success(displayName = emailOrUsername)
            } catch (t: Throwable) {
                Log.w(TAG, "Login failed", t)
                Result.Failure(t.message ?: "Network error")
            }
        }

    /** Clears the persisted session and wipes WebView cookies for the tenant host. */
    suspend fun signOut() {
        sessionStore.clear()
        jar.clear()
        val cm = CookieManager.getInstance()
        cm.removeAllCookies(null)
        cm.flush()
    }

    private fun extractCsrfToken(html: String): String? {
        // Matches: <input ... name="school_csrf_name" value="HEX" />
        val pattern = Regex(
            """name\s*=\s*"school_csrf_name"[^>]*value\s*=\s*"([^"]+)"""",
            RegexOption.IGNORE_CASE,
        )
        return pattern.find(html)?.groupValues?.getOrNull(1)
            ?: Regex(
                """value\s*=\s*"([^"]+)"[^>]*name\s*=\s*"school_csrf_name"""",
                RegexOption.IGNORE_CASE,
            ).find(html)?.groupValues?.getOrNull(1)
    }

    private fun String.toHttpUrlOrNullSafe(): HttpUrl = HttpUrl.Builder()
        .scheme(if (startsWith("http://")) "http" else "https")
        .host(removePrefix("https://").removePrefix("http://").substringBefore('/'))
        .build()
}

/**
 * Tiny in-memory [CookieJar] keyed by host. Only used during the login
 * `POST` round-trip — the persistent store of record is the Android
 * [CookieManager] (for the WebViews) and [SessionStore] (for cold starts).
 */
private class InMemoryCookieJar : CookieJar {
    private val byHost = ConcurrentHashMap<String, MutableList<Cookie>>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        byHost[url.host]?.toList().orEmpty()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val list = byHost.getOrPut(url.host) { mutableListOf() }
        cookies.forEach { incoming ->
            list.removeAll { it.name == incoming.name }
            list.add(incoming)
        }
    }

    fun allFor(url: HttpUrl): List<Cookie> = byHost[url.host]?.toList().orEmpty()
    fun clear() { byHost.clear() }
}
