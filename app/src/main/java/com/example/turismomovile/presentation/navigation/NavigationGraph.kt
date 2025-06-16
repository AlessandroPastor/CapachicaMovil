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
import com.example.turismomovile.presentation.screens.land_page.EmprendedoresScreen
import com.example.turismomovile.presentation.screens.land_page.EventsScreen
import com.example.turismomovile.presentation.screens.land_page.ExplorerScreen
import com.example.turismomovile.presentation.screens.land_page.LangPageViewModel
import com.example.turismomovile.presentation.screens.land_page.PlacesScreen
import com.example.turismomovile.presentation.screens.land_page.RecommendationsScreen
import com.example.turismomovile.presentation.screens.land_page.ServiceScreen
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
        Routes.EXPLORATE,
    )

    LaunchedEffect(navController) {
        snapshotFlow { navController.currentBackStackEntry }
            .collect { backStackEntry ->
                val route = backStackEntry?.destination?.route
                val token = sessionManager.getUser()?.token
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

        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    scope.launch {
                        val isFirstTime = !sessionManager.isOnboardingCompleted()
                        if (isFirstTime) {
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Routes.LAND_PAGE) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // Onboarding Screen
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
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

        // Welcome / Land Page
        composable(Routes.LAND_PAGE) {
            WelcomeScreen(
                navController = navController,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                }
            )
        }

        // Explorer Screen
        composable(Routes.EXPLORATE) {
            ExplorerScreen(
            )
        }

        // Login Screen
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

        // Home Screen (privado)
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

        // Productos -> EmprendedoresScreen
        composable(Routes.PRODUCTS) {
            EmprendedoresScreen(
                navController = navController,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                }
            )
        }

        // Agregamos ahora los demás screens del BottomNavigation 🔥

        composable(Routes.SERVICES) {
            ServiceScreen(navController = navController,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                })
        }

        composable(Routes.PLACES) {
            PlacesScreen(navController = navController,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                })
        }

        composable(Routes.EVENTS) {
            EventsScreen(navController = navController,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                })
        }

        composable(Routes.RECOMMENDATIONS) {
            RecommendationsScreen(navController = navController,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                })
        }


        // Tourist Info (solo si lo necesitas)
        composable(Routes.DEVICE_INFO) {
            TouristInfoScreen(navController)
        }

        // Finalmente, mantenemos la navegación privada de configuración
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
