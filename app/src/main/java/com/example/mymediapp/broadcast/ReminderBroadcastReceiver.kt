package com.example.mymediapp.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val medicineName = intent?.getStringExtra("medicineName")
        val doseNumber = intent?.getIntExtra("doseNumber", 0)

        // Her kan du lage en notifikasjon som varsler brukeren
        val notification = NotificationCompat.Builder(context!!, "medication_reminder_channel")
            .setContentTitle("Medisinpåminnelse")
            .setContentText("Tid for å ta dose nr. $doseNumber av $medicineName")
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(doseNumber!!, notification)
    }
}
