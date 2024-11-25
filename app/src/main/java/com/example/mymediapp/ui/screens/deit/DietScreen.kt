package com.example.mymediapp.ui.screens.deit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mymediapp.factory.DietViewModelFactory
import com.example.mymediapp.model.MealItem
import com.example.mymediapp.model.MyCalendar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@Composable
fun DietScreen(navController: NavController, factory: DietViewModelFactory) {
    val viewModel: DietViewModel = viewModel(factory = factory)
    val mealItems by viewModel.mealItems.observeAsState(emptyList())

    // State variables for meal input
    var newMeal by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showCalendar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }  // To display error message
    var showErrorDialog by remember { mutableStateOf(false) } // To control the dialog visibility

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "My Diet",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Meal name input
            Text(text = "Meal name")
            OutlinedTextField(
                value = newMeal,
                onValueChange = { newMeal = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Calories input
            Text(text = "Calories (kcal)")
            OutlinedTextField(
                value = newCalories,
                onValueChange = { newCalories = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date and Time picker
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = "Date") // Date Picker
                    OutlinedButton(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(end = 10.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(year, month, dayOfMonth)
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    selectedDate = dateFormat.format(calendar.time)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    ) {
                        Text(if (selectedDate.isEmpty()) "Pick Date" else selectedDate)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Time")
                    OutlinedButton(
                        modifier = Modifier.width(200.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                                    calendar.set(Calendar.MINUTE, minute)
                                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    selectedTime = timeFormat.format(calendar.time)
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        }
                    ) {
                        Text(if (selectedTime.isEmpty()) "Pick Time" else selectedTime)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Meal button
            Button(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(15.dp),
                shape = RoundedCornerShape(5.dp),
                onClick = {
                    // Validate inputs
                    if (newMeal.isNotBlank() && newCalories.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                        val calories = newCalories.toIntOrNull()
                        if (calories != null) {
                            val newMealItem = MealItem(
                                id = UUID.randomUUID().toString(),
                                meal = newMeal,
                                calories = calories,
                                date = selectedDate,
                                time = selectedTime
                            )
                            viewModel.addMealItem(newMealItem)
                            // Reset input fields after success
                            newMeal = ""
                            newCalories = ""
                            selectedDate = ""
                            selectedTime = ""
                            errorMessage = ""  // Clear any previous error message
                        } else {
                            errorMessage = "Calories should be a valid number!"
                            showErrorDialog = true // Show dialog if calories are invalid
                        }
                    } else {
                        errorMessage = "Please fill all fields!"
                        showErrorDialog = true // Show dialog if any field is empty
                    }
                }
            ) {
                Text("Add Meal")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to navigate to Calendar Screen
            Button(
                contentPadding = PaddingValues(15.dp),
                shape = RoundedCornerShape(5.dp),
                onClick = {
                    navController.navigate("calendar")
                }
            ) {
                Text("View Calendar")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display meals or calendar
        if (showCalendar) {
            item {
                MyCalendar().CalendarView(mealItems = mealItems)
            }
        } else {
            items(mealItems) { meal ->
                MealItemView(meal, onDelete = { mealId ->
                    viewModel.deleteMealItem(mealId)
                })
            }
        }

        // Error dialog
        if (showErrorDialog) {
            item {
                ErrorDialog(
                    message = errorMessage,
                    onDismiss = { showErrorDialog = false }
                )
            }
        }
    }
}
@Composable
fun MealItemView(meal: MealItem, onDelete: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${meal.meal} - ${meal.calories} cal on ${meal.date} at ${meal.time}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onDelete(meal.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Meal")
        }
    }
}
@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Error") },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

