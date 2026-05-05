package com.example.frontpage

import android.content.Context
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
import com.example.frontpage.mood.ui.MoodFeature
import com.example.frontpage.sleep.data.SleepRepository
import com.example.frontpage.sleep.data.SleepSettingsRepository
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.SleepLogDialog
import com.example.frontpage.sleep.ui.SleepScreen
import com.example.frontpage.stepcounter.StepCounterScreen
import com.example.frontpage.ui.theme.FrontPageTheme
import com.example.frontpage.workout.ui.WorkoutScreen

private enum class AppScreen {
    Home,
    Workout,
    Nutrition,
    Sleep,
    Steps,
    MoodLog,
    MoodTracking
}

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
    var selectedScreen by remember { mutableStateOf(AppScreen.Home) }
    val context = LocalContext.current

    var foodItems by remember { mutableStateOf(listOf<FoodItem>()) }
    var showFoodLogging by remember { mutableStateOf(false) }
    var showSleepLogDialog by remember { mutableStateOf(false) }

    val latestSleep = SleepRepository.getLatestSleep()
    val sleepDisplay = latestSleep?.let {
        SleepCalculator.formatDuration(it.durationMinutes)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedScreen == AppScreen.Home,
                    onClick = { selectedScreen = AppScreen.Home },
                    label = { Text("Home") },
                    icon = { Text("🏠") }
                )

                NavigationBarItem(
                    selected = selectedScreen == AppScreen.Workout,
                    onClick = { selectedScreen = AppScreen.Workout },
                    label = { Text("Workout") },
                    icon = { Text("🏃") }
                )

                NavigationBarItem(
                    selected = selectedScreen == AppScreen.Nutrition,
                    onClick = { selectedScreen = AppScreen.Nutrition },
                    label = { Text("Nutrition") },
                    icon = { Text("🥗") }
                )

                NavigationBarItem(
                    selected = selectedScreen == AppScreen.Sleep,
                    onClick = { selectedScreen = AppScreen.Sleep },
                    label = { Text("Sleep") },
                    icon = { Text("🌙") }
                )

                NavigationBarItem(
                    selected = selectedScreen == AppScreen.Steps,
                    onClick = { selectedScreen = AppScreen.Steps },
                    label = { Text("Steps") },
                    icon = { Text("👣") }
                )

                NavigationBarItem(
                    selected = selectedScreen == AppScreen.MoodLog ||
                            selectedScreen == AppScreen.MoodTracking,
                    onClick = { selectedScreen = AppScreen.MoodLog },
                    label = { Text("Mood") },
                    icon = { Text("🙂") }
                )
            }
        }
    ) { padding ->

        when (selectedScreen) {
            AppScreen.Home -> {
                HomeScreen(
                    context = context,
                    modifier = Modifier.padding(padding),
                    foodItems = foodItems,
                    sleepDisplay = sleepDisplay,
                    onLogMealClick = { showFoodLogging = true },
                    onWorkoutClick = { selectedScreen = AppScreen.Workout },
                    onLogSleepClick = { showSleepLogDialog = true },
                    onLogMoodClick = { selectedScreen = AppScreen.MoodTracking }
                )
            }

            AppScreen.Workout -> {
                WorkoutScreen()
            }

            AppScreen.Nutrition -> {
                NutritionScreen(
                    padding = padding,
                    foodItems = foodItems,
                    onBackToHome = {
                        selectedScreen = AppScreen.Home
                    },
                    onLogMealClick = {
                        showFoodLogging = true
                    },
                    onDeleteFood = { foodToDelete ->
                        foodItems = foodItems - foodToDelete
                    }
                )
            }

            AppScreen.Sleep -> {
                SleepScreen(
                    modifier = Modifier.padding(padding)
                )
            }

            AppScreen.Steps -> {
                StepCounterScreen(
                    modifier = Modifier.padding(padding)
                )
            }

            AppScreen.MoodLog -> {
                MoodFeature.LogRoute(
                    modifier = Modifier.padding(padding),
                    onLogNewMood = {
                        selectedScreen = AppScreen.MoodTracking
                    }
                )
            }

            AppScreen.MoodTracking -> {
                MoodFeature.TrackingRoute(
                    modifier = Modifier.padding(padding),
                    onSaved = {
                        selectedScreen = AppScreen.MoodLog
                    },
                    onBack = {
                        selectedScreen = AppScreen.MoodLog
                    }
                )
            }
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

        if (showSleepLogDialog) {
            SleepLogDialog(
                goalMinutes = SleepSettingsRepository.sleepGoalMinutes,
                onDismiss = {
                    showSleepLogDialog = false
                },
                onSave = { sleepHour, sleepMinute, wakeHour, wakeMinute, quality, durationMinutes, notes ->

                    val now = System.currentTimeMillis()

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

@Composable
fun HomeScreen(
    context: Context,
    modifier: Modifier = Modifier,
    foodItems: List<FoodItem>,
    sleepDisplay: String?,
    onLogMealClick: () -> Unit,
    onWorkoutClick: () -> Unit,
    onLogSleepClick: () -> Unit,
    onLogMoodClick: () -> Unit
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

    val calorieGoal = 2500
    val totalCalories = foodItems.sumOf { it.calories }
    val calorieDisplay = "$totalCalories / $calorieGoal"

    val sleepCardDisplay = sleepDisplay ?: sleep

    val caloriesCardColor = if (totalCalories > calorieGoal) {
        Color(0xFFFFCDD2)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

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
                value = sleepCardDisplay,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Hydration",
                value = hydration,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MoodFeature.HomeSummaryCard(
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
                onClick = onLogMealClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Log Meal")
            }

            Button(
                onClick = onWorkoutClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Workout")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onLogSleepClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Log Sleep")
            }

            Button(
                onClick = onLogMoodClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Log Mood")
            }
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