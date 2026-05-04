package com.example.frontpage.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class WorkoutEntry(
    val name: String,
    val durationMinutes: Int
)

@Composable
fun WorkoutScreen() {
    val workouts = remember { mutableStateListOf<WorkoutEntry>() }

    var showDialog by remember { mutableStateOf(false) }
    var workoutName by remember { mutableStateOf("") }
    var workoutDuration by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Workout Logging",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (workouts.isEmpty()) {
            Text(
                text = "No workouts logged yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(workouts) { workout ->
                    WorkoutListItem(workout)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log workout")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Log workout")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = { workoutName = it },
                        label = { Text("Workout name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = workoutDuration,
                        onValueChange = { workoutDuration = it },
                        label = { Text("Duration in minutes") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val duration = workoutDuration.toIntOrNull()

                        if (workoutName.isNotBlank() && duration != null) {
                            workouts.add(
                                WorkoutEntry(
                                    name = workoutName,
                                    durationMinutes = duration
                                )
                            )

                            workoutName = ""
                            workoutDuration = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WorkoutListItem(workout: WorkoutEntry) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "${workout.durationMinutes} min",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}