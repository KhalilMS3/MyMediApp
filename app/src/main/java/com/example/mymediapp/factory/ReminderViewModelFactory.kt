package com.example.mymediapp.factory

import android.app.AlarmManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mymediapp.ui.reminderCreator.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReminderViewModelFactory(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val alarmManager: AlarmManager
) : ViewModelProvider.Factory { // Ensure inheritance
    override fun <T : ViewModel> create(modelClass: Class<T>): T { // Correct type parameter
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(context, auth, db, alarmManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}