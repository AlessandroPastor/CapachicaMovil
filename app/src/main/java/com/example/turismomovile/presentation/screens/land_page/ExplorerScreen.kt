package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.turismomovile.R
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.presentation.MapScreen
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject


@Composable
fun ExplorerScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
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