package com.example.mymediapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun loginScreen(navController: NavController) {

    Text("LOG IN",
        modifier = Modifier.padding(10.dp)
    )
}
