package com.example.mymediapp.model

import java.util.Date

data class Reminder(
    val medicineName: String,
    val numberOfDoses: Int,        // Number of doses
    val timeBetweenDoses: Int,     // Time between doses in hours
    val startDate: Date,           // Start date for reminders
    val endDate: Date,             // End date for the reminder period
    val startTime: Time,           // Start time of the day for the first dose
    val notes: String? = null      // Optional notes for the reminder
)

data class Time(
    val hours: Int,
    val minutes: Int
)
