package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF4CAF50),
    onPrimaryContainer = Color(0xFFE8F5E9),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF004D40),
    secondaryContainer = Color(0xFF80CBC4),
    onSecondaryContainer = Color(0xFF003330),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF5D4037),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF757575),
  )

private val LightColorScheme =
  lightColorScheme(
    // Primary - Soft Green (màu xanh lá nhạt)
    primary = Color(0xFF00E300),
    onPrimary = Color(0xDBDFFFE8),
    primaryContainer = Color(0xDBDFFFE8),
    onPrimaryContainer = Color(0xFF418C3B),

    // Secondary - Soft Cyan/Teal (màu xanh ngọc nhạt)
    secondary = Color(0xFFD7FFF7),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC5FFF5),
    onSecondaryContainer = Color(0xFFBAFFEE),

    // Tertiary - Soft Yellow (màu vàng nhạt)
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF5D4037),
    tertiaryContainer = Color(0xFFFFECB3),
    onTertiaryContainer = Color(0xFF3E2723),

    // Background & Surface - Very light with subtle warm tint
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFEFEFE),
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),

    // Surface Variant - Subtle cool tint
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF424242),

    // Error - Soft Red
    error = Color(0xFFFF0000),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFD0D1),
    onErrorContainer = Color(0xFFAF0000),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic coloring to always use our custom pastel color scheme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      // Always use our custom color scheme, ignoring system dynamic colors
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
