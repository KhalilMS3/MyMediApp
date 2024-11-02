package com.example.mymediapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                Text("This is our home screen!")
                Spacer(modifier = Modifier.height(16.dp))

                // Button for Profile-screen
                Button(onClick = {
                    navController.navigate("Profile")
                }) {
                    Text("View Profile")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Button for back to login-screen
                    navController.navigate("start") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Text("Logout")
                }
            }
        }
    )
}
