package com.example.frontpage.workout.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.workout.WorkoutViewModel
import com.example.frontpage.workout.domain.decodeExercises
import com.example.frontpage.workout.model.WorkoutEntry

enum class WorkoutScreenMode {
    WorkoutList,
    WorkoutBuilder,
    WorkoutDetails
}

@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val workouts by workoutViewModel.workouts.collectAsState()

    val currentExercises = remember { mutableStateListOf<String>() }

    var screenMode by remember { mutableStateOf(WorkoutScreenMode.WorkoutList) }
    var selectedWorkout by remember { mutableStateOf<WorkoutEntry?>(null) }

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showFinishWorkoutDialog by remember { mutableStateOf(false) }

    var exerciseName by remember { mutableStateOf("") }
    var workoutName by remember { mutableStateOf("") }
    var workoutDuration by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        workoutViewModel.refreshCurrentUser()
    }

    when (screenMode) {
        WorkoutScreenMode.WorkoutList -> {
            WorkoutListScreen(
                modifier = modifier,
                workouts = workouts,
                onLogWorkoutClick = {
                    currentExercises.clear()
                    screenMode = WorkoutScreenMode.WorkoutBuilder
                },
                onWorkoutClick = { workout ->
                    selectedWorkout = workout
                    screenMode = WorkoutScreenMode.WorkoutDetails
                }
            )
        }

        WorkoutScreenMode.WorkoutBuilder -> {
            WorkoutBuilderScreen(
                modifier = modifier,
                exercises = currentExercises,
                onAddExerciseClick = {
                    showAddExerciseDialog = true
                },
                onFinishWorkoutClick = {
                    showFinishWorkoutDialog = true
                },
                onCancelClick = {
                    currentExercises.clear()
                    screenMode = WorkoutScreenMode.WorkoutList
                }
            )
        }

        WorkoutScreenMode.WorkoutDetails -> {
            selectedWorkout?.let { workout ->
                WorkoutDetailsScreen(
                    modifier = modifier,
                    workout = workout,
                    onBackClick = {
                        selectedWorkout = null
                        screenMode = WorkoutScreenMode.WorkoutList
                    },
                    onDeleteClick = {
                        workoutViewModel.deleteWorkout(workout.id)
                        selectedWorkout = null
                        screenMode = WorkoutScreenMode.WorkoutList
                    }
                )
            }
        }
    }

    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = {
                Text("Add exercise")
            },
            text = {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (exerciseName.isNotBlank()) {
                            currentExercises.add(exerciseName.trim())
                            exerciseName = ""
                            showAddExerciseDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        exerciseName = ""
                        showAddExerciseDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showFinishWorkoutDialog) {
        AlertDialog(
            onDismissRequest = { showFinishWorkoutDialog = false },
            title = {
                Text("Finish workout")
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

                        if (
                            workoutName.isNotBlank() &&
                            duration != null &&
                            currentExercises.isNotEmpty()
                        ) {
                            workoutViewModel.addWorkout(
                                name = workoutName.trim(),
                                durationMinutes = duration,
                                exercises = currentExercises.toList()
                            )

                            workoutName = ""
                            workoutDuration = ""
                            currentExercises.clear()
                            showFinishWorkoutDialog = false
                            screenMode = WorkoutScreenMode.WorkoutList
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        workoutName = ""
                        workoutDuration = ""
                        showFinishWorkoutDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WorkoutListScreen(
    modifier: Modifier = Modifier,
    workouts: List<WorkoutEntry>,
    onLogWorkoutClick: () -> Unit,
    onWorkoutClick: (WorkoutEntry) -> Unit
) {
    Column(
        modifier = modifier
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
                items(
                    items = workouts,
                    key = { workout -> workout.id }
                ) { workout ->
                    WorkoutListItem(
                        workout = workout,
                        onClick = { onWorkoutClick(workout) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogWorkoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log workout")
        }
    }
}

@Composable
fun WorkoutBuilderScreen(
    modifier: Modifier = Modifier,
    exercises: List<String>,
    onAddExerciseClick: () -> Unit,
    onFinishWorkoutClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "New workout",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (exercises.isEmpty()) {
            Text(
                text = "No exercises added yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(exercises) { exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = exercise,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddExerciseClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add exercise")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onFinishWorkoutClick,
            enabled = exercises.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish workout")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel workout")
        }
    }
}

@Composable
fun WorkoutDetailsScreen(
    modifier: Modifier = Modifier,
    workout: WorkoutEntry,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val exercises = decodeExercises(workout.exercisesText)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = workout.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${workout.durationMinutes} minutes",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Exercises",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(exercises) { exercise ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = exercise,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to workouts")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete workout")
        }
    }
}

@Composable
fun WorkoutListItem(
    workout: WorkoutEntry,
    onClick: () -> Unit
) {
    val exercises = decodeExercises(workout.exercisesText)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${exercises.size} exercises",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}