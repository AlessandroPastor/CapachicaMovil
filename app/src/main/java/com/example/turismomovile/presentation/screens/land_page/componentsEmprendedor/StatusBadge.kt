package com.example.turismomovile.presentation.screens.land_page.componentsEmprendedor

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EmprendedorStatusBadge(status: Int) {
    val (statusColor, statusText) = when (status) {
        1 -> Color(0xFF4CAF50) to "Activo" // Verde
        0 -> Color(0xFFF44336) to "Inactivo" // Rojo
        else -> Color(0xFFFF9800) to "Pendiente" // Naranja
    }

    Surface(
        color = statusColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}