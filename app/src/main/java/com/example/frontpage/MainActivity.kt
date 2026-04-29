package com.example.frontpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.ui.theme.FrontPageTheme

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

    var showFoodLogging by remember { mutableStateOf(false) }
    var showNutrition by remember { mutableStateOf(false) }
    var foodItems by remember { mutableStateOf(listOf<FoodItem>()) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !showNutrition,
                    onClick = {
                        showNutrition = false
                    },
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
                    selected = showNutrition,
                    onClick = {
                        showNutrition = true
                    },
                    label = { Text("Nutrition") },
                    icon = { Text("🥗") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    label = { Text("Sleep") },
                    icon = { Text("🌙") }
                )
            }
        }
    ) { padding ->

        if (showNutrition) {
            NutritionScreen(
                padding = padding,
                foodItems = foodItems,
                onBackToHome = {
                    showNutrition = false
                },
                onLogMealClick = {
                    showFoodLogging = true
                }
            )
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
                        onClick = { showFoodLogging = true },
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

        if (showFoodLogging) {
            FoodLoggingScreen(
                onAddFood = { newFood ->
                    foodItems = foodItems + newFood

                    val updatedTotalCalories =
                        foodItems.sumOf { it.calories } + newFood.calories

                    calories = "$updatedTotalCalories kcal"
                },
                onClose = {
                    showFoodLogging = false
                }
            )
        }
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