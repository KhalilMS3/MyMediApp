// MyCalendar.kt
package com.example.mymediapp.model

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBackIosNew
import androidx.compose.material.icons.twotone.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

class MyCalendar {

    @Composable
    fun CalendarView(mealItems: List<MealItem>) {
        var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
        var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.YEAR, currentYear)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-based index for day of week

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {

            // Month and Year Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Month Button
                IconButton(onClick = {
                    if (currentMonth == Calendar.JANUARY) {
                        currentMonth = Calendar.DECEMBER
                        currentYear -= 1
                    } else {
                        currentMonth -= 1
                    }
                }) {
                    Icon(Icons.TwoTone.ArrowBackIosNew, contentDescription = "Previous Month")
                }

                // Current Month and Year Display
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
                    style = MaterialTheme.typography.headlineSmall
                )

                // Next Month Button
                IconButton(onClick = {
                    if (currentMonth == Calendar.DECEMBER) {
                        currentMonth = Calendar.JANUARY
                        currentYear += 1
                    } else {
                        currentMonth += 1
                    }
                }) {
                    Icon(Icons.TwoTone.ArrowForwardIos, contentDescription = "Next Month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday Labels
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(text = it, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                content = {
                    // Empty cells before the first day
                    for (i in 0 until firstDayOfWeek) {
                        item {
                            Spacer(modifier = Modifier
                                .size(50.dp)
                                .padding(2.dp))
                        }
                    }

                    // Days of the month
                    for (day in 1..daysInMonth) {
                        val currentDate = String.format("%04d-%02d-%02d", currentYear, currentMonth + 1, day)
                        val mealsOnThisDay = mealItems.filter { it.date == currentDate }
                        val totalCalories = mealsOnThisDay.sumOf { it.calories }

                        item {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(2.dp)
                                    .background(
                                        color = if (totalCalories > 0) Color(0xFFE0F7FA) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = day.toString())
                                    if (totalCalories > 0) {
                                        Text(
                                            text = "$totalCalories cal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF00796B) // Teal color for calories
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
