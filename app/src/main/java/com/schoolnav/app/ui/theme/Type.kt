package com.schoolnav.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.schoolnav.app.R

/**
 * Google Fonts downloadable-fonts provider. The certificate hashes live in
 * res/values/font_certs.xml. At runtime the Google Fonts provider on Play Services
 * downloads and caches the requested font (here: Tiro Bangla). While the download
 * is in flight Compose falls back to FontFamily.SansSerif.
 */
private val GoogleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val TiroBangla = GoogleFont("Tiro Bangla")

/** Tiro Bangla supports Regular (400) and Italic — we use Regular for both
 *  weight stops to keep the typography crisp. */
val TiroBanglaFontFamily = FontFamily(
    Font(googleFont = TiroBangla, fontProvider = GoogleFontsProvider, weight = FontWeight.Normal),
    Font(googleFont = TiroBangla, fontProvider = GoogleFontsProvider, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(googleFont = TiroBangla, fontProvider = GoogleFontsProvider, weight = FontWeight.Medium),
    Font(googleFont = TiroBangla, fontProvider = GoogleFontsProvider, weight = FontWeight.SemiBold),
    Font(googleFont = TiroBangla, fontProvider = GoogleFontsProvider, weight = FontWeight.Bold),
)

val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = TiroBanglaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)
