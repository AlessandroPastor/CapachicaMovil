package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.turismomovile.data.remote.dto.configuracion.Emprendedor
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.MainTopAppBar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedoresScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false, lifecycle = LocalLifecycleOwner.current.lifecycle)
    val stateEmprendedor by viewModel.stateEmprendedor.collectAsState()
    val isRefreshing = remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val currentSection by viewModel.currentSection

    // ✅ Cargamos datos solo una vez al entrar
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.PRODUCTS)
        viewModel.loadEmprendedores()
    }

    // ✅ Paramos refresh cuando termina la carga
    LaunchedEffect(stateEmprendedor.isLoading) {
        if (!stateEmprendedor.isLoading) {
            isRefreshing.value = false
        }
    }

    // ✅ Manejamos notificaciones de error
    LaunchedEffect(stateEmprendedor.notification) {
        if (stateEmprendedor.notification.isVisible) {
            notificationState.showNotification(
                message = stateEmprendedor.notification.message,
                type = stateEmprendedor.notification.type,
                duration = stateEmprendedor.notification.duration
            )
        }
    }

    AppTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    title = "Emprendedores",
                    isSearchVisible = isSearchVisible,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Implementar búsqueda si deseas */ },
                    onToggleSearch = { isSearchVisible = true },
                    onCloseSearch = {
                        isSearchVisible = false
                        searchQuery = ""
                    },
                    onClickExplorer = onClickExplorer,
                    onStartClick = onStartClick,
                    isDarkMode = isDarkMode,
                    onToggleTheme = { themeViewModel.toggleTheme() }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    currentSection = currentSection,
                    onSectionSelected = { section ->
                        viewModel.onSectionSelected(section)
                    },
                    navController = navController
                )
            }
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                PullToRefreshComponent(
                    isRefreshing = isRefreshing.value,
                    onRefresh = {
                        isRefreshing.value = true
                        viewModel.loadEmprendedores()
                    }
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        items(stateEmprendedor.items) { emprendedor ->
                            EmprendedorItem(emprendedor)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmprendedorItem(emprendedor: Emprendedor) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = emprendedor.razonSocial ?: "Sin nombre",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = emprendedor.address ?: "Sin dirección",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

