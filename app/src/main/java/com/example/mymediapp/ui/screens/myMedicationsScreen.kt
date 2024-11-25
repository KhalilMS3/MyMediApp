package com.example.mymediapp.ui.screens


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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.compose.secondaryDark
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import android.app.AlarmManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.mymediapp.factory.ReminderViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun myMedicationsScreen(navController: NavController) {
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

    // Observe the list of reminders from the ViewModel
    val reminders = viewModel.reminders.observeAsState(listOf())
    val medicines = viewModel.medicineResults.observeAsState(initial = emptyList()).value
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Section
        item {
            Text(
                text = "My Medications",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(
                thickness = 2.dp,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 20.dp),
                color = secondaryDark
            )
        }

        // List of Reminders
        items(reminders.value) { reminder ->
            ReminderItem(reminder = reminder)
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder) {
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

            Divider(thickness = 2.dp, color = secondaryDark, modifier = Modifier.padding(top = 10.dp))
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                .padding(top = 10.dp, bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)){

                Text(
                    text = "Start time:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${reminder.startTime.hours}:${reminder.startTime.minutes}",
                    style = MaterialTheme.typography.titleMedium,
                )
                VerticalDivider(thickness = 2.dp, color = secondaryDark)
                Text(
                    text = "Number of doses:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold

                )
                Text(
                    text = "${reminder.numberOfDoses}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = "Notes:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${reminder.notes}",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}