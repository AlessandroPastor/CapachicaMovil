package com.example.turismomovile.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MyCustomSwipeRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeRefreshState(isRefreshing)

    SwipeRefresh(
        state = state,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
        indicator = { refreshState, trigger ->
            DynamicRefreshIndicator(
                state = refreshState,
                refreshTrigger = trigger
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun DynamicRefreshIndicator(
    state: SwipeRefreshState,
    refreshTrigger: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp), // Mayor altura para mejor UX
        contentAlignment = Alignment.Center
    ) {
        if (state.isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Desliza para actualizar",
                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer {
                        rotationZ = state.indicatorOffset * 0.2f // Rotación más pronunciada
                    },
            )
        }
    }
}