package com.example.mymediapp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForwardIos
import androidx.compose.material.icons.twotone.ArrowBackIosNew
import androidx.compose.material.icons.twotone.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

class MyCalendar {

    @Composable
    fun CalendarView(mealItems: List<MealItem>) {
        var currentMonth by remember { mutableStateOf(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)) }
        var currentYear by remember { mutableStateOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)) }

        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.MONTH, currentMonth)
        calendar.set(java.util.Calendar.YEAR, currentYear)
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1 // 0-based index for day of week

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (currentMonth == 0) {
                        currentMonth = 11
                        currentYear -= 1
                    } else {
                        currentMonth -= 1
                    }
                }) {
                    Icon(Icons.TwoTone.ArrowBackIosNew, contentDescription = "Arrow back")
                }
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
                    style = MaterialTheme.typography.headlineSmall
                )
                Button(onClick = {
                    if (currentMonth == 11) {
                        currentMonth = 0
                        currentYear += 1
                    } else {
                        currentMonth += 1
                    }
                }) {
                    Icon(Icons.AutoMirrored.TwoTone.ArrowForwardIos, contentDescription = "Arrow back")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday Labels
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(text = it, style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Days Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                content = {
                    // Empty cells before the first day
                    for (i in 0 until firstDayOfWeek) {
                        item { Spacer(modifier = Modifier.size(40.dp)) }
                    }

                    // Days of the month
                    for (day in 1..daysInMonth) {
                        val currentDate = String.format("%04d-%02d-%02d", currentYear, currentMonth + 1, day)
                        val mealsOnThisDay = mealItems.filter { it.date == currentDate }

                        item {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(4.dp)
                                    .border(1.dp, Color.Gray)
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = day.toString())
                                    if (mealsOnThisDay.isNotEmpty()) {
                                        Text(text = "${mealsOnThisDay.size} meal(s)", style = MaterialTheme.typography.bodySmall)
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
