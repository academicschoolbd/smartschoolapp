package com.schoolnav.app.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet4Bar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.schoolnav.app.auth.SessionState
import com.schoolnav.app.auth.rememberAuthViewModel
import com.schoolnav.app.ui.components.shimmer
import com.schoolnav.app.tenant.ActiveTenant
import com.schoolnav.app.web.WebAssets
import com.schoolnav.app.web.WebPage
import com.schoolnav.app.web.requiresAuth
import com.schoolnav.app.web.urlOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Renders a single Ramom Smart School page inside the app, wrapped in
 * native chrome:
 *
 * - Slim **LinearProgressIndicator** at the top while the page loads.
 * - **Skeleton placeholder** (animated shimmer-like cards) covers the area
 *   until first paint so the user never stares at a white screen.
 * - **Pull-to-refresh** wraps the WebView (Material 3 doesn't ship a
 *   composable that reliably proxies vertical drag to a child WebView, so
 *   we wrap `SwipeRefreshLayout` from androidx via [AndroidView]).
 * - CSS + JS from [WebAssets] is injected on every page load so the user
 *   never sees the website's own header/footer/sidebar.
 * - HTTP cache + DOM storage + `loadsImagesAutomatically = true` make
 *   repeated visits feel near-instant.
 * - Friendly **error state** with retry when the network fails.
 * - **Auth gate** for [WebPage.Authenticated] pages — non-signed-in users
 *   see a "Sign in to continue" prompt that routes to [Destination.Login].
 * - The system back button moves through WebView history before popping
 *   the navigation stack.
 *
 * @param page The mapped Ramom page (or [WebPage.Placeholder] for buttons
 *  that don't have a backend equivalent yet).
 * @param onRequestLogin Called when an auth-gated page is opened without
 *  a session.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebFeatureScreen(
    page: WebPage,
    onRequestLogin: () -> Unit,
) {
    val authVm = rememberAuthViewModel()
    val session by authVm.session.collectAsState()
    val scope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground
    val brandColor = MaterialTheme.colorScheme.primary

    when {
        page is WebPage.Placeholder -> ComingSoonState()
        page.requiresAuth && session !is SessionState.SignedIn -> SignInRequiredState(onRequestLogin)
        else -> {
            val url = page.urlOrNull ?: run { ComingSoonState(); return }
            var loading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }
            var showSkeleton by remember { mutableStateOf(true) }
            val webViewRef = remember { mutableStateOf<WebView?>(null) }

            val css = remember(brandColor, backgroundColor, onBackground) {
                WebAssets.mobileChromeCss(
                    brandColorHex = brandColor.toCssHex(),
                    contentBackgroundHex = backgroundColor.toCssHex(),
                    contentForegroundHex = onBackground.toCssHex(),
                )
            }
            val cssScript = remember(css) { WebAssets.cssInjectionScript(css) }

            // Hide the skeleton once a page has loaded; show again on every navigation.
            LaunchedEffect(loading) {
                if (!loading) {
                    // Slight delay so the injected CSS has a frame to apply before reveal.
                    delay(120)
                    showSkeleton = false
                } else {
                    showSkeleton = true
                }
            }

            BackHandler(enabled = webViewRef.value?.canGoBack() == true) {
                webViewRef.value?.goBack()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val swipeLayout = SwipeRefreshLayout(ctx).apply {
                            setColorSchemeColors(brandColor.toArgb())
                        }
                        val webView = createWebView(
                            context = ctx,
                            cssScript = cssScript,
                            jsScript = WebAssets.viewportAndCleanupJs,
                            onStarted = { loading = true; error = null },
                            onFinished = {
                                loading = false
                                swipeLayout.isRefreshing = false
                            },
                            onError = { msg ->
                                loading = false
                                swipeLayout.isRefreshing = false
                                error = msg
                            },
                        )
                        swipeLayout.addView(
                            webView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            ),
                        )
                        swipeLayout.setOnRefreshListener { webView.reload() }
                        webViewRef.value = webView
                        webView.loadUrl(url)
                        swipeLayout
                    },
                    update = { layout ->
                        val webView = layout.getChildAt(0) as? WebView ?: return@AndroidView
                        if (webView.url != url && !loading) {
                            webView.loadUrl(url)
                        }
                    },
                )

                AnimatedVisibility(
                    visible = loading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = brandColor,
                    )
                }

                AnimatedVisibility(
                    visible = showSkeleton && error == null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    SkeletonOverlay()
                }

                if (error != null) {
                    ErrorOverlay(message = error!!) {
                        error = null
                        scope.launch { webViewRef.value?.reload() }
                    }
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    webViewRef.value?.apply {
                        stopLoading()
                        loadUrl("about:blank")
                        removeAllViews()
                        destroy()
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun createWebView(
    context: android.content.Context,
    cssScript: String,
    jsScript: String,
    onStarted: () -> Unit,
    onFinished: () -> Unit,
    onError: (String) -> Unit,
): WebView {
    val webView = WebView(context)

    CookieManager.getInstance().apply {
        setAcceptCookie(true)
        setAcceptThirdPartyCookies(webView, true)
    }

    webView.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadsImagesAutomatically = true
        useWideViewPort = true
        loadWithOverviewMode = true
        builtInZoomControls = false
        displayZoomControls = false
        cacheMode = WebSettings.LOAD_DEFAULT
        mediaPlaybackRequiresUserGesture = false
        setSupportZoom(false)
        textZoom = 100
        // Spoof a modern Chrome on Android so Bootstrap MQs render the
        // mobile layout rather than the tablet fallback some Ramom themes use.
        userAgentString = userAgentString.replace("; wv", "")
    }

    webView.webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            onStarted()
            // Inject CSS as early as possible so the website chrome never paints.
            view?.evaluateJavascript(cssScript, null)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            // Re-inject after the page's own scripts may have removed the style tag.
            view?.evaluateJavascript(cssScript, null)
            view?.evaluateJavascript(jsScript, null)
            onFinished()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            val target = request?.url?.toString() ?: return false
            // Stay inside the tenant; external links (mailto:, tel:, other hosts)
            // bounce to the system handler.
            val tenantHost = ActiveTenant.host
            val tenantSchemeOk = target.startsWith("http://") || target.startsWith("https://")
            return if (tenantSchemeOk && target.contains(tenantHost)) {
                false
            } else {
                try {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, request.url)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    view?.context?.startActivity(intent)
                } catch (_: Throwable) { /* ignored */ }
                true
            }
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?,
        ) {
            if (request?.isForMainFrame == true) {
                val msg = error?.description?.toString() ?: "Could not load this page"
                onError(msg)
            }
        }
    }
    return webView
}

@Composable
private fun SignInRequiredState(onRequestLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This section is for students, parents, teachers and staff. " +
                    "Sign in with your school account to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRequestLogin, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text(text = "Sign in")
            }
        }
    }
}

@Composable
private fun ComingSoonState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Coming soon",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This section isn't published on the school portal yet. " +
                    "Once the school enables it on Smart School, this button will start " +
                    "opening real content automatically.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorOverlay(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.SignalCellularConnectedNoInternet4Bar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Couldn't load this page",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onRetry, modifier = Modifier.height(48.dp)) {
                Icon(Icons.Filled.Wifi, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Retry")
            }
        }
    }
}

@Composable
private fun SkeletonOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SkeletonLine(widthFraction = 0.85f, height = 22.dp)
        SkeletonLine(widthFraction = 0.55f, height = 14.dp)
        Spacer(modifier = Modifier.height(8.dp))
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .shimmer(),
            )
        }
    }
}

@Composable
private fun SkeletonLine(widthFraction: Float, height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .shimmer(),
    )
}

/** Formats a Compose [Color] as `"#RRGGBB"` for CSS injection. */
private fun Color.toCssHex(): String {
    val argb = this.toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    return "#%02X%02X%02X".format(r, g, b)
}
