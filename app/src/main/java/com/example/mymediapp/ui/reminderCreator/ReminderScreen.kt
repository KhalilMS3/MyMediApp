package com.example.mymediapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.compose.errorLight
import com.example.compose.primaryLight
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavController) {
    val viewModel: ReminderViewModel = viewModel()
    var searchText by remember { mutableStateOf("") }
    var numberOfDoses by remember { mutableStateOf("") }
    val medicines by viewModel.medicineResults.observeAsState(initial = emptyList())
    var isListVisible by remember { mutableStateOf(false) }
    var isMedicineSelected by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf("") }
    var timeBetweenDoses by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create new Reminder") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)) {

                // Search Field
                Text("Medicine Name", modifier = Modifier.padding(vertical = 8.dp))
                TextField(
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
                    modifier = Modifier
                        .fillMaxWidth()
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
                        Text("Number of doses", fontSize = 16.sp)
                        TextField(
                            value = numberOfDoses,
                            onValueChange = { numberOfDoses = it },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Time between doses", fontSize = 16.sp)
                        TextField(
                            value = timeBetweenDoses,
                            onValueChange = { input ->
                                // Vi begrenser input til maks 8 tegn (to tall for timer, to tall for minutter)
                                if (input.length <= 8) {
                                    // Tillater kun tall, og holder "h" og "m" uendret
                                    val sanitizedInput = input.replace(Regex("[^0-9]"), "")
                                    val hours = sanitizedInput.take(2).padStart(2, '0') // Første 2 tegn er timer
                                    val minutes = sanitizedInput.drop(2).take(2).padStart(2, '0') // Neste 2 tegn er minutter

                                    timeBetweenDoses = "${hours}h.${minutes}m"
                                }
                            },
                            label = { Text("__h.__m") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        )
                    }

                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start date", fontSize = 16.sp)
                        TextField(
                            value = startDate,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            readOnly = true, // Bare for å åpne DatePicker ved klikk
                            leadingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = "Start Date", modifier = Modifier.clickable { startDatePicker.show() })
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Slutt date", fontSize = 16.sp)
                        TextField(
                            value = endDate,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            readOnly = true, // Bare for å åpne DatePicker ved klikk
                            leadingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = "End Date", modifier = Modifier.clickable { endDatePicker.show() })
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Notes", fontSize = 16.sp)
                TextField(
                    value = notes,
                    onValueChange = { notes= it },
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = { /* TODO: Legg til funksjonalitet */ },
                        modifier = Modifier.weight(1f).padding(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryLight)
                    ) {
                        Text("Create", color = Color.White)
                    }
                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.weight(1f).padding(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = errorLight)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        }
    )
}
