package com.example.turismomovile


import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.turismomovile.Utils.isNetworkAvailable
import com.example.turismomovile.di.appModule
import com.example.turismomovile.presentation.MapScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val otorgarp = rememberMultiplePermissionsState(permissions =
                listOf(
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA,
                )
            )

            val snackbarHostState = remember { SnackbarHostState() }

            // Mostramos mensajes dependiendo del estado de permisos y red
            LaunchedEffect(Unit) {
                if (otorgarp.allPermissionsGranted) {
                    snackbarHostState.showSnackbar("Permiso concedido correctamente")
                } else {
                    if (otorgarp.shouldShowRationale) {
                        snackbarHostState.showSnackbar("La aplicación requiere permisos para funcionar")
                    } else {
                        snackbarHostState.showSnackbar("Permisos denegados permanentemente")
                    }
                    otorgarp.launchMultiplePermissionRequest()
                }

                val connected = isNetworkAvailable(this@MainActivity)
                snackbarHostState.showSnackbar(
                    if (connected) "Acceso a red disponible"
                    else "No hay acceso a red"
                )
            }
            App() // ← Aquí llamas a tu App Composable
        }
    }
}
