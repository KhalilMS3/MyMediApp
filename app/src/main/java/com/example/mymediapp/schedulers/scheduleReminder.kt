package com.example.mymediapp.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mymediapp.broadcast.ReminderBroadcastReceiver

// Funksjon for å planlegge en påminnelse ved hjelp av AlarmManager
fun scheduleReminder(context: Context, reminderTime: Long, medicineId: String) {
    // Opprett en Intent for ReminderBroadcastReceiver
    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
        putExtra("medicineId", medicineId) // Send medisin-ID som ekstra data
    }

    // Opprett en PendingIntent for å aktivere Intent
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        medicineId.hashCode(), // Unik ID for påminnelsen
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Få en instans av AlarmManager
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Sett opp alarmen for å trigge ved det angitte tidspunktet
    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        reminderTime, // Tidspunktet for påminnelsen
        pendingIntent // PendingIntent som skal aktiveres
    )
}
