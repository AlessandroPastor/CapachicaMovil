package com.example.turismomovile.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Colores personalizados mejorados para turismo
object AppColors {
    // Light Theme Colors - Paleta turística moderna
    val Primary = Color(0xFF1E88E5)        // Azul océano vibrante
    val PrimaryVariant = Color(0xFF1565C0) // Azul más profundo
    val Secondary = Color(0xFF26A69A)      // Verde turquesa tropical
    val SecondaryVariant = Color(0xFF00695C) // Verde esmeralda
    val Tertiary = Color(0xFFFF7043)       // Naranja atardecer
    val Background = Color(0xFFF8FFFE)     // Blanco nieve con tinte azul
    val Surface = Color(0xFFFFFFFF)        // Blanco puro
    val SurfaceVariant = Color(0xFFF5F5F5) // Gris muy claro
    val Error = Color(0xFFE53935)          // Rojo coral
    val OnPrimary = Color(0xFFFFFFFF)      // Blanco
    val OnSecondary = Color(0xFFFFFFFF)    // Blanco
    val OnBackground = Color(0xFF1A1A1A)   // Negro suave
    val OnSurface = Color(0xFF212121)      // Gris muy oscuro
    val OnError = Color(0xFFFFFFFF)        // Blanco

    // Colores accent para elementos especiales
    val AccentGold = Color(0xFFFFB300)     // Dorado para premiums
    val AccentCoral = Color(0xFFFF6B6B)    // Coral para destacados
    val AccentLavender = Color(0xFF9C27B0) // Lavanda para categorías

    // Notification Colors Light - Más suaves y modernos
    val SuccessLight = Color(0xFFE8F5E8)   // Verde menta claro
    val SuccessTextLight = Color(0xFF2E7D32) // Verde bosque
    val ErrorLight = Color(0xFFFFEBEE)     // Rosa claro
    val ErrorTextLight = Color(0xFFD32F2F) // Rojo moderno
    val WarningLight = Color(0xFFFFF8E1)   // Amarillo crema
    val WarningTextLight = Color(0xFFF57C00) // Naranja dorado
    val InfoLight = Color(0xFFE1F5FE)      // Azul cielo claro
    val InfoTextLight = Color(0xFF0277BD)  // Azul información

    // Dark Theme Colors - Más elegantes y con mejor contraste
    val PrimaryDark = Color(0xFF42A5F5)    // Azul brillante
    val PrimaryVariantDark = Color(0xFF1976D2) // Azul profundo
    val SecondaryDark = Color(0xFF4DB6AC)  // Turquesa brillante
    val SecondaryVariantDark = Color(0xFF00897B) // Verde mar
    val TertiaryDark = Color(0xFFFFAB40)   // Naranja dorado
    val BackgroundDark = Color(0xFF0A0E13) // Azul noche profundo
    val SurfaceDark = Color(0xFF1A1D23)    // Gris azulado oscuro
    val SurfaceVariantDark = Color(0xFF2A2D33) // Gris medio
    val OnPrimaryDark = Color(0xFF000000)  // Negro
    val OnSecondaryDark = Color(0xFF000000) // Negro
    val OnBackgroundDark = Color(0xFFE0E0E0) // Gris claro
    val OnSurfaceDark = Color(0xFFE0E0E0)  // Gris claro
    val OnErrorDark = Color(0xFF000000)    // Negro

    // Colores accent dark
    val AccentGoldDark = Color(0xFFFFD54F) // Dorado claro
    val AccentCoralDark = Color(0xFFFF8A80) // Coral claro
    val AccentLavenderDark = Color(0xFFBA68C8) // Lavanda claro

    // Notification Colors Dark - Mejor visibilidad
    val SuccessDark = Color(0xFF2E7D32)    // Verde oscuro
    val SuccessTextDark = Color(0xFFA5D6A7) // Verde claro
    val ErrorDark = Color(0xFFD32F2F)      // Rojo oscuro
    val ErrorTextDark = Color(0xFFFFCDD2)  // Rosa claro
    val WarningDark = Color(0xFFFFA000)    // Naranja oscuro
    val WarningTextDark = Color(0xFFFFE082) // Amarillo claro
    val InfoDark = Color(0xFF1976D2)       // Azul oscuro
    val InfoTextDark = Color(0xFF81D4FA)   // Azul claro

    // Gradientes para elementos especiales
    val GradientPrimary = listOf(
        Color(0xFF1E88E5),
        Color(0xFF26A69A)
    )

    val GradientSunset = listOf(
        Color(0xFFFF7043),
        Color(0xFFFFB300)
    )

    val GradientNight = listOf(
        Color(0xFF1A1D23),
        Color(0xFF0A0E13)
    )

    // Colores semánticos para turismo
    val BeachBlue = Color(0xFF03A9F4)      // Azul playa
    val MountainGreen = Color(0xFF4CAF50)  // Verde montaña
    val DesertSand = Color(0xFFFFE0B2)     // Arena del desierto
    val ForestGreen = Color(0xFF388E3C)    // Verde bosque
    val SkyBlue = Color(0xFF87CEEB)        // Azul cielo
    val SunsetOrange = Color(0xFFFF8A65)   // Naranja atardecer
}

// Dimensiones personalizadas ampliadas y organizadas
object AppDimensions {
    // Espaciados básicos
    val spacing_1 = 1
    val spacing_2 = 2
    val spacing_4 = 4
    val spacing_6 = 6
    val spacing_8 = 8
    val spacing_12 = 12
    val spacing_16 = 16
    val spacing_20 = 20
    val spacing_24 = 24
    val spacing_28 = 28
    val spacing_32 = 32
    val spacing_40 = 40
    val spacing_48 = 48
    val spacing_56 = 56
    val spacing_64 = 64
    val spacing_80 = 80
    val spacing_96 = 96
    val spacing_120 = 120
    val spacing_150 = 150

    // Componentes específicos
    val buttonHeight = 48
    val buttonHeightLarge = 56
    val inputHeight = 56
    val cardHeight = 120
    val headerHeight = 64
    val bottomNavHeight = 80
    val toolbarHeight = 56
    val fabSize = 56
    val fabSizeSmall = 40

    // Iconos y elementos pequeños
    val iconSize = 24
    val iconSizeSmall = 16
    val iconSizeMedium = 20
    val iconSizeLarge = 32
    val iconSizeXLarge = 48

    // Elevaciones y sombras
    val cardElevation = 4
    val cardElevationHigh = 8
    val cardElevationLow = 2
    val shadowElevation = 6
    val shadowElevationHigh = 12

    // Bordes y esquinas
    val cornerRadius = 8
    val cornerRadiusSmall = 4
    val cornerRadiusMedium = 12
    val cornerRadiusLarge = 16
    val cornerRadiusXLarge = 20
    val cornerRadiusXXLarge = 24
    val cornerRadiusCircle = 50

    // Líneas y divisores
    val borderWidth = 1
    val borderWidthThick = 2
    val dividerHeight = 1

    // Contenedores y layouts
    val screenPadding = 16
    val screenPaddingLarge = 24
    val cardPadding = 16
    val cardPaddingLarge = 24
    val listItemPadding = 16
    val dialogPadding = 24

    // Elementos específicos para login
    val logoSize = 120
    val logoSizeLarge = 150
    val loginCardMaxWidth = 400
    val loginFormSpacing = 16
    val loginButtonSpacing = 20

    // Animaciones y efectos
    val animationDurationShort = 200
    val animationDurationMedium = 300
    val animationDurationLong = 600
    val animationDelayShort = 100
    val animationDelayMedium = 200
    val animationDelayLong = 400

    // Partículas y efectos visuales
    val particleSize = 3
    val particleSizeMedium = 5
    val particleSizeLarge = 8
    val shimmerWidth = 1000
    val floatDistance = 8
    val breathingScale = 0.03f
    val glowIntensity = 4

    // Responsive breakpoints
    val mobileBreakpoint = 600
    val tabletBreakpoint = 840
    val desktopBreakpoint = 1200
}

// Definir los esquemas de color mejorados
private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryVariant,
    onPrimaryContainer = AppColors.OnPrimary,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryVariant,
    onSecondaryContainer = AppColors.OnSecondary,
    tertiary = AppColors.Tertiary,
    onTertiary = Color.White,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurface,
    error = AppColors.Error,
    onError = AppColors.OnError,
    errorContainer = AppColors.ErrorLight,
    onErrorContainer = AppColors.ErrorTextLight,
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0)
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryDark,
    onPrimary = AppColors.OnPrimaryDark,
    primaryContainer = AppColors.PrimaryVariantDark,
    onPrimaryContainer = AppColors.OnPrimaryDark,
    secondary = AppColors.SecondaryDark,
    onSecondary = AppColors.OnSecondaryDark,
    secondaryContainer = AppColors.SecondaryVariantDark,
    onSecondaryContainer = AppColors.OnSecondaryDark,
    tertiary = AppColors.TertiaryDark,
    onTertiary = Color.Black,
    background = AppColors.BackgroundDark,
    onBackground = AppColors.OnBackgroundDark,
    surface = AppColors.SurfaceDark,
    onSurface = AppColors.OnSurfaceDark,
    surfaceVariant = AppColors.SurfaceVariantDark,
    onSurfaceVariant = AppColors.OnSurfaceDark,
    error = AppColors.ErrorDark,
    onError = AppColors.OnErrorDark,
    errorContainer = AppColors.ErrorDark,
    onErrorContainer = AppColors.ErrorTextDark,
    outline = Color(0xFF616161),
    outlineVariant = Color(0xFF424242)
)

// Local composition para el tema
val LocalAppDimens = staticCompositionLocalOf { AppDimensions }
val LocalAppColors = staticCompositionLocalOf { AppColors }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalAppDimens provides AppDimensions,
        LocalAppColors provides AppColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}