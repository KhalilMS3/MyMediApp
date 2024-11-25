package com.example.mymediapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun myDietScreen(navController: NavController) {

    Text("My Diet Screen",
        modifier = Modifier.padding(10.dp))
}
