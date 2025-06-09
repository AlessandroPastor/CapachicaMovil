package com.example.turismomovile.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.debugoptions.MapViewDebugOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.PuckBearing


import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.example.turismomovile.R


@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }

    val markers by viewModel.markers.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            mapView.debugOptions = setOf(
                MapViewDebugOptions.PARSE_STATUS,
                MapViewDebugOptions.MODEL_BOUNDS,
            )
        }

        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }

        val image = rememberIconImage(resourceId = R.drawable.marker_red)

        markers.forEach { point ->
            PointAnnotation(point = point) {
                iconImage = image
                interactionsState.onClicked {
                    Toast.makeText(context, "Marker clicked", Toast.LENGTH_SHORT).show()
                    true
                }.onDragged {
                    // handle drag
                }.also {
                    it.isDraggable = true
                }
            }
        }

        // Opcional: círculo de ejemplo
        CircleAnnotation(point = Point.fromLngLat(18.06, 59.31)) {
            circleRadius = 8.0
            circleColor = Color(0xffee4e8b)
            circleStrokeWidth = 2.0
            circleStrokeColor = Color(0xffffffff)
        }

        // Mostrar marcador de ubicación del usuario
        userLocation?.let { point ->
            val userIcon = rememberIconImage(resourceId = R.drawable.maker_blue)
            PointAnnotation(point = point) {
                iconImage = userIcon
                interactionsState.onClicked {
                    Toast.makeText(context, "User location clicked", Toast.LENGTH_SHORT).show()
                    true
                }.onDragged { }.also {
                    it.isDraggable = true
                }
            }
        }
    }
}