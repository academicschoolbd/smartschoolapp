package com.schoolnav.app.tenant

import androidx.compose.ui.graphics.Color

/**
 * Single source of truth for which Ramom Smart School (smartschool.bd) tenant
 * this APK targets. The whole app is multi-tenant by design — to rebrand for a
 * different school, edit **this file only** (and optionally the launcher icon,
 * app name string and `google-services.json`). Nothing else in the codebase
 * hard-codes a school name, slug or URL.
 *
 * See `docs/TENANT_SETUP.md` for a step-by-step rebrand guide.
 */
data class TenantConfig(
    /** Pretty school name shown in the drawer header, login screen and app bar. */
    val displayName: String,

    /** Short subtitle below the school name (e.g. "Public school dashboard"). */
    val tagline: String,

    /**
     * Base URL of the tenant deployment **without** a trailing slash.
     * Examples: `https://ngps.smartschool.bd`, `https://demo.smartschool.bd`.
     */
    val baseUrl: String,

    /**
     * The CodeIgniter "frontend slug" for the public website pages. For
     * Ramom Smart School this is the first URL segment under the tenant
     * host: `https://ngps.smartschool.bd/ngps/news` → slug = `"ngps"`.
     *
     * Confirm the value by visiting the tenant homepage and inspecting the
     * `News` / `Teachers` links in the public nav.
     */
    val frontendSlug: String,

    /** Primary brand color used by the in-app chrome (top bar, FAB, buttons). */
    val brandColor: Color,

    /**
     * Optional URL of the school logo (PNG/SVG). Rendered in the drawer
     * header and on the native login screen. If `null`, the bundled
     * Material `School` icon is used as a fallback.
     */
    val logoUrl: String? = null,

    /**
     * Whether the tenant exposes the public frontend pages at
     * `https://host/<slug>/<page>`. All current Ramom Smart School tenants
     * do; leaving this `true` is almost always correct.
     */
    val hasPublicFrontend: Boolean = true,
) {
    /** Returns `https://host/<slug>/<page>` for the public frontend. */
    fun publicUrl(page: String): String = "$baseUrl/$frontendSlug/$page"

    /** Returns `https://host/<path>` for an authenticated dashboard route. */
    fun authedUrl(path: String): String = "$baseUrl/$path"

    /** Login POST endpoint — same path on every Ramom Smart School tenant. */
    val loginUrl: String get() = "$baseUrl/authentication"

    /** Logout endpoint — common Ramom convention. */
    val logoutUrl: String get() = "$baseUrl/authentication/logout"

    /** Host portion of [baseUrl] (without scheme), useful for cookie scoping. */
    val host: String
        get() = baseUrl
            .removePrefix("https://")
            .removePrefix("http://")
            .substringBefore('/')
}

/**
 * The active tenant the APK is built for. This is the **only** thing to change
 * when forking the app for a different Smart School tenant.
 */
val ActiveTenant: TenantConfig = TenantConfig(
    displayName = "NGPS — Faakal Police Lines School & College",
    tagline = "Notices, results, classes & more",
    baseUrl = "https://ngps.smartschool.bd",
    frontendSlug = "ngps",
    brandColor = Color(0xFF1A73E8),
    logoUrl = null,
)
