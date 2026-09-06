package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkPrimaryText,
    onSecondary = DarkPrimaryText,
    onBackground = DarkPrimaryText,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkBorder // Using surfaceVariant for borders
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightSurface,
    onSecondary = LightPrimaryText,
    onBackground = LightPrimaryText,
    onSurface = LightPrimaryText,
    surfaceVariant = LightBorder // Using surfaceVariant for borders
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color so our financial brand identity (Green/Red semantics) remains consistent.
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
