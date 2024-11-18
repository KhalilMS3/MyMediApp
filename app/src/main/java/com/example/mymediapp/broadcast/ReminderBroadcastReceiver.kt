package com.example.mymediapp.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mymediapp.R
import android.util.Log
import kotlin.math.absoluteValue


class ReminderBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminderId")
        val medicineName = intent.getStringExtra("medicineName")
        val doseNumber = intent.getIntExtra("doseNumber", 0)
        Log.d("ReminderBroadcastReceiver", "Received alarm for $medicineName, dose $doseNumber")

        val notificationId = "$reminderId-$doseNumber".hashCode().absoluteValue

        val notification = NotificationCompat.Builder(context, "medication_reminder_channel")
            .setSmallIcon(R.drawable.mymedi_full_png_green)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take dose $doseNumber of $medicineName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notification)
        Log.d("ReminderBroadcastReceiver", "Notification displayed for dose $doseNumber with ID $notificationId")
    }
}