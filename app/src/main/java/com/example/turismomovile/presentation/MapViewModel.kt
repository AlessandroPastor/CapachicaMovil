package com.example.turismomovile.presentation
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val locationService: LocationService = LocationServiceFactory.getOrCreate()
    private var locationProvider: DeviceLocationProvider? = null

    private val _userLocation = MutableStateFlow<Point?>(null)
    val userLocation: StateFlow<Point?> = _userLocation

    private val _markers = MutableStateFlow<List<Point>>(emptyList())
    val markers: StateFlow<List<Point>> = _markers

    init {
        fetchMarkers()
        fetchUserLocation()
    }

    private fun fetchMarkers() {
        _markers.value = listOf(
            Point.fromLngLat(-98.0, 39.5), // Centro de EE.UU.
            Point.fromLngLat(-104.9903, 39.7392), // Denver
            Point.fromLngLat(-74.0060, 40.7128), // Nueva York
            Point.fromLngLat(-118.2437, 34.0522) // Los Ángeles
        )
    }

    private fun fetchUserLocation() {
        viewModelScope.launch {

            val request = LocationProviderRequest.Builder()
                .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
                .displacement(0F)
                .accuracy(AccuracyLevel.HIGHEST)
                .build()

            val result = locationService.getDeviceLocationProvider(request)

            if (result.isValue) {
                locationProvider = result.value
                locationProvider?.getLastLocation { location ->
                    if (location != null) {
                        val point = Point.fromLngLat(location.longitude, location.latitude)
                        _userLocation.value = point

                        Log.i("GPS-ViewModel", "Lon:${location.longitude}, Lat:${location.latitude}")
                    }
                }
            } else {
                Log.e("GPS-ViewModel", "Failed to get device location provider")
            }
            delay(1000L)
        }


    }
}