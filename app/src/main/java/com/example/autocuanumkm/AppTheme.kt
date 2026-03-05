package com.example.autocuanumkm

// ═══════════════════════════════════════════════════════════════════════════
//  AppTheme.kt  — Warna, Tipografi, dan Material3 Theme untuk AUTO CUAN UMKM
//  Prinsip desain: kontras tinggi, font besar, warna ramah lansia.
// ═══════════════════════════════════════════════════════════════════════════

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ─── Palet Warna ───────────────────────────────────────────────────────────

// Hijau Tua — melambangkan kemakmuran & kepercayaan (warna utama UMKM)
val Green800 = Color(0xFF1B5E20)
val Green700 = Color(0xFF2E7D32)
val Green50  = Color(0xFFE8F5E9)

// Oranye Hangat — aksen tombol sekunder & ikon
val Orange700 = Color(0xFFE65100)
val Orange100 = Color(0xFFFFE0B2)

// Netral
val NearBlack  = Color(0xFF1A1A2E)
val DarkGray   = Color(0xFF424242)
val MediumGray = Color(0xFF757575)
val LightGray  = Color(0xFFF5F5F5)
val OffWhite   = Color(0xFFFAFAFA)
val White      = Color(0xFFFFFFFF)

// Error
val Red800 = Color(0xFFC62828)
val Red100 = Color(0xFFFFCDD2)

// ─── Tipografi Ramah Lansia ────────────────────────────────────────────────
// Body minimal 18sp | Label minimal 16sp | Heading 22sp ke atas

private val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize   = 26.sp,
        lineHeight = 34.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize   = 18.sp,
        lineHeight = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 18.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 22.sp
    )
)

// ─── Color Scheme (hanya Light Mode) ──────────────────────────────────────

private val AppColorScheme = lightColorScheme(
    primary              = Green700,
    onPrimary            = White,
    primaryContainer     = Green50,
    onPrimaryContainer   = Green800,
    secondary            = Orange700,
    onSecondary          = White,
    secondaryContainer   = Orange100,
    onSecondaryContainer = Orange700,
    background           = OffWhite,
    onBackground         = NearBlack,
    surface              = White,
    onSurface            = NearBlack,
    surfaceVariant       = Green50,
    onSurfaceVariant     = DarkGray,
    error                = Red800,
    onError              = White,
    errorContainer       = Red100,
    onErrorContainer     = Red800,
    outline              = MediumGray,
    outlineVariant       = LightGray
)

// ─── AutoCuanTheme Entry Point ─────────────────────────────────────────────

@Composable
fun AutoCuanTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
