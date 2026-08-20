package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ClockAppTheme(
    themeMode: String = "DARK", // "SYSTEM", "LIGHT", "DARK"
    colorTheme: AppColorTheme = AppColorTheme.GEOMETRIC,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemDark
    }

    val context = LocalContext.current
    val colorScheme: ColorScheme = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        when (colorTheme) {
            AppColorTheme.GEOMETRIC -> if (isDark) GeometricDark else GeometricLight
            AppColorTheme.BLUE -> if (isDark) BlueDark else BlueLight
            AppColorTheme.PURPLE -> if (isDark) PurpleDark else PurpleLight
            AppColorTheme.PINK -> if (isDark) PinkDark else PinkLight
            AppColorTheme.GREEN -> if (isDark) GreenDark else GreenLight
            AppColorTheme.ORANGE -> if (isDark) OrangeDark else OrangeLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
