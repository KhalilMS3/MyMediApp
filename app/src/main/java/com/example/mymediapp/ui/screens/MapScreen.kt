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
import kotlinx.coroutines.tasks.await
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.model.Place
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context



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
    val coroutineScope = rememberCoroutineScope()

    // Initialize the Places SDK if not already initialized
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, "YOUR_API_KEY")
        }
    }

    // State variables
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val permissionGranted = remember { mutableStateOf(false) }
    var pharmacyLocations by remember { mutableStateOf<List<LatLng>>(emptyList()) }

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

    // Fetch nearby pharmacies when userLocation is available
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            coroutineScope.launch(Dispatchers.IO) {
                pharmacyLocations = getNearbyPharmacies(location, context)
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

    // Custom map style to hide other points of interest
  /*  val mapStyleOptions = remember {
        MapStyleOptions("""
            [
              {
                "featureType": "poi",
                "stylers": [
                  { "visibility": "off" }
                ]
              },
              {
                "featureType": "poi.medical",
                "stylers": [
                  { "visibility": "on" }
                ]
              }
            ]
        """.trimIndent())
    }*/

    // Display the Google Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = permissionGranted.value,
            //mapStyleOptions = mapStyleOptions
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true
        )
    ) {
        // Display pharmacy markers
        pharmacyLocations.forEach { location ->
            Marker(
                state = rememberMarkerState(position = location),
                title = "Pharmacy"
            )
        }
    }
}
suspend fun getNearbyPharmacies(
    userLocation: LatLng,
    context: Context
): List<LatLng> {
    val placesClient = Places.createClient(context)

    // Define the place fields to return
    val placeFields = listOf(
        Place.Field.NAME,
        Place.Field.LAT_LNG,
        Place.Field.TYPES
    )

    // Create a request object
    val request = FindCurrentPlaceRequest.newInstance(placeFields)

    // Check permission before making the request
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return emptyList()
    }

    return try {
        val response = placesClient.findCurrentPlace(request).await()
        response.placeLikelihoods
            .filter { likelihood ->
                likelihood.place.types?.contains(Place.Type.PHARMACY) == true
            }
            .mapNotNull { it.place.latLng }
    } catch (e: Exception) {
        emptyList()
    }
}



