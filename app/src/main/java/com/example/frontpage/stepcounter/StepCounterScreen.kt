package com.example.frontpage.stepcounter

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StepCounterScreen(
    modifier: Modifier = Modifier,
    viewModel: StepCounterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showGoalDialog by remember { mutableStateOf(false) }
    var goalText by remember { mutableStateOf(uiState.goal.toString()) }

    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted

        if (granted) {
            viewModel.startStepCounter()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            viewModel.startStepCounter()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopStepCounter()
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Step Counter", style = MaterialTheme.typography.headlineSmall)

        if (!hasPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Permission needed")
                    Text("Allow physical activity permission to count steps.")
                }
            }

            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                }
            ) {
                Text("Grant Permission")
            }

            return@Column
        }

        if (!uiState.isSensorAvailable) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Step counter sensor is not available on this device.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Today's Steps")

                Text(
                    text = "${uiState.steps}",
                    style = MaterialTheme.typography.displaySmall
                )

                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Goal: ${uiState.goal} steps")
            }
        }

        Button(
            onClick = { viewModel.resetSteps() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset steps")
        }

        Button(
            onClick = {
                goalText = uiState.goal.toString()
                showGoalDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change goal")
        }
    }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = {
                showGoalDialog = false
            },
            title = {
                Text("Change step goal")
            },
            text = {
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    label = { Text("Goal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newGoal = goalText.toIntOrNull()

                        if (newGoal != null && newGoal > 0) {
                            viewModel.changeGoal(newGoal)
                            showGoalDialog = false
                        }
                    }
                ) {
                    Text("Change")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showGoalDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}