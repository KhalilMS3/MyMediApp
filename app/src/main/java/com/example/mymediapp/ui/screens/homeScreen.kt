package com.example.mymediapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun homeScreen(navController: NavController, viewModel: ReminderViewModel = viewModel()) {

    // Fetching reminders list from viewModel
    val reminders by viewModel.reminders.observeAsState(emptyList())
    // Firebase auth and firestore
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var userName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Fetch user information from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Get the username from Firestore document
                        val fetchedName = document.getString("name") ?: ""
                        userName = fetchedName
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("homeScreen", "Error fetching user data", e)
                    userName = "Bruker"
                }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Hei, $userName",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = "Kommende medisiner",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Content Section
        if (reminders.isEmpty()) {
            item {
                Text("Ingen kommende medisiner")
            }
        } else {
            items(reminders) { reminder ->
                MedicineItem(reminder)
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MedicineItem(reminder: Reminder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reminder.medicineName,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Neste dose: ${reminder.startTime.hours}:${reminder.startTime.minutes}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}