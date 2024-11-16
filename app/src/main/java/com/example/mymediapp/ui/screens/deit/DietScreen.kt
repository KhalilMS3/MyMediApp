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
import com.example.mymediapp.model.MealItem
import com.example.mymediapp.model.MyCalendar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@Composable
fun DietScreen(navController: NavController) {
    val viewModel: DietViewModel = viewModel()
    val mealItems by viewModel.mealItems.observeAsState(emptyList())

    var newMeal by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showCalendar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "My Diet",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Meal name")
        OutlinedTextField(
            value = newMeal,
            onValueChange = { newMeal = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Calories (kcal)")
        OutlinedTextField(
            value = newCalories,
            onValueChange = { newCalories = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "Date")
                // Date Picker
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
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(text = "Time")
                // Time Picker
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

        Button(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(15.dp),
            shape = RoundedCornerShape(5.dp),
            onClick = {
                if (newMeal.isNotBlank() && newCalories.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                    val newMealItem = MealItem(
                        id = UUID.randomUUID().toString(),
                        meal = newMeal,
                        calories = newCalories.toIntOrNull() ?: 0,
                        date = selectedDate,
                        time = selectedTime
                    )
                    // Add the meal item to Firebase
                    viewModel.addMealItem(newMealItem)
                    // Reset the input fields
                    newMeal = ""
                    newCalories = ""
                    selectedDate = ""
                    selectedTime = ""
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

        if (showCalendar) {
            MyCalendar().CalendarView(mealItems = mealItems)
        } else {
            LazyColumn {
                items(mealItems) { meal ->
                    MealItemView(meal, onDelete = { mealId ->
                        viewModel.deleteMealItem(mealId)
                    })
                }
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

