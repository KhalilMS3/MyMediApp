package com.example.mymediapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest

@Composable
fun UserView(navController: NavController) {
    // State variables for input fields and image URI
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize() // Fyller hele tilgjengelig plass
            .padding(16.dp), // Legger til 16dp padding rundt hele kolonnen
        horizontalAlignment = Alignment.CenterHorizontally // Sentraliserer innholdet horisontalt
    ) {
        // Første TextField for "Fornavn"
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Fornavn") }, // Viser hint-tekst som "Fornavn"
            modifier = Modifier
                .fillMaxWidth() // Fyller bredden av skjermen
                .padding(vertical = 8.dp) // Legger til 8dp padding over og under
        )

        // Andre TextField for "Etternavn"
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Etternavn") }, // Viser hint-tekst som "Etternavn"
            modifier = Modifier
                .fillMaxWidth() // Fyller bredden av skjermen
                .padding(vertical = 8.dp) // Legger til 8dp padding over og under
        )

        // Knappekomponent for å laste opp bilde
        Button(
            onClick = {
                // Her kan du legge til handling for bildeopplastning
            },
            modifier = Modifier
                .padding(top = 16.dp) // Legger til 16dp topp-marg for å skille fra TextFields
        ) {
            Text("Last opp bilde") // Tekst som vises på knappen
        }

        // Spacer legger til plass mellom knappen og bildet
        Spacer(modifier = Modifier.height(20.dp))

        // Viser bildet hvis imageUri ikke er null
        imageUri?.let {
            Image(
                painter = rememberImagePainter(
                    ImageRequest.Builder(context)
                        .data(it) // Setter bildekilden
                        .build()
                ),
                contentDescription = "Profilbilde", // Beskrivelse av bildet
                modifier = Modifier
                    .size(150.dp) // Setter størrelse til 150dp x 150dp
                    .padding(top = 20.dp), // Legger til 20dp topp-marg
                alignment = Alignment.Center, // Sentraliserer bildet
                contentScale = ContentScale.Crop // Beskjærer bildet for å fylle hele ImageView
            )
        }

        // Spacer for å legge til avstand mellom bildet og lagre-knappen
        Spacer(modifier = Modifier.height(20.dp))

        // Knappekomponent for å lagre brukerens informasjon
        Button(
            onClick = {
                // Her kan du legge til handling for å lagre brukerens informasjon
            },
            modifier = Modifier
                .padding(top = 20.dp) // Legger til 20dp topp-marg for å skille fra bildet
        ) {
            Text("Lagre informasjon") // Tekst som vises på knappen
        }
    }
}
