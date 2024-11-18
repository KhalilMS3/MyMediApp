package com.example.mymediapp.model

import java.util.Date
import java.util.UUID


data class Reminder(

    val medicineName: String = "",
    val numberOfDoses: Int = 0,        // Number of doses
    val timeBetweenDosesString: String = "0h0m",    // Time between doses in hours
    val startDate: Date = Date(),      // Start date for reminders
    val endDate: Date = Date(),        // End date for the reminder period
    val startTime: Time = Time(),      // Start time of the day for the first dose
    val notes: String? = null ,         // Optional notes for the reminder
    //val id: String = "${medicineName}_${System.currentTimeMillis()}",

    val id: String = UUID.randomUUID().toString()

    )

data class Time(
    val hours: Int = 0,
    val minutes: Int = 0
)