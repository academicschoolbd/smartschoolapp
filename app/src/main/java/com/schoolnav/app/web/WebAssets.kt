package com.schoolnav.app.web

/**
 * CSS + JS snippets injected into every WebView page in [com.schoolnav.app.ui.screens.WebFeatureScreen].
 *
 * The goal is to make the Ramom Smart School pages feel **native**: hide the
 * website's own header/footer/sidebar so the user only sees the content
 * (article body, table, gallery, …), force a mobile-friendly viewport,
 * scale typography to the device, and apply the app's brand colors.
 *
 * Selectors are intentionally broad so this also works for any other Ramom
 * Smart School tenant whose theme is slightly different. We use
 * `display: none !important` rather than `remove()` so the page's layout
 * scripts don't crash trying to find missing nodes.
 */
internal object WebAssets {

    /**
     * CSS injected as a `<style>` tag immediately after `<head>` so it
     * applies before first paint and the user never sees the website chrome
     * flash on screen.
     *
     * @param brandColorHex Hex string of the active tenant's brand color,
     *  e.g. `"#1A73E8"`. Used to recolor primary buttons, links and headings.
     * @param contentBackgroundHex Background color of the WebView container
     *  (matches the host Compose surface so there is no white flash on dark
     *  mode).
     * @param contentForegroundHex Default text color (matches the host
     *  Compose `onBackground`).
     */
    fun mobileChromeCss(
        brandColorHex: String,
        contentBackgroundHex: String,
        contentForegroundHex: String,
    ): String = """
        /* Hide every chrome element on the public website and the admin theme. */
        header, footer, nav,
        .header, #header, .top-header, .main-header, .site-header, .header-wrap,
        .navbar, .navbar-wrapper, .breadcrumb, .page-header,
        .footer, #footer, .copyright, .site-footer, .footer-wrap,
        .sidebar, #sidebar, .left-sidebar, .nav-side-menu, aside,
        .app-sidebar, #app-sidebar, .app-topbar, .app-footer,
        .topbar, .top-bar, .top-content, .heading-page,
        #scroll-to-top, .scroll-to-top, .back-to-top,
        .floating-button, .floating-btn,
        .preloader, #preloader, .preloader-wrap,
        .cookies-notification, .cookie-consent,
        .school-info-box .logo, .home-banner, .school-name-wrap,
        .top-menu, .menu-bar, .topbar-wrap {
            display: none !important;
            height: 0 !important;
            visibility: hidden !important;
        }

        html, body {
            background: $contentBackgroundHex !important;
            color: $contentForegroundHex !important;
            font-size: 16px !important;
            margin: 0 !important;
            padding: 0 !important;
            overflow-x: hidden !important;
            -webkit-text-size-adjust: 100% !important;
        }

        body, .container, .container-fluid, .row, .col, [class^="col-"], [class*=" col-"],
        .main, .main-content, .page-content, .content-wrapper, main,
        .app-main, .app-content, .content, .wrapper, .page-wrap {
            margin: 0 !important;
            padding: 12px !important;
            max-width: 100% !important;
            width: 100% !important;
            left: 0 !important;
            float: none !important;
        }

        img, video, iframe {
            max-width: 100% !important;
            height: auto !important;
            border-radius: 8px !important;
        }

        /* Make Bootstrap-3 tables (very common in Ramom views) responsive. */
        table {
            width: 100% !important;
            max-width: 100% !important;
            border-collapse: collapse !important;
            font-size: 14px !important;
            display: block !important;
            overflow-x: auto !important;
            white-space: nowrap !important;
        }
        table th, table td {
            padding: 8px !important;
            border-color: rgba(127,127,127,0.2) !important;
        }
        table th { background: rgba(127,127,127,0.08) !important; }

        a, a:link, a:visited {
            color: $brandColorHex !important;
            text-decoration: none !important;
        }

        h1, h2, h3, h4, h5, h6 {
            color: $contentForegroundHex !important;
            line-height: 1.3 !important;
        }
        h1 { font-size: 22px !important; }
        h2 { font-size: 20px !important; }
        h3 { font-size: 18px !important; }

        .btn, .btn-primary, .button, button[type="submit"] {
            background: $brandColorHex !important;
            border-color: $brandColorHex !important;
            color: #FFFFFF !important;
            border-radius: 10px !important;
            padding: 10px 16px !important;
        }

        .card, .panel, .box, .widget {
            border-radius: 12px !important;
            box-shadow: 0 1px 3px rgba(0,0,0,0.08) !important;
            border: 1px solid rgba(127,127,127,0.15) !important;
            margin-bottom: 12px !important;
            padding: 12px !important;
        }

        /* Public site article body */
        .article, .article-body, .single-news, .news-details, .post-content {
            background: transparent !important;
            padding: 0 !important;
        }

        /* Photo galleries — make thumbnails fluid */
        .gallery, .photo-gallery, .gallery-grid {
            display: grid !important;
            grid-template-columns: repeat(2, 1fr) !important;
            gap: 8px !important;
        }
        .gallery img, .photo-gallery img, .gallery-grid img {
            width: 100% !important;
            aspect-ratio: 1 / 1 !important;
            object-fit: cover !important;
        }
    """.trimIndent()

    /**
     * JS that runs at `DOMContentLoaded`. Forces a mobile viewport (a lot
     * of the admin pages don't declare one) and removes any remaining
     * fixed-position banner the CSS couldn't catch.
     */
    val viewportAndCleanupJs: String = """
        (function() {
            var meta = document.querySelector('meta[name="viewport"]');
            if (!meta) {
                meta = document.createElement('meta');
                meta.name = 'viewport';
                document.head.appendChild(meta);
            }
            meta.setAttribute(
                'content',
                'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no, viewport-fit=cover'
            );

            // Remove fixed-position floaters that escape the CSS hide rules.
            document.querySelectorAll('*').forEach(function(el) {
                var pos = getComputedStyle(el).position;
                if ((pos === 'fixed' || pos === 'sticky') &&
                    !el.closest('.modal, .swal2-container')) {
                    el.style.position = 'static';
                }
            });
        })();
    """.trimIndent()

    /**
     * Injection script — wraps [mobileChromeCss] in a `<style>` element and
     * appends it to `<head>`. Called inside `WebViewClient.onPageStarted`
     * so the rules apply before first paint.
     */
    fun cssInjectionScript(css: String): String {
        val literal = jsStringLiteral(css)
        return """
            (function() {
                if (document.getElementById('__schoolnav_chrome_css')) return;
                var s = document.createElement('style');
                s.id = '__schoolnav_chrome_css';
                s.textContent = $literal;
                (document.head || document.documentElement).appendChild(s);
            })();
        """.trimIndent()
    }

    /** Encodes [s] as a safe JS template literal. */
    private fun jsStringLiteral(s: String): String {
        val escaped = s
            .replace("\\", "\\\\")
            .replace("`", "\\`")
            .replace("$", "\\$")
        return "`$escaped`"
    }
}
