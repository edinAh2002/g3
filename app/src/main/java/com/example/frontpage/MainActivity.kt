package com.example.frontpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.ui.theme.FrontPageTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import com.example.frontpage.sleep.SleepScreen
import com.example.frontpage.sleep.SleepCalculator
import com.example.frontpage.sleep.SleepDateUtils
import com.example.frontpage.sleep.SleepEntry
import com.example.frontpage.sleep.SleepLogDialog
import com.example.frontpage.sleep.SleepRepository
import com.example.frontpage.sleep.SleepSettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontPageTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {

    var calories by remember { mutableStateOf("1,850") }
    var workout by remember { mutableStateOf("45 min") }
    var sleep by remember { mutableStateOf("7.5h") }
    var hydration by remember { mutableStateOf("6 cups") }
    var showSettings by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf("Home") }
    var showSleepLogDialog by remember { mutableStateOf(false) }

    val latestSleep = SleepRepository.getLatestSleep()
    val sleepDisplay = latestSleep?.let {
        SleepCalculator.formatDuration(it.durationMinutes)
    } ?: sleep

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "Home",
                    onClick = { currentScreen = "Home" },
                    label = { Text("Home") },
                    icon = { Text("🏠") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    label = { Text("Workout") },
                    icon = { Text("🏃") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    label = { Text("Nutrition") },
                    icon = { Text("🥗") }
                )

                NavigationBarItem(
                    selected = currentScreen == "Sleep",
                    onClick = { currentScreen = "Sleep" },
                    label = { Text("Sleep") },
                    icon = { Text("🌙") }
                )
            }
        }
    ) { padding ->

        if (currentScreen == "Sleep") {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                SleepScreen()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Good morning!", style = MaterialTheme.typography.headlineSmall)
                Text("Let's crush your goals today! ")

                IconButton(onClick = { showSettings = true }) {
                    Text("⚙️")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Calories", calories, Modifier.weight(1f))
                    StatCard("Workout", workout, Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Sleep", sleepDisplay, Modifier.weight(1f))
                    StatCard("Hydration", hydration, Modifier.weight(1f))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Current Streak")
                        Text("14 days! ", style = MaterialTheme.typography.headlineMedium)
                        Text("Keep it up!")
                    }
                }

                Text("Quick Actions", style = MaterialTheme.typography.titleMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Log Meal")
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Workout")
                    }
                }

                Button(
                    onClick = { showSleepLogDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Log Sleep")
                }

                if (showSettings) {
                    AlertDialog(
                        onDismissRequest = { showSettings = false },
                        title = { Text("Edit Stats") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                OutlinedTextField(
                                    value = calories,
                                    onValueChange = { calories = it },
                                    label = { Text("Calories") }
                                )

                                OutlinedTextField(
                                    value = workout,
                                    onValueChange = { workout = it },
                                    label = { Text("Workout") }
                                )

                                OutlinedTextField(
                                    value = sleep,
                                    onValueChange = { sleep = it },
                                    label = { Text("Sleep") }
                                )

                                OutlinedTextField(
                                    value = hydration,
                                    onValueChange = { hydration = it },
                                    label = { Text("Hydration") }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showSettings = false }) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSettings = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                if (showSleepLogDialog) {
                    SleepLogDialog(
                        goalMinutes = SleepSettingsRepository.sleepGoalMinutes,
                        onDismiss = {
                            showSleepLogDialog = false
                        },
                        onSave = { sleepHour, sleepMinute, wakeHour, wakeMinute, quality, durationMinutes, notes ->

                            val now = System.currentTimeMillis()

                            sleep = SleepCalculator.formatDuration(durationMinutes)

                            SleepRepository.addSleep(
                                SleepEntry(
                                    id = now.toInt(),
                                    date = SleepDateUtils.formatHistoryDate(now),
                                    sleepHour = sleepHour,
                                    sleepMinute = sleepMinute,
                                    wakeHour = wakeHour,
                                    wakeMinute = wakeMinute,
                                    durationMinutes = durationMinutes,
                                    quality = quality,
                                    notes = notes,
                                    dateMillis = now
                                )
                            )

                            showSleepLogDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}