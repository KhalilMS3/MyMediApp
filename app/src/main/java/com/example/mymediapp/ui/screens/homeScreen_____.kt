package com.example.mymediapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel


@Composable
fun homeScreen(navController: NavController,  viewModel: ReminderViewModel = viewModel()) {
    // Hent listen over påminnelser fra ViewModel
    val reminders by viewModel.reminders.observeAsState(emptyList())

    // Layout for å vise medisinene
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Kommende medisiner",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (reminders.isEmpty()) {
            Text("Ingen kommende medisiner")
        } else {
            LazyColumn {
                items(reminders) { reminder ->
                    MedicineItem(reminder)
                }
            }
        }
    }
}

@Composable
fun MedicineItem(reminder: Reminder) {
    // Enkel visning for hver medisin på listen
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = reminder.medicineName, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Neste dose: ${reminder.startTime.hours}:${reminder.startTime.minutes}")
        }
    }
}
