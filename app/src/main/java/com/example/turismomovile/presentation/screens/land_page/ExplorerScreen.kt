package com.example.turismomovile.presentation.screens.land_page


import androidx.compose.runtime.*

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.turismomovile.R
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.presentation.MapScreen
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject


@Composable
fun ExplorerScreen(
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {

    val visible = remember { mutableStateOf(false) }
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false, // Valor inicial
        lifecycle = LocalLifecycleOwner.current.lifecycle // Contexto de lifecycle
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stateAso by viewModel.stateAso.collectAsStateWithLifecycle()
    val isRefreshing = remember { mutableStateOf(false) }
    var selectedAsociacion by remember { mutableStateOf<Asociacion?>(null) }

    // Imágenes
    val sliderImages = listOf(
        R.drawable.fondo,
        R.drawable.fondo2,
        R.drawable.capachica,
    )

    // Efectos
    LaunchedEffect(Unit) { visible.value = true }

    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    MapScreen()

    LaunchedEffect(stateAso.notification) {
        if (stateAso.notification.isVisible) {
            notificationState.showNotification(
                message = stateAso.notification.message,
                type = stateAso.notification.type,
                duration = stateAso.notification.duration
            )
        }
    }
}