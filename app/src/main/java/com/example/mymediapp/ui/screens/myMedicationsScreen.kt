package com.example.mymediapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.model.Time
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun myMedicationsScreen(navController: NavController, viewModel: ReminderViewModel = viewModel()) {
    // Observe the list of reminders from the ViewModel
    val reminders = viewModel.reminders.observeAsState(listOf())
    val medicines = viewModel.medicineResults.observeAsState(initial = emptyList()).value
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            //.verticalScroll(rememberScrollState())
    ) {
        Text(text = "My Medications", modifier = Modifier.padding(bottom = 8.dp))

        // Display the list of reminders using LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(reminders.value) { reminder ->
                ReminderItem(reminder = reminder)
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = "Medicine: ${reminder.medicineName}")
        Text(text = "Start Date: ${reminder.startDate}")
        Text(text = "Time: ${reminder.startTime.hours}:${reminder.startTime.minutes}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}