package com.example.turismomovile


import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
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
            ))
            LaunchedEffect(true){
                if (otorgarp.allPermissionsGranted){
                    Toast.makeText(this@MainActivity, "Permiso concedido",
                        Toast.LENGTH_SHORT).show()
                }else{if (otorgarp.shouldShowRationale){
                    Toast.makeText(this@MainActivity, "La aplicacion requiereeste permiso",
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity, "El permiso fue denegado", Toast.LENGTH_SHORT).show()
                }
                    otorgarp.launchMultiplePermissionRequest()
                }
                Toast.makeText(this@MainActivity,
                    "${isNetworkAvailable(this@MainActivity)}",Toast.LENGTH_LONG
                ).show()
            }

            App() // ← Aquí llamas a tu App Composable
        }
    }
}