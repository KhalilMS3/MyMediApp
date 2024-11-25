package com.example.mymediapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mymediapp.preferences.LocalUserPreferences

@Composable
fun SettingsScreen(navController: NavController) {
    val userPreferences = LocalUserPreferences.current

    // Opprett en ViewModel ved hjelp av vår tilpassede Factory
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(userPreferences)
    )

    val isDarkMode by viewModel.darkModeFlow.collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Choose Theme", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        // Alternativer for tema
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { viewModel.setDarkMode(false) }) {
                Text("Light Mode")
            }
            Button(onClick = { viewModel.setDarkMode(true) }) {
                Text("Dark Mode")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vis nåværende modus
        Text(
            text = if (isDarkMode) "Dark Mode Enabled" else "Light Mode Enabled",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
