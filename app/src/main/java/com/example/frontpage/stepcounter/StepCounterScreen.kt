package com.example.frontpage.stepcounter

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.health.connect.client.PermissionController
import com.example.frontpage.stepcounter.data.StepsHealthConnectManager
import kotlinx.coroutines.delay
@Composable
fun StepCounterScreen(
    modifier: Modifier = Modifier,
    viewModel: StepCounterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showGoalDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var goalText by remember { mutableStateOf(uiState.goal.toString()) }

    val healthConnectPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = PermissionController.createRequestPermissionResultContract()
        ) {
            viewModel.refreshHealthConnectState()
            viewModel.refreshTodaySteps()
        }

    LaunchedEffect(Unit) {
        viewModel.refreshHealthConnectState()

        while (true) {
            viewModel.refreshTodaySteps()
            delay(30_000)
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Step Counter",
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(
                onClick = { showInfoDialog = true }
            ) {
                Text("ℹ️")
            }
        }

        if (!uiState.isHealthConnectAvailable) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Health Connect is not available on this device.",
                    modifier = Modifier.padding(16.dp)
                )
            }

            return@Column
        }

        if (!uiState.hasHealthConnectPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Permission needed")
                    Text("Allow Health Connect step permission to show your steps.")
                }
            }

            Button(
                onClick = {
                    healthConnectPermissionLauncher.launch(
                        StepsHealthConnectManager.PERMISSIONS
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Health Connect permission")
            }

            return@Column
        }

        val goalAchieved = uiState.steps >= uiState.goal

        val stepCardColor = if (goalAchieved) {
            Color(0xFF4CAF50)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = stepCardColor
            )
        ) {
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
            onClick = { viewModel.refreshTodaySteps() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh steps")
        }

        Button(
            onClick = {
                goalText = uiState.goal.toString()
                showGoalDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change daily goal")
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = {
                showInfoDialog = false
            },
            title = {
                Text("How step tracking works")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "This app reads your step count from Health Connect."
                    )

                    Text(
                        "Health Connect does not always count steps by itself. On older Android versions, another app needs to write step data into Health Connect first."
                    )

                    Text(
                        "You can use an app like Google Fit, Fitbit, Samsung Health, or another fitness app that supports Health Connect."
                    )

                    Text(
                        "Once that app is connected to Health Connect and has permission to write steps, this app can read and display your daily step count."
                    )
                    Text(
                        "On Android 13 or older, some apps and wearables may not be able to write step data to Health Connect."
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showInfoDialog = false
                    }
                ) {
                    Text("Got it")
                }
            }
        )
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