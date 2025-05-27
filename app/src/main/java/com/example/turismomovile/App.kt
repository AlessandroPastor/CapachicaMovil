package com.example.turismomovile

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.presentation.navigation.NavigationGraph
import com.example.turismomovile.presentation.screens.viewmodel.ProvideHomeViewModel
import com.example.turismomovile.presentation.theme.ThemeViewModel
import io.dev.kmpventas.presentation.navigation.Routes
import com.example.turismomovile.presentation.theme.AppTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import kotlinx.coroutines.launch

@Composable
fun App() {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false, // Valor inicial
        lifecycle = LocalLifecycleOwner.current.lifecycle // Contexto de lifecycle
    )

    AppTheme(darkTheme = isDarkMode) {
        KoinContext {
            val navController = rememberNavController()
            val sessionManager: SessionManager = koinInject()
            val scope = rememberCoroutineScope()

            ProvideHomeViewModel {
                NavigationGraph(
                    navController = navController,
                    onLogout = {
                        scope.launch {
                            sessionManager.clearSession()
                            navController.navigate(Routes.LAND_PAGE) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}
