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
    // Light Theme Colors - Paleta turística moderna inspirada en paraísos tropicales
    val Primary = Color(0xFF00BCD4)        // Turquesa cristalino como agua del Caribe
    val PrimaryVariant = Color(0xFF0097A7) // Azul profundo del océano
    val Secondary = Color(0xFFFF6B35)      // Coral vibrante del atardecer tropical
    val SecondaryVariant = Color(0xFFE65100) // Naranja fuego del ocaso
    val Tertiary = Color(0xFF7CB342)       // Verde jade de palmeras
    val Background = Color(0xFFF0FDFF)     // Blanco cristal con toque de brisa marina
    val Surface = Color(0xFFFFFFFF)        // Blanco arena fina
    val SurfaceVariant = Color(0xFFF5FAFE) // Gris perla suave
    val Error = Color(0xFFE57373)          // Rojo suave como flor tropical
    val OnPrimary = Color(0xFFFFFFFF)      // Blanco
    val OnSecondary = Color(0xFFFFFFFF)    // Blanco
    val OnBackground = Color(0xFF1A1A1A)   // Negro carbón suave
    val OnSurface = Color(0xFF2E3440)      // Gris pizarra elegante
    val OnError = Color(0xFFFFFFFF)        // Blanco

    // Colores accent para elementos especiales - Inspirados en joyas tropicales
    val AccentGold = Color(0xFFFFC107)     // Dorado sol del amanecer
    val AccentCoral = Color(0xFFFF7043)    // Coral del Mar Rojo
    val AccentLavender = Color(0xFF9575CD) // Lavanda de campos de Provenza

    // Notification Colors Light - Suaves como brisa marina
    val SuccessLight = Color(0xFFE8F8F5)   // Verde menta glacial
    val SuccessTextLight = Color(0xFF00695C) // Verde esmeralda profundo
    val ErrorLight = Color(0xFFFFF3E0)     // Melocotón suave
    val ErrorTextLight = Color(0xFFD84315) // Naranja terracota
    val WarningLight = Color(0xFFFFF8E1)   // Crema de vainilla
    val WarningTextLight = Color(0xFFFF8F00) // Ámbar dorado
    val InfoLight = Color(0xFFE0F2F1)      // Aguamarina clara
    val InfoTextLight = Color(0xFF00838F)  // Azul petróleo

    // Dark Theme Colors - Noche tropical elegante
    val PrimaryDark = Color(0xFF26C6DA)    // Turquesa luminoso nocturno
    val PrimaryVariantDark = Color(0xFF00ACC1) // Azul cobalto profundo
    val SecondaryDark = Color(0xFFFF8A65)  // Coral luminoso
    val SecondaryVariantDark = Color(0xFFFF5722) // Naranja volcánico
    val TertiaryDark = Color(0xFF9CCC65)   // Verde lima nocturno
    val BackgroundDark = Color(0xFF0D1421) // Azul noche profunda del océano
    val SurfaceDark = Color(0xFF1E2A38)    // Gris pizarra nocturna
    val SurfaceVariantDark = Color(0xFF2A3441) // Gris carbón elegante
    val OnPrimaryDark = Color(0xFF000000)  // Negro
    val OnSecondaryDark = Color(0xFF000000) // Negro
    val OnBackgroundDark = Color(0xFFECEFF1) // Gris perla luminoso
    val OnSurfaceDark = Color(0xFFE0E3E7)  // Gris claro como niebla
    val OnErrorDark = Color(0xFF000000)    // Negro

    // Colores accent dark - Joyas nocturnas
    val AccentGoldDark = Color(0xFFFFD54F) // Oro brillante de luna llena
    val AccentCoralDark = Color(0xFFFFAB91) // Coral suave nocturno
    val AccentLavenderDark = Color(0xFFB39DDB) // Lavanda lunar

    // Notification Colors Dark - Elegancia nocturna
    val SuccessDark = Color(0xFF00695C)    // Verde bosque nocturno
    val SuccessTextDark = Color(0xFFB2DFDB) // Verde menta luminoso
    val ErrorDark = Color(0xFFD84315)      // Naranja volcánico
    val ErrorTextDark = Color(0xFFFFCCBC)  // Melocotón suave
    val WarningDark = Color(0xFFFF8F00)    // Ámbar intenso
    val WarningTextDark = Color(0xFFFFE0B2) // Crema dorada
    val InfoDark = Color(0xFF00838F)       // Azul petróleo
    val InfoTextDark = Color(0xFFB2EBF2)   // Aguamarina luminosa

    // Gradientes para elementos especiales - Inspirados en paisajes
    val GradientPrimary = listOf(
        Color(0xFF00BCD4),  // Turquesa caribeño
        Color(0xFF7CB342)   // Verde jade
    )

    val GradientSunset = listOf(
        Color(0xFFFF6B35),  // Coral del atardecer
        Color(0xFFFFC107)   // Dorado del sol
    )

    val GradientNight = listOf(
        Color(0xFF1E2A38),  // Pizarra nocturna
        Color(0xFF0D1421)   // Océano profundo
    )

    // Colores semánticos para turismo - Inspirados en destinos únicos
    val BeachBlue = Color(0xFF00E5FF)      // Azul Maldivas cristalino
    val MountainGreen = Color(0xFF66BB6A)  // Verde Alpes suizo
    val DesertSand = Color(0xFFFFCC80)     // Arena dorada del Sahara
    val ForestGreen = Color(0xFF43A047)    // Verde Amazonas profundo
    val SkyBlue = Color(0xFF81D4FA)        // Azul cielo patagónico
    val SunsetOrange = Color(0xFFFFAB40)   // Naranja Santorini
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