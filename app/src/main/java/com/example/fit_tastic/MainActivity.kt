package com.example.fit_tastic

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeScreen()
        }
    }
}

data class FoodItem(
    val name: String,
    val calories: Int,
    val category: String
)

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    var workout by remember { mutableStateOf("45 min") }
    var sleep by remember { mutableStateOf("7.5h") }
    var hydration by remember { mutableStateOf("6 cups") }
    var showSettings by remember { mutableStateOf(false) }

    var showFoodLogging by remember { mutableStateOf(false) }
    var showNutrition by remember { mutableStateOf(false) }
    var foodItems by remember { mutableStateOf(listOf<FoodItem>()) }

    val calorieGoal = 2500
    val totalCalories = foodItems.sumOf { it.calories }
    val calorieDisplay = "$totalCalories / $calorieGoal"

    val caloriesCardColor = if (totalCalories > calorieGoal) {
        Color(0xFFFFCDD2)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

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
                    onClick = {
                        val intent = Intent(context, MoodHistoryActivity::class.java)
                        context.startActivity(intent)
                    },
                    label = { Text("Mood") },
                    icon = { Text("🙂") }
                )
            }
        }
    ) { padding ->

        if (showNutrition) {
            NutritionScreen(
                paddingModifier = Modifier.padding(padding),
                foodItems = foodItems,
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
                Text("Let's crush your goals today!")

                IconButton(onClick = { showSettings = true }) {
                    Text("⚙️")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Calories",
                        value = calorieDisplay,
                        modifier = Modifier.weight(1f),
                        containerColor = caloriesCardColor
                    )

                    StatCard(
                        title = "Workout",
                        value = workout,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Sleep",
                        value = sleep,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Hydration",
                        value = hydration,
                        modifier = Modifier.weight(1f)
                    )
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
                        onClick = { showFoodLogging = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Log Meal")
                    }

                    Button(
                        onClick = {
                            val intent = Intent(context, MoodTrackingActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Log Mood")
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
                },
                onClose = {
                    showFoodLogging = false
                }
            )
        }
    }
}

@Composable
fun NutritionScreen(
    paddingModifier: Modifier,
    foodItems: List<FoodItem>,
    onLogMealClick: () -> Unit
) {
    Column(
        modifier = paddingModifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nutrition", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = onLogMealClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Meal")
        }

        val groupedItems = foodItems.groupBy { it.category }

        listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { category ->
            Text(category.uppercase(), style = MaterialTheme.typography.titleMedium)

            val items = groupedItems[category].orEmpty()

            if (items.isEmpty()) {
                Text("No meals logged yet.")
            } else {
                items.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.name)
                            Text("${item.calories} calories")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodLoggingScreen(
    onAddFood: (FoodItem) -> Unit,
    onClose: () -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Breakfast") }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Log Meal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Food name") }
                )

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    label = { Text("Calories") }
                )

                Text("Meal type")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { category = "Breakfast" }) {
                        Text("Breakfast")
                    }

                    Button(onClick = { category = "Lunch" }) {
                        Text("Lunch")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { category = "Dinner" }) {
                        Text("Dinner")
                    }

                    Button(onClick = { category = "Snack" }) {
                        Text("Snack")
                    }
                }

                Text("Selected: $category")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calories = caloriesText.toIntOrNull() ?: 0

                    if (foodName.isNotBlank()) {
                        onAddFood(
                            FoodItem(
                                name = foodName,
                                calories = calories,
                                category = category
                            )
                        )
                    }

                    onClose()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}