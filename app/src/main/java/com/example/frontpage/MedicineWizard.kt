package com.example.frontpage

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

data class MedicineReminder(
    val id: Int,
    val medicineName: String,
    val dosage: String,
    val date: String,
    val time: String
)

@Composable
fun MedicineWizard(
    onClose: () -> Unit,
    onReminderCreated: (MedicineReminder) -> Unit
) {
    val context = LocalContext.current

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var displayDate by remember { mutableStateOf("None") }
    var displayTime by remember { mutableStateOf("None") }

    val calendar = remember { Calendar.getInstance() }

    AlertDialog(
        onDismissRequest = { onClose() },
        title = {
            Text("New Medicine Reminder")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine name") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage, e.g. 1 tablet") },
                    singleLine = true
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                displayDate = "$day/${month + 1}/$year"
                                calendar.set(Calendar.YEAR, year)
                                calendar.set(Calendar.MONTH, month)
                                calendar.set(Calendar.DAY_OF_MONTH, day)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text("Set Date", fontSize = 18.sp)
                    }

                    Text("Date: $displayDate", fontSize = 18.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                displayTime = String.format("%02d:%02d", hour, minute)
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                calendar.set(Calendar.SECOND, 0)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }) {
                        Text("Set Time", fontSize = 18.sp)
                    }

                    Text("Time: $displayTime", fontSize = 18.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (medicineName.isBlank()) {
                        Toast.makeText(context, "Enter medicine name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (displayDate == "None") {
                        Toast.makeText(context, "Choose a date", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (displayTime == "None") {
                        Toast.makeText(context, "Choose a time", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val triggerTime = calendar.timeInMillis

                    if (triggerTime <= System.currentTimeMillis()) {
                        Toast.makeText(context, "Choose a future time", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val id = System.currentTimeMillis().toInt()

                    scheduleReminder(
                        context = context,
                        medicineName = medicineName,
                        dosage = dosage,
                        triggerTime = triggerTime,
                        requestCode = id
                    )

                    onReminderCreated(
                        MedicineReminder(
                            id = id,
                            medicineName = medicineName,
                            dosage = dosage,
                            date = displayDate,
                            time = displayTime
                        )
                    )

                    Toast.makeText(
                        context,
                        "Reminder set for $medicineName",
                        Toast.LENGTH_SHORT
                    ).show()

                    onClose()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onClose() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReminderListPopup(
    reminders: List<MedicineReminder>,
    onDeleteReminder: (MedicineReminder) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    val prefs = context.getSharedPreferences("reminders", Context.MODE_PRIVATE)

    val activeReminders = reminders.filter {
        !prefs.getBoolean("done_${it.id}", false)
    }

    AlertDialog(
        onDismissRequest = { onClose() },
        title = {
            Text("Upcoming Reminders")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activeReminders.isEmpty()) {
                    Text("No reminders yet")
                } else {
                    activeReminders.forEach { reminder ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE0E0E0)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = reminder.medicineName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp
                                )

                                Text("Dosage: ${reminder.dosage}", fontSize = 18.sp)
                                Text("Date: ${reminder.date}", fontSize = 18.sp)
                                Text("Time: ${reminder.time}", fontSize = 18.sp)

                                Button(
                                    onClick = {
                                        onDeleteReminder(reminder)
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    contentPadding = PaddingValues(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onClose() }) {
                Text("Back")
            }
        }
    )
}