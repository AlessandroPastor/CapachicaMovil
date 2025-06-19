package com.example.turismomovile.presentation.screens.land_page.componentsEmprendedor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun LogoDeFamilia(logoUrl: String?) {
    if (!logoUrl.isNullOrEmpty()) {
        Surface(
            modifier = Modifier
                .size(40.dp) // Tamaño del badge
                .shadow(4.dp, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        ) {
            AsyncImage(
                model = logoUrl,
                contentDescription = "Logo del Emprendedor",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        // Si no hay logo, puedes mostrar un ícono o el color de estado predeterminado
        Surface(
            modifier = Modifier
                .size(40.dp) // Tamaño del badge
                .shadow(4.dp, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Person, // Icono predeterminado si no hay logo
                contentDescription = "Icono Emprendedor",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                tint = Color.White
            )
        }
    }
}
