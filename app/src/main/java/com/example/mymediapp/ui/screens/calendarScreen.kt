package com.example.mymediapp.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mymediapp.model.MyCalendar
import com.example.mymediapp.ui.screens.deit.DietViewModel

@Composable
fun CalendarScreen(navController: NavController) {
    val viewModel: DietViewModel = viewModel()
    val mealItems by viewModel.mealItems.observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // MyCalendar composable to show the calendar
        MyCalendar().CalendarView(mealItems = mealItems)

        Spacer(modifier = Modifier.height(16.dp))


    }
}