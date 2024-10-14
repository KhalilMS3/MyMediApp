package com.example.mymediapp.model
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*


data class MealItem(val id: Int, val meal: String, val calories: Int, val date: String, val time: String)

class Diet {

    @Composable
    fun MealApp() {
        var mealItems by remember { mutableStateOf(listOf<MealItem>()) }
        var nextId by remember { mutableStateOf(0) }
        var newMeal by remember { mutableStateOf("") }
        var newCalories by remember { mutableStateOf("") }
        var selectedDate by remember { mutableStateOf("") }
        var selectedTime by remember { mutableStateOf("") }
        var showCalendar by remember { mutableStateOf(false) } // State to toggle the calendar view

        val context = LocalContext.current
        val calendar = java.util.Calendar.getInstance()

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "My Diet",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = newMeal,
                onValueChange = { newMeal = it },
                label = { Text("Meal Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newCalories,
                onValueChange = { newCalories = it },
                label = { Text("Calories") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date Picker
            Button(onClick = {
                DatePickerDialog(context, { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedDate = dateFormat.format(calendar.time)
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show()
            }) {
                Text(if (selectedDate.isEmpty()) "Pick Date" else "Date: $selectedDate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time Picker
            Button(onClick = {
                TimePickerDialog(context, { _, hour, minute ->
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
                    calendar.set(java.util.Calendar.MINUTE, minute)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    selectedTime = timeFormat.format(calendar.time)
                }, calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true).show()
            }) {
                Text(if (selectedTime.isEmpty()) "Pick Time" else "Time: $selectedTime")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (newMeal.isNotBlank() && newCalories.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                    val newMealItem = MealItem(
                        nextId++,
                        newMeal,
                        newCalories.toInt(),
                        selectedDate,
                        selectedTime
                    )
                    mealItems = mealItems + newMealItem
                    newMeal = ""
                    newCalories = ""
                    selectedDate = ""
                    selectedTime = ""
                }
            }) {
                Text("Add Meal")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to show or hide the calendar
            Button(onClick = {
                showCalendar = !showCalendar
            }) {
                Text(if (showCalendar) "Hide Calendar" else "Show My Calendar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conditionally show the calendar view or list of meal items
            if (showCalendar) {
                MyCalendar().CalendarView(mealItems = mealItems) // Using Calendar class to display calendar view
            } else {
                LazyColumn {
                    items(mealItems) { meal ->
                        MealItemView(meal)
                    }
                }
            }
        }
    }

    @Composable
    fun MealItemView(meal: MealItem) {
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
        }
    }
}
