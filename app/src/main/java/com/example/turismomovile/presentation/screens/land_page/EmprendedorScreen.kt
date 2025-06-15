package com.example.turismomovile.presentation.screens.land_page
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.turismomovile.data.remote.dto.configuracion.Emprendedor
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.SearchBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedoresScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    // ✅ Cada vez que entra, marcamos sección PRODUCTS
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.PRODUCTS, navController)
    }

    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false, lifecycle = LocalLifecycleOwner.current.lifecycle)
    val stateEmprendedor by viewModel.stateEmprendedor.collectAsState()
    val isRefreshing = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val isSearchVisible = remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    val currentSection by viewModel.currentSection

    // Cargar emprendedores al refrescar
    fun handleRefresh() {
        isRefreshing.value = true
        viewModel.loadEmprendedores()
        isRefreshing.value = false
    }

    // Mostrar notificaciones si hay errores
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
                TopAppBar(
                    title = {
                        Text(
                            text = "Emprendedores",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = { isSearchVisible.value = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }
                        IconButton(onClick = onClickExplorer) {
                            Icon(Icons.Default.Explore, contentDescription = "Explorar")
                        }
                        IconButton(onClick = { themeViewModel.toggleTheme() }) {
                            Icon(
                                if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Cambiar tema"
                            )
                        }
                        Button(
                            onClick = onStartClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Ingresar", fontSize = 14.sp)
                        }
                    }
                )
            },

            bottomBar = {
                BottomNavigationBar(
                    currentSection = currentSection,
                    onSectionSelected = { viewModel.onSectionSelected(it, navController) },
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
                    onRefresh = { handleRefresh() }
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Lista de Emprendedores
                            stateEmprendedor.items.forEach { emprendedor ->
                                EmprendedorItem(emprendedor)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
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
        Text(text = emprendedor.razonSocial ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)
        Text(text = emprendedor.address ?: "Sin dirección", style = MaterialTheme.typography.bodySmall)
    }
}


