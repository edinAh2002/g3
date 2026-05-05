package com.example.frontpage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun scheduleReminder(
    context: Context,
    medicineName: String,
    dosage: String,
    triggerTime: Long,
    requestCode: Int
) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("id", requestCode)
        putExtra("medicineName", medicineName)
        putExtra("dosage", dosage)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.set(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}