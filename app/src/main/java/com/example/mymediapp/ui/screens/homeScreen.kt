package com.example.mymediapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.compose.secondaryDark
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlarmManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.mymediapp.factory.ReminderViewModelFactory

@Composable
fun homeScreen(navController: NavController) {
    // Obtain the Context and AlarmManager
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Initialize Firebase instances
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Create ReminderViewModelFactory
    val reminderViewModelFactory = ReminderViewModelFactory(context, auth, db, alarmManager)

    // Instantiate ReminderViewModel using the factory
    val viewModel: ReminderViewModel = viewModel(factory = reminderViewModelFactory)

    // Fetching reminders list from viewModel
    val reminders by viewModel.reminders.observeAsState(emptyList())
    // Firebase auth and firestore

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
                    userName = "User"
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
                text = "Hello, $userName",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 0.dp)
            )
            Divider(thickness = 1.dp, modifier = Modifier.padding(bottom = 20.dp).width(200.dp), color = secondaryDark)
            Text(
                text = "Upcoming medicines",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Content Section
        if (reminders.isEmpty()) {
            item {
                Text("No upcoming medicines..")
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
            Divider(thickness = 2.dp, color = secondaryDark)
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(top = 20.dp, bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){

            Text(
                text = "Next dose:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                    text = "${reminder.startTime.hours}:${reminder.startTime.minutes}",
                    style = MaterialTheme.typography.bodyMedium
                )
                VerticalDivider(thickness = 2.dp, color = secondaryDark)
            Text(
                    text = "Dose number:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold

                )
                Text(
                    text = "${reminder.numberOfDoses}",
                    style = MaterialTheme.typography.bodyMedium

                )
            }
            Text(
                text = "Notes:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${reminder.notes}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}