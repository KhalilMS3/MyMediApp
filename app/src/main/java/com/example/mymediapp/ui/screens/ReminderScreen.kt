package com.example.mymediapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.ChatBubble
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Medication
import androidx.compose.material.icons.twotone.Numbers
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.material.icons.twotone.WatchLater
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.compose.errorContainerLightMediumContrast
import com.example.compose.onPrimaryContainerDark
import com.example.compose.onPrimaryContainerLight
import com.example.compose.primaryLight
import com.example.compose.secondaryLightMediumContrast
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.model.Time
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavController, viewModel: ReminderViewModel = viewModel()) {
    var searchText by remember { mutableStateOf("") }
    var numberOfDoses by remember { mutableStateOf("") }
    val medicines by viewModel.medicineResults.observeAsState(initial = emptyList())
    var isListVisible by remember { mutableStateOf(false) }
    var isMedicineSelected by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf("") }
    var timeBetweenDoses by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Function to parse time from String to Time object
    fun parseStartTime(timeString: String): Time {
        val parts = timeString.split(":")
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return Time(hours, minutes)
    }

    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    // DatePickerDialog for Start Date
    val startDatePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            startDate = "$dayOfMonth-${month + 1}-$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // DatePickerDialog for End Date
    val endDatePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            endDate = "$dayOfMonth-${month + 1}-$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // TimePickerDialog for Start Time
    val startTimePicker = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            startTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create new Reminder") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.TwoTone.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)) {

                // Search Field
                Text("Medicine Name", modifier = Modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        isMedicineSelected = false
                        isListVisible = it.isNotEmpty() // Show list when typing
                        if (it.isNotEmpty()) {
                            viewModel.searchMedicines(it, "cdfbbf2f46d746d18712f4e04d82816a")
                        }
                    },
                    label = { Text("Search for medicine") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.TwoTone.Medication, contentDescription = "Medication")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Medicine search results
                if (isListVisible && medicines.isNotEmpty()) {
                    LazyColumn {
                        items(medicines) { medicine ->
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMedicine = medicine.navnFormStyrke ?: ""
                                    searchText = selectedMedicine
                                    isMedicineSelected = true
                                    isListVisible = false
                                }
                                    .padding(8.dp)) {
                                Text(
                                    text = medicine.navnFormStyrke ?: "",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                HorizontalDivider(thickness = 1.dp, color = primaryLight)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Doses per day", fontSize = 16.sp)
                        OutlinedTextField(
                            value = numberOfDoses,
                            onValueChange = { numberOfDoses = it },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            leadingIcon = {
                                Icon(Icons.TwoTone.Numbers, contentDescription = "Number of doses")
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Time between doses", fontSize = 16.sp)
                        OutlinedTextField(
                            value = timeBetweenDoses,
                            onValueChange = { input ->
                                // Limit input to max 8 characters (two for hours, two for minutes)
                                if (input.length <= 8) {
                                    // Allow only numbers, and leave "h" and "m" unchanged
                                    val sanitizedInput = input.replace(Regex("[^0-9]"), "")
                                    val hours = sanitizedInput.take(2).padStart(2, '0') // First 2 characters are hours
                                    val minutes = sanitizedInput.drop(2).take(2).padStart(2, '0') // Next 2 characters are minutes

                                    timeBetweenDoses = "${hours}h.${minutes}m"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            leadingIcon = {
                                Icon(Icons.TwoTone.Timer, contentDescription = "Time between doses")}
                            )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start date", fontSize = 16.sp)
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            readOnly = true, // Open DatePicker on click
                            leadingIcon = {
                                Icon(Icons.TwoTone.DateRange, contentDescription = "Start Date", modifier = Modifier.clickable { startDatePicker.show() })
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End date", fontSize = 16.sp)
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            readOnly = true, // Open DatePicker on click
                            leadingIcon = {
                                Icon(Icons.TwoTone.DateRange, contentDescription = "End Date", modifier = Modifier.clickable { endDatePicker.show() })
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Start Time Field
                Text("Start Time", fontSize = 16.sp)
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    readOnly = true, // Open TimePicker on click
                    leadingIcon = {
                        Icon(Icons.TwoTone.WatchLater, contentDescription = "Start Time", modifier = Modifier.clickable { startTimePicker.show() })
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Notes", fontSize = 16.sp)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
                    leadingIcon = {
                        Icon(Icons.TwoTone.ChatBubble, contentDescription = "Notes")
                    }
                    )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = {
                            // Validate user input
                            if (selectedMedicine.isEmpty()) {
                                Toast.makeText(context, "Vennligst velg en medisin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (numberOfDoses.isEmpty()) {
                                Toast.makeText(context, "Vennligst oppgi antall doser", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (timeBetweenDoses.isEmpty()) {
                                Toast.makeText(context, "Vennligst oppgi tid mellom doser", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (startDate.isEmpty()) {
                                Toast.makeText(context, "Vennligst velg startdato", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (endDate.isEmpty()) {
                                Toast.makeText(context, "Vennligst velg sluttdato", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Convert timeBetweenDoses to Int
                            val timeBetweenDosesInt = timeBetweenDoses.toIntOrNull() ?: 0 // Default to 0 if invalid input
                            // Convert String dates to Date objects
                            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val startDateParsed: Date = dateFormat.parse(startDate) ?: Date()
                            val endDateParsed: Date = dateFormat.parse(endDate) ?: Date()
                            // Convert String startTime to Time object
                            val startTimeParsed: Time = parseStartTime(startTime)
                            val doses = numberOfDoses.toIntOrNull() ?: 0
                            val (hoursBetween, minutesBetween) = viewModel.parseTimeBetweenDoses(timeBetweenDoses)

                            val reminder = Reminder(
                                medicineName = selectedMedicine,
                                numberOfDoses = doses,
                                timeBetweenDosesString = timeBetweenDoses,
                                startDate = startDateParsed,
                                endDate = endDateParsed,
                                startTime = startTimeParsed,
                                notes = notes
                            )
                            viewModel.addReminder(reminder)
                            Toast.makeText(context, "Reminder created", Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                        },// Go back to the my medication screen

                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(15.dp),
                        shape = RoundedCornerShape(5.dp),
                        //colors = ButtonDefaults.buttonColors(containerColor = secondaryLightMediumContrast),

                    ) {
                        Text("Add Reminder")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(15.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = errorContainerLightMediumContrast),
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        }
    )
}
