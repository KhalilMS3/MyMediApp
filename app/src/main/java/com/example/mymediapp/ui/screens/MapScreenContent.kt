package com.example.mymediapp.ui.screens


import android.annotation.SuppressLint

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.rememberMarkerState


@SuppressLint("MissingPermission")
@Composable
fun MapScreenContent() {
    // List of pharmacy locations
    val pharmacies = listOf(
        LatLng(37.7749, -122.4194), // San Francisco
        LatLng(34.0522, -118.2437), // Los Angeles
        LatLng(40.7128, -74.0060)   // New York
    )

    // Set the initial camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pharmacies[0], 10f)
    }

    // Display the Google Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Add markers for each pharmacy
        pharmacies.forEach { location ->
            Marker(
                state = rememberMarkerState(position = location),
                title = "Pharmacy",
                snippet = "Pharmacy Location"
            )
        }
    }
}