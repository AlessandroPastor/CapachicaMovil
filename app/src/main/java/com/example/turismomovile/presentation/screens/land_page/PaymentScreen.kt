package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.turismomovile.R
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.dto.ventas.ReservaUsuarioDTO
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.example.turismomovile.data.remote.dto.ventas.Payments


@OptIn(ExperimentalMaterial3Api::class)
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
    var paymentStep by remember { mutableStateOf(1) }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    val reserva by viewModel.reserva.collectAsStateWithLifecycle()

    val animatedProgress by animateFloatAsState(
        targetValue = when (paymentStep) {
            1 -> 0.33f
            2 -> 0.66f
            3 -> 1f
            else -> 0f
        },
        label = "progressAnimation"
    )

    LaunchedEffect(Unit) {
        user = withContext(Dispatchers.IO) { sessionManager.getUser() }
        viewModel.loadReserva(reservaId)
    }

    LaunchedEffect(reserva) {
        // Si la reserva ya está pagada, mostrar directamente la confirmación
        if (reserva?.status?.lowercase() == "pagada") {
            paymentStep = 3
        }
    }

        // Resto del código permanece igual...
        NotificationHost(state = notificationState) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Proceso de Pago") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (reserva?.status?.lowercase()) {
                        "pagada" -> {
                            AlreadyPaidConfirmation(
                                modifier = Modifier.fillMaxSize(),
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        "cancelada" -> {
                            ReservationCancelled(
                                modifier = Modifier.fillMaxSize(),
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        else -> {
                            // Flujo normal de pago para estado "pendiente"
                            LinearProgressIndicator(
                                progress = animatedProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )

                        Spacer(modifier = Modifier.height(16.dp))

                        when (paymentStep) {
                            1 -> PaymentDetailsStep(
                                reserva = reserva,
                                user = user,
                                onContinue = { paymentStep = 2 }
                            )
                            2 -> PaymentMethodStep(
                                cardNumber = cardNumber,
                                cardExpiry = cardExpiry,
                                cardCvv = cardCvv,
                                cardHolder = cardHolder,
                                onCardNumberChange = { cardNumber = it },
                                onCardExpiryChange = { cardExpiry = it },
                                onCardCvvChange = { cardCvv = it },
                                onCardHolderChange = { cardHolder = it },
                                onPayClick = { viewModel.createPayment(reservaId) },
                                isLoading = state.isLoading,
                                total = reserva?.total
                            )
                            3 -> PaymentConfirmationStep(state.payment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlreadyPaidConfirmation(
        modifier: Modifier = Modifier,
        onBackClick: () -> Unit
    ) {
        Column(
            modifier = modifier.padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Ya pagado",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Reserva ya pagada",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Esta reserva ya ha sido pagada anteriormente. No es necesario realizar otro pago.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver atrás", style = MaterialTheme.typography.bodyLarge)
            }
        }
}

    @Composable
    private fun ReservationCancelled(
        modifier: Modifier = Modifier,
        onBackClick: () -> Unit
    ) {
        Column(
            modifier = modifier.padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = "Cancelada",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Reserva cancelada",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Esta reserva ha sido cancelada y no puede ser pagada.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Volver atrás", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }


@Composable
private fun PaymentDetailsStep(
    reserva: ReservaUsuarioDTO?,
    user: User?,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Detalles de la Reserva",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Tarjeta de reserva
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        reserva?.code ?: reserva?.id ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Divider()

                user?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cliente:", style = MaterialTheme.typography.bodyMedium)
                        Text(it.fullName ?: "${it.name} ${it.last_name}",
                            style = MaterialTheme.typography.bodyLarge)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Email:", style = MaterialTheme.typography.bodyMedium)
                        Text(it.email, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Divider()

                val firstDetail = reserva?.reserve_details?.firstOrNull()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        firstDetail?.emprendimiento_service?.name ?: "-",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        reserva?.created_at?.take(10) ?: "-",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val personas = reserva?.reserve_details?.sumOf { it.cantidad?.toIntOrNull() ?: 0 } ?: 0

                    Text("Cantidad:", style = MaterialTheme.typography.bodyMedium)
                    Text("$personas", style = MaterialTheme.typography.bodyLarge)
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total a pagar:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${reserva?.total ?: ""}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                    )
                    )
                }
            }
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Continuar al Pago", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun PaymentMethodStep(
    cardNumber: String,
    cardExpiry: String,
    cardCvv: String,
    cardHolder: String,
    onCardNumberChange: (String) -> Unit,
    onCardExpiryChange: (String) -> Unit,
    onCardCvvChange: (String) -> Unit,
    onCardHolderChange: (String) -> Unit,
    onPayClick: () -> Unit,
    isLoading: Boolean,
    total: String?
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Método de Pago",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Tarjeta de crédito simulada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3A7BD5),
                            Color(0xFF00D2FF)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(R.drawable.visa),
                        contentDescription = "Visa",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = cardNumber.ifEmpty { "•••• •••• •••• ••••" },
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TITULAR",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                        Text(
                            text = cardHolder.ifEmpty { "NOMBRE APELLIDO" },
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                    }

                    Column {
                        Text(
                            text = "EXPIRA",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                        Text(
                            text = cardExpiry.ifEmpty { "MM/AA" },
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                    }

                    Box {} // Espaciador
                }
            }
        }

        // Formulario de tarjeta
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    if (it.length <= 19) {
                        onCardNumberChange(formatCardNumber(it))
                    }
                },
                label = { Text("Número de tarjeta") },
                placeholder = { Text("1234 5678 9012 3456") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.CreditCard, contentDescription = null)
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = cardExpiry,
                    onValueChange = {
                        if (it.length <= 5) {
                            onCardExpiryChange(formatExpiryDate(it))
                        }
                    },
                    label = { Text("Vencimiento") },
                    placeholder = { Text("MM/AA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                )

                OutlinedTextField(
                    value = cardCvv,
                    onValueChange = {
                        if (it.length <= 3) {
                            onCardCvvChange(it)
                        }
                    },
                    label = { Text("CVV") },
                    placeholder = { Text("123") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    }
                )
            }

            OutlinedTextField(
                value = cardHolder,
                onValueChange = onCardHolderChange,
                label = { Text("Nombre del titular") },
                placeholder = { Text("Como aparece en la tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )
        }

        Button(
            onClick = onPayClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = cardNumber.length == 19 &&
                    cardExpiry.length == 5 &&
                    cardCvv.length == 3 &&
                    cardHolder.isNotBlank() &&
                    !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Pagar \$${total ?: ""}", style = MaterialTheme.typography.bodyLarge)
            }
        }

        // Métodos alternativos
        Text(
            text = "O pagar con",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            IconButton(
                onClick = { /* PayPal */ },
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.payl),
                    contentDescription = "PayPal",
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = { /* Google Pay */ },
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.googlsss),
                    contentDescription = "Google Pay",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PaymentConfirmationStep(payment: Payments?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { // Icono de confirmación principal
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
                text = "¡Pago Completado!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
                text = "Tu reserva ha sido confirmada.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
        )
        payment?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Código: ${it.code}",
                style = MaterialTheme.typography.bodyMedium
            )
            it.total?.let { total ->
                Text(
                    text = "Total: S/ $total",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}


// Funciones de formato
private fun formatCardNumber(input: String): String {
    val cleaned = input.filter { it.isDigit() }
    return cleaned.chunked(4).joinToString(" ")
}

private fun formatExpiryDate(input: String): String {
    val cleaned = input.filter { it.isDigit() }
    return when {
        cleaned.length <= 2 -> cleaned
        else -> "${cleaned.take(2)}/${cleaned.drop(2).take(2)}"
    }
}