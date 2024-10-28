package com.example.mymediapp.ui.screens

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory

class MapScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, "AIzaSyBBsduLzEvQQEDltvXW3LM7RTjfs29_SGc")
        setContent {
            MapScreenContent()
        }
    }
}
@Composable
fun MapScreenContent() {
    val context = LocalContext.current
    val pharmacies = listOf(
        LatLng(59.12381847069611, 11.387099491629524), // Apotek 1 Svanen Halden
        LatLng(59.12103238604425, 11.38714240798957), // Apotek 1 Ørnen Halden
        LatLng(59.13813818120554, 11.376318530258079), // Apotek 1 Brødløs Halden
        LatLng(59.121028810345926, 11.377691821241067), // Vitusapotek Høvleriet
        LatLng(59.12182528695402, 11.382507550921988)   // Apotek 1 Tista
    )

    // State variables
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val permissionGranted = remember { mutableStateOf(false) }

    // Request location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted.value = isGranted
    }

    // Check and request location permission
    LaunchedEffect(Unit) {
        when {
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                permissionGranted.value = true
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Get user's current location
    if (permissionGranted.value) {
        val fusedLocationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }

    // Map camera position state
    val cameraPositionState = rememberCameraPositionState()

    // Move camera to user's location when available
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    // Display the Google Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = permissionGranted.value,
            ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true
        )
    ) {
        // Display pharmacy markers
        pharmacies.forEach { location ->
            Marker(
                state = rememberMarkerState(position = location),
                title = "Pharmacy",
                snippet = "Pharmacy Location"
            )

        }
    }
}

