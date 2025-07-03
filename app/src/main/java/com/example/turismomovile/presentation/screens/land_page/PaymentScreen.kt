package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PaymentScreen(
    reservaId: String,
    navController: NavHostController,
    viewModel: PaymentViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        user = withContext(Dispatchers.IO) { sessionManager.getUser() }
    }

    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            viewModel.clearSuccess()
            navController.navigate(Routes.HomeScreen.Product.RESERVAS) {
                popUpTo(Routes.HOME)
            }
        }
    }

    NotificationHost(state = notificationState) {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pagar Reserva",
                        style = MaterialTheme.typography.titleLarge
                    )
                    user?.let {
                        Text(text = it.fullName ?: "${it.name} ${it.last_name}")
                        Text(text = it.email)
                    }
                    Text(text = "Reserva ID: $reservaId")
                    Button(
                        onClick = { viewModel.createPayment(reservaId) },
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Pagar")
                        }
                    }
                }
            }
        }
    }
}