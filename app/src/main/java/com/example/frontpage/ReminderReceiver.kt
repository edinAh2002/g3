package com.example.frontpage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.edit

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", -1)
        val medicineName = intent.getStringExtra("medicineName")
        val dosage = intent.getStringExtra("dosage")

        val prefs = context.getSharedPreferences("reminders", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("done_$id", true) }

        Toast.makeText(
            context,
            "It's time to take your $medicineName ($dosage)",
            Toast.LENGTH_LONG
        ).show()
    }
}