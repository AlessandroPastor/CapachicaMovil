package com.example.turismomovile.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    itemName: String
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Confirmar Desactivación")
            },
            text = {
                Text("¿Estás seguro de desactivar '$itemName'?")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}
