package com.example.turismomovile.presentation.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.turismomovile.data.remote.dto.ventas.ReservaUsuarioDTO
import com.example.turismomovile.presentation.components.AppEmptyState
import com.example.turismomovile.presentation.components.AppPaginationControls
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.screens.land_page.ReservaViewModel
import org.koin.compose.koinInject

@Composable
fun ReservaUserScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: ReservaViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        viewModel.loadReservas()
    }

    LaunchedEffect(state.notification) {
        state.notification.takeIf { it.isVisible }?.let {
            notificationState.showNotification(
                message = it.message,
                type = it.type,
                duration = it.duration
            )
        }
    }

    NotificationHost(state = notificationState) {
        Scaffold(
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(70.dp))

                    // SearchBar
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.loadReservas(searchQuery = it.ifEmpty { null })
                        },
                        placeholderText = "Buscar tus Reservas",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        when {
                            state.items.isEmpty() -> {
                                AppEmptyState(title = "No se encontraron resultados",
                                    description = "No se encontraron reservas para tu búsqueda")
                            }
                            else -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(bottom = 80.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.items, key = { it.id ?: "" }) { reserva ->
                                        ReservaCard(
                                            reserva = reserva,
                                            onClick = {},
                                            onDelete = {},
                                            onAddImage = {}
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Paginación
                    if (state.totalPages > 1) {
                        AppPaginationControls(
                            currentPage = state.currentPage,
                            totalPages = state.totalPages,
                            onPreviousPage = {
                                viewModel.loadReservas((state.currentPage - 1), searchQuery)
                            },
                            onNextPage = {
                                viewModel.loadReservas((state.currentPage + 1), searchQuery)
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCard(
    reserva: ReservaUsuarioDTO,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onAddImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado de la reserva
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reserva.code ?: "Reserva sin código",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Estado: ${reserva.status?.replaceFirstChar { it.uppercase() } ?: "No disponible"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = when (reserva.status?.lowercase()) {
                            "confirmado" -> MaterialTheme.colorScheme.primary
                            "pendiente" -> MaterialTheme.colorScheme.secondary
                            "cancelado" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Detalles principales
            reserva.reserve_details.firstOrNull()?.let { detalle ->
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "📍 ${detalle.lugar ?: "Lugar no especificado"}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (!detalle.description.isNullOrBlank()) {
                        Text(
                            text = detalle.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Detalles financieros
            reserva.reserve_details.forEachIndexed { index, detalle ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Costo unitario:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = detalle.costo?.toString() ?: "No disponible",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cantidad:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = detalle.cantidad?.toString() ?: "No disponible",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = detalle.total?.toString() ?: "No disponible",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Información del usuario
            reserva.user?.let { user ->
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "👤 Reservado por:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${user.name} ${user.last_name}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!user.email.isNullOrBlank()) {
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholderText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
