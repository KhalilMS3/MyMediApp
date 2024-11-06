package com.example.mymediapp.ui.reminderCreator

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymediapp.broadcast.ReminderBroadcastReceiver
import com.example.mymediapp.model.MedicineResponse
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.network.MedicineApiService
import com.example.mymediapp.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class ReminderViewModel : ViewModel() {

    // Initialize FirebaseAuth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // LiveData for medicine search results
    private val _medicineResults = MutableLiveData<List<MedicineResponse>>()
    val medicineResults: LiveData<List<MedicineResponse>> = _medicineResults


    // LiveData for list of saved reminders
    private val _reminders = MutableLiveData<List<Reminder>>(emptyList())
    val reminders: LiveData<List<Reminder>> get() = _reminders
    private val _medications = MutableLiveData<List<Reminder>>(emptyList())
    val medications: LiveData<List<Reminder>> = _medications
    //private val context = getApplication<Application>().applicationContext                              //10


    fun parseTimeBetweenDoses(timeString: String): Pair<Int, Int> {
        val parts = timeString.split("h", "m")
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return Pair(hours, minutes)
    }

    private val apiService: MedicineApiService = RetrofitInstance.createService(MedicineApiService::class.java)

    // Function to search for medicines
    fun searchMedicines(queryText: String, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.searchMedicines(queryText, apiKey)
                if (response.isSuccessful) {
                    _medicineResults.postValue(response.body() ?: emptyList())
                } else {
                    _medicineResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                _medicineResults.postValue(emptyList())
            }
        }
    }

    // Function to add a reminder
    fun addReminder(reminder: Reminder) {

        val userId = auth.currentUser?.uid
        Log.d("ReminderViewModel", "Attempting to add reminder:$reminder")

        val updatedReminders = _reminders.value?.toMutableList() ?: mutableListOf()
        updatedReminders.add(reminder)
        _reminders.value = updatedReminders

        // Log the reminder being added
        Log.d("ReminderViewModel", "New reminder added: $reminder")

        // Log the current list of reminders
        Log.d("ReminderViewModel", "Current list of reminders: $updatedReminders")
        // Save to Firestore
        saveReminderToFirestore(reminder)
    }

    fun addMedication(reminder: Reminder) {
        val updatedList = _medications.value?.toMutableList() ?: mutableListOf()
        updatedList.add(reminder)
        _medications.value = updatedList
    }

    private fun saveReminderToFirestore(reminder: Reminder) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val reminderId = UUID.randomUUID().toString() // Generate a unique ID for the reminder

            db.collection("users")
                .document(userId)
                .collection("reminders")
                .document(reminderId)
                .set(reminder)
                .addOnSuccessListener {
                    Log.d("Firestore", "Reminder saved successfully.")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error saving reminder", e)
                }
        } else {
            Log.w("Firestore", "User not authenticated. Cannot save reminder.")
            // Optionally, handle unauthenticated state (e.g., prompt login)
        }
    }

    fun updateReminder(reminderId: String, updatedReminder: Reminder) {
        // Update the local list
        val updatedReminders = _reminders.value?.map {
            if (it.id == reminderId) updatedReminder else it
        } ?: listOf(updatedReminder)
        _reminders.value = updatedReminders

        // Update in Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("reminders")
                .document(reminderId)
                .set(updatedReminder)
                .addOnSuccessListener {
                    Log.d("Firestore", "Reminder updated successfully.")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error updating reminder", e)
                }
        }
    }

    fun fetchReminders() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("reminders")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        val reminderList = snapshots.documents.mapNotNull { doc ->
                            doc.toObject(Reminder::class.java)
                        }
                        _reminders.postValue(reminderList)
                    }
                }
        } else {
            Log.w("Firestore", "User not authenticated. Cannot fetch reminders.")
        }
    }
    // Initialize Firestore settings if needed
    init {
        fetchReminders()
    }

    fun deleteReminder(reminderId: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Remove from local list
            val updatedReminders = _reminders.value?.filter { it.id != reminderId }
            _reminders.value = updatedReminders!!

            // Remove from Firestore
            db.collection("users")
                .document(userId)
                .collection("reminders")
                .document(reminderId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "Reminder deleted successfully.")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error deleting reminder", e)
                }
        } else {
            Log.w("Firestore", "User not authenticated. Cannot delete reminder.")
            // Optionally, handle unauthenticated state
        }
    }


    // Function to schedule notifications based on the reminder
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleReminders(reminder: Reminder, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Calculate the first dose time based on the start date and time
        val startCalendar = Calendar.getInstance().apply {
            timeInMillis = reminder.startDate.time
            set(Calendar.HOUR_OF_DAY, reminder.startTime.hours)
            set(Calendar.MINUTE, reminder.startTime.minutes)
        }

        // Schedule an alarm for each dose
        repeat(reminder.numberOfDoses) { doseNumber ->
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("medicineName", reminder.medicineName)
                putExtra("doseNumber", doseNumber + 1)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                doseNumber, // Unique requestCode for each dose
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Set the alarm for the current dose
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                startCalendar.timeInMillis,
                pendingIntent
            )

            // Update the time for the next dose (add time between doses)
            startCalendar.add(Calendar.HOUR_OF_DAY, reminder.timeBetweenDoses)
        }
    }
}
