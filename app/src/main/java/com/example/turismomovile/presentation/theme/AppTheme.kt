package com.example.turismomovile.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Colores personalizados
object AppColors {
    // Light Theme Colors
    val Primary = Color(0xFF2196F3)
    val PrimaryDark = Color(0xFF1976D2)
    val Secondary = Color(0xFF03DAC6)
    val Background = Color(0xFFFAFAFA)
    val Surface = Color(0xFFFFFFFF)
    val Error = Color(0xFFB00020)

    // Notification Colors Light
    val SuccessLight = Color(0xFFDCF7DC)  // Verde claro
    val SuccessTextLight = Color(0xFF1B5E20)  // Verde oscuro
    val ErrorLight = Color(0xFFFFEBEE)  // Rojo claro
    val ErrorTextLight = Color(0xFFB00020)  // Rojo oscuro
    val WarningLight = Color(0xFFFFF3E0)  // Naranja claro
    val WarningTextLight = Color(0xFFE65100)  // Naranja oscuro
    val InfoLight = Color(0xFFE3F2FD)  // Azul claro
    val InfoTextLight = Color(0xFF0D47A1)  // Azul oscuro

    // Dark Theme Colors
    val PrimaryDark_Dark = Color(0xFF64B5F6)
    val SecondaryDark = Color(0xFF03DAC6)
    val BackgroundDark = Color(0xFF121212)
    val SurfaceDark = Color(0xFF242424)
    val ErrorDark = Color(0xFF7F0000)  // Rojo oscuro

    // Notification Colors Dark
    val SuccessDark = Color(0xFF1B5E20)  // Verde oscuro
    val SuccessTextDark = Color(0xFFDCF7DC)  // Verde claro
    val ErrorTextDark = Color(0xFFFFEBEE)  // Rojo claro
    val WarningDark = Color(0xFF7F4100)  // Naranja oscuro
    val WarningTextDark = Color(0xFFFFF3E0)  // Naranja claro
    val InfoDark = Color(0xFF0D47A1)  // Azul oscuro
    val InfoTextDark = Color(0xFFE3F2FD)  // Azul claro
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

// Definir los esquemas de color para tema claro y oscuro
private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.Secondary,
    background = AppColors.Background,
    surface = AppColors.Surface,
    error = AppColors.Error
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryDark_Dark,
    secondary = AppColors.SecondaryDark,
    background = AppColors.BackgroundDark,
    surface = AppColors.SurfaceDark,
    error = AppColors.ErrorDark
)

// Local composition para el tema
val LocalAppDimens = staticCompositionLocalOf { AppDimensions }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalAppDimens provides AppDimensions
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}