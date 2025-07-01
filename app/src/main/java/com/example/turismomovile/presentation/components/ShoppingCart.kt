package com.example.turismomovile.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

// Modelo de item de carrito
data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val imageRes: Int? = null,
    val quantity: Int = 1
)

@Composable
fun ShoppingCart(
    items: List<CartItem>,
    onItemQuantityChange: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    modifier: Modifier = Modifier,
    checkoutButton: @Composable (() -> Unit)? = null
) {
    val total = items.sumOf { it.price * it.quantity }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Tu carrito",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (items.isEmpty()) {
            Text("Tu carrito está vacío.", color = Color.Gray)
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f, false)
                    .padding(bottom = 16.dp)
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    CartItemRow(
                        item = item,
                        onChangeQuantity = { newQty ->
                            onItemQuantityChange(item, newQty)
                        },
                        onRemove = {
                            onRemoveItem(item)
                        }
                    )
                    if (index < items.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total: $${"%.2f".format(total)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                checkoutButton?.invoke()
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onChangeQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            item.imageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold)
                Text("$${"%.2f".format(item.price)}", color = Color.Gray)
            }
            QuantitySelector(
                quantity = item.quantity,
                onQuantityChange = onChangeQuantity
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Button(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            enabled = quantity > 1,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(24.dp)
        ) {
            Text("-", fontWeight = FontWeight.Bold)
        }
        Text(
            quantity.toString(),
            modifier = Modifier.width(24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = { onQuantityChange(quantity + 1) },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(24.dp)
        ) {
            Text("+", fontWeight = FontWeight.Bold)
        }
    }
}
