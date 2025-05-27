package com.example.turismomovile.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.presentation.screens.configuration.role.asociaciones.AsociacionesScreen
import io.dev.kmpventas.presentation.navigation.Routes
import com.example.turismomovile.presentation.screens.configuration.role.modules.ModuleScreen
import com.example.turismomovile.presentation.screens.configuration.role.modulos_padres.ParentModuleScreen
import io.dev.kmpventas.presentation.screens.configuration.role.municipalidad.MunicipalidadScreen
import com.example.turismomovile.presentation.screens.configuration.role.role.RoleScreen
import com.example.turismomovile.presentation.screens.login.LoginScreen
import com.example.turismomovile.presentation.screens.dashboard.HomeViewModel
import com.example.turismomovile.presentation.screens.land_page.ExplorerScreen
import io.dev.kmpventas.presentation.screens.land_page.LangPageViewModel
import com.example.turismomovile.presentation.screens.land_page.WelcomeScreen
import com.example.turismomovile.presentation.screens.navigation.BaseScreenLayout
import com.example.turismomovile.presentation.screens.navigation.DefaultScreen
import com.example.turismomovile.presentation.screens.navigation.OnboardingScreen
import com.example.turismomovile.presentation.screens.navigation.SplashScreen
import com.example.turismomovile.presentation.screens.navigation.TouristInfoScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    sessionManager: SessionManager = koinInject()
) {
    val viewModel: HomeViewModel = koinInject()
    val viewModelLangPage: LangPageViewModel = koinInject()
    val scope = rememberCoroutineScope()

    val publicRoutes = setOf(
        Routes.SPLASH,
        Routes.ONBOARDING,
        Routes.LAND_PAGE,
        Routes.LOGIN,
        Routes.EXPLORATE
    )
    LaunchedEffect(navController) {
        snapshotFlow { navController.currentBackStackEntry }
            .collect { backStackEntry ->
                val route = backStackEntry?.destination?.route
                val token = sessionManager.getUser()?.token // ⚠️ suspend, asegúrate de usar `collect` en LaunchedEffect
                println("🧠 Token: $token | Ruta actual: $route")

                if (token.isNullOrEmpty() && route !in publicRoutes) {
                    onLogout()
                    navController.navigate(Routes.LAND_PAGE) {
                        popUpTo(0)
                    }
                }
            }
    }




    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    scope.launch {
                        val isFirstTime = !sessionManager.isOnboardingCompleted()
                        if (isFirstTime) {
                            println("🎓 Mostrando ONBOARDING")
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        } else {
                            println("✅ Splash finalizado, navegando a LAND_PAGE")
                            navController.navigate(Routes.LAND_PAGE) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            println("📘 Mostrando OnboardingScreen")
            OnboardingScreen (
                onComplete = {
                    scope.launch {
                        sessionManager.setOnboardingCompleted(true)
                        navController.navigate(Routes.LAND_PAGE) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                }
            )
        }



        composable(Routes.LAND_PAGE) {
            WelcomeScreen(
                onStartClick = {
                    println("🚪 Usuario quiere ingresar. Navegando a LOGIN")
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    println("🚪 Usuario quiere explorar. Navegando a EXPLORER_SCREEN")
                    navController.navigate(Routes.EXPLORATE) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.EXPLORATE) {
            ExplorerScreen(
                onStartClick = {
                    println("🌍 Usuario regresó a la pantalla de bienvenida")
                    navController.navigate(Routes.LAND_PAGE) {
                        popUpTo(Routes.EXPLORATE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    println("🌍 Usuario quiere explorar más")
                    navController.navigate(Routes.EXPLORATE)
                }
            )
        }







        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { user ->
                    scope.launch {
                        sessionManager.saveUser(user)
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onBackPressed = {
                    navController.navigate(Routes.LAND_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }




        composable(Routes.HOME) {
            BaseScreenLayout(
                navController = navController,
                title = "Inicio",
                onLogout = {
                    scope.launch {
                        sessionManager.clearSession()
                        navController.navigate(Routes.LAND_PAGE) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            ) { paddingValues ->
                DefaultScreen(
                    title = "Inicio",
                    route = Routes.HOME,
                    navController = navController,
                    onLogout = onLogout,
                    paddingValues = paddingValues
                )
            }
        }


        // Other routes like DeviceInfo or configuration screens
        composable(Routes.DEVICE_INFO) {
            TouristInfoScreen(navController)
        }

        // Setup routes for the menu
        setupMenuRoutes(
            navGraphBuilder = this,
            navController = navController,
            onLogout = onLogout
        )
    }
}

private fun setupMenuRoutes(
    navGraphBuilder: NavGraphBuilder,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val implementedRoutes = mapOf(
        Routes.HomeScreen.Setup.MODULE to "Módulos",
        Routes.HomeScreen.Setup.PARENT_MODULE to "Módulos Padres",
        Routes.HomeScreen.Setup.ROLE to "Roles",
        Routes.HomeScreen.Setup.MUNICIPALIDAD to "Municipalidad",
        Routes.HomeScreen.Setup.ASOCIACIONES to "Asociaciones",
        Routes.HomeScreen.Setup.USUARIOS to "Usuarios",
        Routes.HomeScreen.Setup.SEPTIONS to "Secciones"


    )

    implementedRoutes.forEach { (route, title) ->
        navGraphBuilder.composable(route) {

            BaseScreenLayout(
                navController = navController,
                title = title,
                onLogout = onLogout
            ) { paddingValues ->
                when (route) {
                    Routes.HomeScreen.Setup.ROLE -> RoleScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Setup.MODULE -> ModuleScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )

                    Routes.HomeScreen.Setup.PARENT_MODULE -> ParentModuleScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Setup.MUNICIPALIDAD -> MunicipalidadScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Setup.ASOCIACIONES -> AsociacionesScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    else -> DefaultScreen(
                        title = title,
                        route = route,
                        navController = navController,
                        onLogout = onLogout,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}
