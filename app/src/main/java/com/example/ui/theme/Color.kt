package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Color definitions for modern clean Material 3 palette
enum class AppColorTheme(val displayName: String, val primaryColor: Color) {
    GEOMETRIC("Geometric", Color(0xFFD0BCFF)),
    BLUE("Blue", Color(0xFF38BDF8)),
    PURPLE("Purple", Color(0xFFA855F7)),
    PINK("Pink", Color(0xFFF472B6)),
    GREEN("Green", Color(0xFF34D399)),
    ORANGE("Orange", Color(0xFFFB923C))
}

// Geometric Balance Schemes
val GeometricDark = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2B2930),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    error = Color(0xFFF2B8B5)
)

val GeometricLight = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    background = Color(0xFFFEF7FF),
    onBackground = Color(0xFF1D1B20),
    surface = Color(0xFFFEF7FF),
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    error = Color(0xFFB3261E)
)

// Blue Schemes
val BlueDark = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF00354E),
    primaryContainer = Color(0xFF004D6F),
    onPrimaryContainer = Color(0xFFCBE6FF),
    secondary = Color(0xFF7DD3FC),
    onSecondary = Color(0xFF003548),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF818CF8),
    background = Color(0xFF090D16),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    error = Color(0xFFF87171)
)

val BlueLight = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = Color(0xFF334155),
    tertiary = Color(0xFF6366F1),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626)
)

// Purple Schemes
val PurpleDark = darkColorScheme(
    primary = Color(0xFFA855F7),
    onPrimary = Color(0xFF3B0764),
    primaryContainer = Color(0xFF581C87),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = Color(0xFFC084FC),
    onSecondary = Color(0xFF3B0764),
    secondaryContainer = Color(0xFF1F172B),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = Color(0xFFF472B6),
    background = Color(0xFF0E0A14),
    onBackground = Color(0xFFF5F3FF),
    surface = Color(0xFF181124),
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = Color(0xFF261D38),
    onSurfaceVariant = Color(0xFFA79BBB),
    outline = Color(0xFF3D3156),
    error = Color(0xFFF87171)
)

val PurpleLight = lightColorScheme(
    primary = Color(0xFF9333EA),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF7E22CE),
    secondary = Color(0xFFA855F7),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFAF5FF),
    onSecondaryContainer = Color(0xFF581C87),
    tertiary = Color(0xFFEC4899),
    background = Color(0xFFFAF5FF),
    onBackground = Color(0xFF1E1035),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1035),
    surfaceVariant = Color(0xFFF3E8FF),
    onSurfaceVariant = Color(0xFF7E22CE),
    outline = Color(0xFFD8B4FE),
    error = Color(0xFFDC2626)
)

// Pink Schemes
val PinkDark = darkColorScheme(
    primary = Color(0xFFF472B6),
    onPrimary = Color(0xFF500724),
    primaryContainer = Color(0xFF831843),
    onPrimaryContainer = Color(0xFFFCE7F3),
    secondary = Color(0xFFFB7185),
    onSecondary = Color(0xFF4C0519),
    secondaryContainer = Color(0xFF24121A),
    onSecondaryContainer = Color(0xFFFFDDE7),
    tertiary = Color(0xFFC084FC),
    background = Color(0xFF120B0F),
    onBackground = Color(0xFFFFF1F2),
    surface = Color(0xFF1E1219),
    onSurface = Color(0xFFFFF1F2),
    surfaceVariant = Color(0xFF2F1B27),
    onSurfaceVariant = Color(0xFFBA9EAC),
    outline = Color(0xFF4D3040),
    error = Color(0xFFF87171)
)

val PinkLight = lightColorScheme(
    primary = Color(0xFFDB2777),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFCE7F3),
    onPrimaryContainer = Color(0xFF9D174D),
    secondary = Color(0xFFF43F5E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFF1F2),
    onSecondaryContainer = Color(0xFF881337),
    tertiary = Color(0xFF9333EA),
    background = Color(0xFFFFF1F2),
    onBackground = Color(0xFF2E0818),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2E0818),
    surfaceVariant = Color(0xFFFDF2F8),
    onSurfaceVariant = Color(0xFF9D174D),
    outline = Color(0xFFFBCFE8),
    error = Color(0xFFDC2626)
)

// Green Schemes
val GreenDark = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = Color(0xFF022C1A),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF4ADE80),
    onSecondary = Color(0xFF052E16),
    secondaryContainer = Color(0xFF112218),
    onSecondaryContainer = Color(0xFFD0FBE1),
    tertiary = Color(0xFF2DD4BF),
    background = Color(0xFF07130D),
    onBackground = Color(0xFFF0FDF4),
    surface = Color(0xFF0E2217),
    onSurface = Color(0xFFF0FDF4),
    surfaceVariant = Color(0xFF183324),
    onSurfaceVariant = Color(0xFF96B8A4),
    outline = Color(0xFF264C36),
    error = Color(0xFFF87171)
)

val GreenLight = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = Color(0xFF10B981),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF0FDF4),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Color(0xFF0D9488),
    background = Color(0xFFF0FDF4),
    onBackground = Color(0xFF022C1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF022C1A),
    surfaceVariant = Color(0xFFECFDF5),
    onSurfaceVariant = Color(0xFF047857),
    outline = Color(0xFFA7F3D0),
    error = Color(0xFFDC2626)
)

// Orange Schemes
val OrangeDark = darkColorScheme(
    primary = Color(0xFFFB923C),
    onPrimary = Color(0xFF431407),
    primaryContainer = Color(0xFF7C2D12),
    onPrimaryContainer = Color(0xFFFFEDD5),
    secondary = Color(0xFFF59E0B),
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF251811),
    onSecondaryContainer = Color(0xFFFED7AA),
    tertiary = Color(0xFFF43F5E),
    background = Color(0xFF140D08),
    onBackground = Color(0xFFFFF7ED),
    surface = Color(0xFF22160F),
    onSurface = Color(0xFFFFF7ED),
    surfaceVariant = Color(0xFF352319),
    onSurfaceVariant = Color(0xFFC0A696),
    outline = Color(0xFF4E372A),
    error = Color(0xFFF87171)
)

val OrangeLight = lightColorScheme(
    primary = Color(0xFFEA580C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF9A3412),
    secondary = Color(0xFFF97316),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFF7ED),
    onSecondaryContainer = Color(0xFF7C2D12),
    tertiary = Color(0xFFE11D48),
    background = Color(0xFFFFF7ED),
    onBackground = Color(0xFF431407),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF431407),
    surfaceVariant = Color(0xFFFFF1EB),
    onSurfaceVariant = Color(0xFFC2410C),
    outline = Color(0xFFFED7AA),
    error = Color(0xFFDC2626)
)
