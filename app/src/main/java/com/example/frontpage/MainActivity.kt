package com.example.frontpage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.frontpage.stepcounter.StepCounterScreen
import com.example.frontpage.ui.theme.FrontPageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrontPageTheme {
                FitnessApp()
            }
        }
    }
}

@Composable
fun FitnessApp() {
    var selectedScreen by remember { mutableStateOf("Home") }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedScreen == "Home",
                    onClick = { selectedScreen = "Home" },
                    label = { Text("Home") },
                    icon = { Text("🏠") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Workout",
                    onClick = { selectedScreen = "Workout" },
                    label = { Text("Workout") },
                    icon = { Text("🏃") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Nutrition",
                    onClick = { selectedScreen = "Nutrition" },
                    label = { Text("Nutrition") },
                    icon = { Text("🥗") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Sleep",
                    onClick = { selectedScreen = "Sleep" },
                    label = { Text("Sleep") },
                    icon = { Text("🌙") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Steps",
                    onClick = { selectedScreen = "Steps" },
                    label = { Text("Steps") },
                    icon = { Text("👣") }
                )
            }
        }
    ) { padding ->
        when (selectedScreen) {
            "Home" -> HomeScreen(
                context = context,
                modifier = Modifier.padding(padding)
            )

            "Workout" -> PlaceholderScreen(
                title = "Workout",
                modifier = Modifier.padding(padding)
            )

            "Nutrition" -> PlaceholderScreen(
                title = "Nutrition",
                modifier = Modifier.padding(padding)
            )

            "Sleep" -> PlaceholderScreen(
                title = "Sleep",
                modifier = Modifier.padding(padding)
            )

            "Steps" -> StepCounterScreen(
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun HomeScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val sharedPreferences = context.getSharedPreferences("user_stats", Context.MODE_PRIVATE)

    var calories by remember {
        mutableStateOf(sharedPreferences.getString("calories", "1,850") ?: "1,850")
    }

    var workout by remember {
        mutableStateOf(sharedPreferences.getString("workout", "45 min") ?: "45 min")
    }

    var sleep by remember {
        mutableStateOf(sharedPreferences.getString("sleep", "7.5h") ?: "7.5h")
    }

    var hydration by remember {
        mutableStateOf(sharedPreferences.getString("hydration", "6 cups") ?: "6 cups")
    }

    var showSettings by remember { mutableStateOf(false) }
    var showMedicineWizard by remember { mutableStateOf(false) }
    var showReminderList by remember { mutableStateOf(false) }

    var reminders by remember { mutableStateOf(listOf<MedicineReminder>()) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Good morning!", style = MaterialTheme.typography.headlineSmall)
                Text("Let's crush your goals today!")
            }

            Button(
                onClick = { showReminderList = true }
            ) {
                Text("🔔")
            }
        }

        IconButton(onClick = { showSettings = true }) {
            Text("⚙️")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Calories", calories, Modifier.weight(1f))
            StatCard("Workout", workout, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Sleep", sleep, Modifier.weight(1f))
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
                Text("14 days!", style = MaterialTheme.typography.headlineMedium)
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
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Log Sleep")
        }

        Button(
            onClick = { showMedicineWizard = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Medicine Reminder")
        }
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
                TextButton(
                    onClick = {
                        sharedPreferences.edit()
                            .putString("calories", calories)
                            .putString("workout", workout)
                            .putString("sleep", sleep)
                            .putString("hydration", hydration)
                            .apply()

                        showSettings = false
                    }
                ) {
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

    if (showMedicineWizard) {
        MedicineWizard(
            onClose = { showMedicineWizard = false },
            onReminderCreated = { reminder ->
                reminders = reminders + reminder
            }
        )
    }

    if (showReminderList) {
        ReminderListPopup(
            reminders = reminders,
            onDeleteReminder = { reminder ->
                reminders = reminders - reminder
            },
            onClose = { showReminderList = false }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text("Coming soon.")
    }
}