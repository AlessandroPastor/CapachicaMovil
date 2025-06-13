package io.dev.kmpventas.presentation.navigation

object Routes {
    // Rutas base
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val ONBOARDING = "onboarding"
    const val DEVICE_INFO = "device_info"
    const val LAND_PAGE = "landpage"
    const val EXPLORATE = "explorate"
    const val PRODUCTOS = "productos"

    object HomeScreen {
        private const val HOME_PREFIX = "/homeScreen"

        // Configuración
        object Setup {
            private const val SETUP = "$HOME_PREFIX/setup"
            const val MUNICIPALIDAD = "$SETUP/municipalidad"
            const val USUARIOS = "$SETUP/user"
            const val SEPTIONS = "$SETUP/sections"
            const val MODULE = "$SETUP/module"
            const val PARENT_MODULE = "$SETUP/parent-module"
            const val ROLE = "$SETUP/role"
            const val ASOCIACIONES = "$SETUP/asociaciones"
        }
    }
}