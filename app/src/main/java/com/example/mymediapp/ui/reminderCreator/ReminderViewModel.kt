package com.example.mymediapp.ui.reminderCreator

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
import kotlin.math.absoluteValue



import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class ReminderViewModel(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val alarmManager: AlarmManager,

) : ViewModel() {


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
        val hoursPattern = "(\\d+)h".toRegex()
        val minutesPattern = "(\\d+)m".toRegex()

        val hoursMatch = hoursPattern.find(timeString)
        val minutesMatch = minutesPattern.find(timeString)

        val hours = hoursMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val minutes = minutesMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0

        if (hours == 0 && minutes == 0) {
            throw IllegalArgumentException("Invalid timeBetweenDosesString format: $timeString")
        }

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
        if (userId != null) {
            val updatedReminders = _reminders.value?.toMutableList() ?: mutableListOf()
            updatedReminders.add(reminder)
            _reminders.value = updatedReminders

            saveReminderToFirestore(reminder)

            // Schedule alarms for the new reminder
            scheduleReminders(reminder)

            Log.d("ReminderViewModel", "Reminder added and alarms scheduled: $reminder")
        } else {
            Log.w("Firestore", "User not authenticated. Cannot add reminder.")
        }
    }

    fun addMedication(reminder: Reminder) {
        val updatedList = _medications.value?.toMutableList() ?: mutableListOf()
        updatedList.add(reminder)
        _medications.value = updatedList
    }

    fun saveReminderToFirestore(reminder: Reminder) {
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

                     // Schedule alarms for each reminder
                     reminderList.forEach { reminder ->
                         scheduleReminders(reminder)
                     }

                     Log.d("ReminderViewModel", "Reminders fetched and alarms scheduled.")
                 }
             }
     } else {
         Log.w("Firestore", "User not authenticated. Cannot fetch reminders.")
     }
 }

    // Initialize Firestore
    init {
        fetchReminders()
    }



    fun calculateTotalDoses(reminder: Reminder): Int {
        val startDateTime = Calendar.getInstance().apply {
            time = reminder.startDate
            set(Calendar.HOUR_OF_DAY, reminder.startTime.hours)
            set(Calendar.MINUTE, reminder.startTime.minutes)
        }

        val endDateTime = Calendar.getInstance().apply {
            time = reminder.endDate
        }

        // Parse hours and minutes between doses
        val (hoursBetween, minutesBetween) = parseTimeBetweenDoses(reminder.timeBetweenDosesString)

        // Calculate time between doses in milliseconds
        val timeBetweenDosesInMillis = (hoursBetween * 3600000L) + (minutesBetween * 60000L)

        if (timeBetweenDosesInMillis == 0L) {
            // Avoid division by zero
            return 0
        }

        val durationInMillis = endDateTime.timeInMillis - startDateTime.timeInMillis

        return (durationInMillis / timeBetweenDosesInMillis).toInt()
    }
    private val scheduledReminderIds = mutableSetOf<String>()

    fun scheduleReminders(reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startCalendar = Calendar.getInstance().apply {
            time = reminder.startDate
            set(Calendar.HOUR_OF_DAY, reminder.startTime.hours)
            set(Calendar.MINUTE, reminder.startTime.minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val (hoursBetween, minutesBetween) = parseTimeBetweenDoses(reminder.timeBetweenDosesString)
        val totalDoses = calculateTotalDoses(reminder)

        for (doseNumber in 0 until totalDoses) {
            val alarmTimeInMillis = startCalendar.timeInMillis

            if (alarmTimeInMillis > System.currentTimeMillis()) {
                val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                    putExtra("reminderId", reminder.id)
                    putExtra("medicineName", reminder.medicineName)
                    putExtra("doseNumber", doseNumber + 1)
                }

                val requestCode = (reminder.id.hashCode() + doseNumber).absoluteValue

                // Check if the alarm already exists
                val existingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )

                if (existingIntent != null) {
                    Log.d("ReminderViewModel", "Alarm already scheduled for dose ${doseNumber + 1}")
                    // Skip scheduling this alarm
                } else {
                    // Proceed to schedule the alarm
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTimeInMillis,
                        pendingIntent
                    )

                    Log.d("ReminderViewModel", "Alarm set for dose ${doseNumber + 1} at ${Date(alarmTimeInMillis)}")
                }
            } else {
                Log.d("ReminderViewModel", "Skipping alarm for dose ${doseNumber + 1} as it's in the past")
            }

            // Update the calendar for the next dose
            startCalendar.add(Calendar.HOUR_OF_DAY, hoursBetween)
            startCalendar.add(Calendar.MINUTE, minutesBetween)
        }
    }







    fun cancelReminderAlarms(reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val totalDoses = calculateTotalDoses(reminder)
        val (hoursBetween, minutesBetween) = parseTimeBetweenDoses(reminder.timeBetweenDosesString)

        val startCalendar = Calendar.getInstance().apply {
            time = reminder.startDate
            set(Calendar.HOUR_OF_DAY, reminder.startTime.hours)
            set(Calendar.MINUTE, reminder.startTime.minutes)
        }

        for (doseNumber in 0 until totalDoses) {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("medicineName", reminder.medicineName)
                putExtra("doseNumber", doseNumber + 1)
            }

            val requestCode = (reminder.id.hashCode() + doseNumber).absoluteValue

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

            // Update time for next dose
            startCalendar.add(Calendar.HOUR_OF_DAY, hoursBetween)
            startCalendar.add(Calendar.MINUTE, minutesBetween)
        }
    }
    fun deleteReminder(reminderId: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val reminder = _reminders.value?.find { it.id == reminderId }
            if (reminder != null) {
                cancelReminderAlarms(reminder)
            }

            // Remove from local list
            val updatedReminders = _reminders.value?.filter { it.id != reminderId }
            _reminders.value = updatedReminders ?: emptyList()

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
        }
    }


}
