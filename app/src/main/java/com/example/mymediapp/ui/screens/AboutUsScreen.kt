package com.example.mymediapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding



@Composable
fun AboutUsScreen(navController: NavController) {
    val team17info = "Team 17 consists of three members: Frans, Khlil, and Dilbren, who are students at Høyskolen i Østfold. Our goal is to create an app that helps people remember when to take their medications and log their food intake. This will assist users in keeping track of their health routines and provide doctors with a quick overview of their diet during visits."
    val email = "postmottak@hiof.no"
    val phone = "69 60 80 00"
    val location = LatLng(59.1292, 11.3528) // coordinates for Høyskolen i Østfold
    val txtlocation = "Høgskolen i Østfold\n" +
            "Postboks 700\n" +
            "NO-1757 Halden\n" +
            "Norway\n"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Enables scrolling if content overflows
        horizontalAlignment = Alignment.Start
    ) {
        // Title
        Text(
            text = "Team 17",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = team17info,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Contact Information
        Text(
            text = "Contact Information:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Email: $email",
            style = MaterialTheme.typography.body1,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Phone: $phone",
            style = MaterialTheme.typography.body1,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 16.dp)
        )



        // Google Map
        Text(
            text = "Postadress:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = txtlocation,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Find us on map:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Map View
        MapViewComposable(location = location)

    }
}

@Composable
fun MapViewComposable(location: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    GoogleMap(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false)
    ) {
        Marker(
            state = MarkerState(position = location),
            title = "Høyskolen i Østfold",
            snippet = "Our Location"
        )
    }
}
