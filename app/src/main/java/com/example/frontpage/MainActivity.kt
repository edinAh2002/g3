package com.example.frontpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.frontpage.ui.theme.FrontPageTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import com.example.frontpage.stepcounter.StepCounterScreen

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
                    icon = { Text("🏃") }
                )
            }
        }
    ) { padding ->
        when (selectedScreen) {
            "Home" -> HomeScreen(Modifier.padding(padding))
            "Workout" -> PlaceholderScreen("Workout", Modifier.padding(padding))
            "Nutrition" -> PlaceholderScreen("Nutrition", Modifier.padding(padding))
            "Sleep" -> PlaceholderScreen("Sleep", Modifier.padding(padding))
            "Steps" -> StepCounterScreen(Modifier.padding(padding))
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var calories by remember { mutableStateOf("1,850") }
    var workout by remember { mutableStateOf("45 min") }
    var sleep by remember { mutableStateOf("7.5h") }
    var hydration by remember { mutableStateOf("6 cups") }
    var showSettings by remember { mutableStateOf(false) }
    var selectedScreen by remember { mutableStateOf("Home") }


        Column(
            modifier = Modifier
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
                onClick = {},
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

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text("Coming soon.")
    }
}