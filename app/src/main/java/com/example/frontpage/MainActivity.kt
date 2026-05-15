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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.auth.AuthEvent
import com.example.frontpage.auth.AuthViewModel
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.auth.ui.AuthStartScreen
import com.example.frontpage.auth.ui.LoginScreen
import com.example.frontpage.auth.ui.SignUpScreen
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.mood.ui.MoodFeature
import com.example.frontpage.reminders.ReminderEntry
import com.example.frontpage.reminders.MedicineWizard
import com.example.frontpage.reminders.ReminderListPopup
import com.example.frontpage.sleep.ui.SleepFeature
import com.example.frontpage.stepcounter.StepCounterScreen
import com.example.frontpage.ui.theme.FrontPageTheme
import com.example.frontpage.workout.ui.WorkoutScreen
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.frontpage.reminders.data.ReminderRepository

private enum class AppScreen {
    AuthStart,
    Login,
    SignUp,
    Home,
    Workout,
    Nutrition,
    Sleep,
    Steps,
    Mood
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        val authRepository = AuthRepository(
            userDao = database.userDao(),
            context = applicationContext
        )

        setContent {
            FrontPageTheme {
                FitnessApp(
                    authRepository = authRepository
                )
            }
        }
    }
}

@Composable
fun FitnessApp(
    authRepository: AuthRepository
) {
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.factory(authRepository)
    )

    val authUiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }

    var selectedScreen by remember {
        mutableStateOf(
            if (authViewModel.hasSavedUser()) {
                AppScreen.Home
            } else {
                AppScreen.AuthStart
            }
        )
    }

    LaunchedEffect(authViewModel) {
        authViewModel.authEvents.collect { event ->
            when (event) {
                AuthEvent.Authenticated -> {
                    authViewModel.loadCurrentUser()
                    selectedScreen = AppScreen.Home
                }
            }
        }
    }

    val isAuthScreen =
        selectedScreen == AppScreen.AuthStart ||
                selectedScreen == AppScreen.Login ||
                selectedScreen == AppScreen.SignUp

    val context = LocalContext.current

    var foodItems by remember { mutableStateOf(listOf<FoodItem>()) }
    var showFoodLogging by remember { mutableStateOf(false) }
    val moodController = MoodFeature.rememberController()
    val sleepController = SleepFeature.rememberController()

    Scaffold(
        bottomBar = {
            if (!isAuthScreen) {
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
                        selected = selectedScreen == AppScreen.Steps,
                        onClick = { selectedScreen = AppScreen.Steps },
                        label = { Text("Steps") },
                        icon = { Text("👣") }
                    )

                    NavigationBarItem(
                        selected = selectedScreen == AppScreen.Nutrition,
                        onClick = { selectedScreen = AppScreen.Nutrition },
                        label = { Text("Nutrition") },
                        icon = { Text("🥗") }
                    )

                    NavigationBarItem(
                        selected = selectedScreen == AppScreen.Mood,
                        onClick = { selectedScreen = AppScreen.Mood },
                        label = {Text("Mood") },
                        icon = { Text("🙂") }
                    )

                    NavigationBarItem(
                        selected = selectedScreen == AppScreen.Sleep,
                        onClick = { selectedScreen = AppScreen.Sleep },
                        label = { Text("Sleep") },
                        icon = { Text("🌙") }
                    )
                }
            }
        }
    ) { padding ->

        when (selectedScreen) {
            AppScreen.AuthStart -> {
                AuthStartScreen(
                    onLoginClick = {
                        authViewModel.resetForm()
                        selectedScreen = AppScreen.Login
                    },
                    onSignUpClick = {
                        authViewModel.resetForm()
                        selectedScreen = AppScreen.SignUp
                    },
                    onGuestClick = {
                        authViewModel.continueAsGuest()
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            AppScreen.Login -> {
                LoginScreen(
                    username = authUiState.username,
                    password = authUiState.password,
                    isLoading = authUiState.isLoading,
                    errorMessage = authUiState.errorMessage,
                    onUsernameChange = authViewModel::onUsernameChange,
                    onPasswordChange = authViewModel::onPasswordChange,
                    onLoginClick = {
                        authViewModel.logIn()
                    },
                    onBackClick = {
                        authViewModel.resetForm()
                        selectedScreen = AppScreen.AuthStart
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            AppScreen.SignUp -> {
                SignUpScreen(
                    username = authUiState.username,
                    password = authUiState.password,
                    confirmPassword = authUiState.confirmPassword,
                    isLoading = authUiState.isLoading,
                    errorMessage = authUiState.errorMessage,
                    onUsernameChange = authViewModel::onUsernameChange,
                    onPasswordChange = authViewModel::onPasswordChange,
                    onConfirmPasswordChange = authViewModel::onConfirmPasswordChange,
                    onSignUpClick = {
                        authViewModel.signUp()
                    },
                    onBackClick = {
                        authViewModel.resetForm()
                        selectedScreen = AppScreen.AuthStart
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            AppScreen.Home -> {
                HomeScreen(
                    context = context,
                    modifier = Modifier.padding(padding),
                    foodItems = foodItems,
                    currentUsername = authUiState.currentUsername,
                    currentUserId = authUiState.currentUserId,
                    isGuest = authUiState.isGuest,
                    onLogOutClick = {
                        authViewModel.logOut()
                        selectedScreen = AppScreen.AuthStart
                    },
                    onSwitchAccountClick = {
                        authViewModel.logOut()
                        selectedScreen = AppScreen.AuthStart
                    },
                    onLogMealClick = { showFoodLogging = true },
                    onWorkoutClick = { selectedScreen = AppScreen.Workout },
                    onLogSleepClick = {
                        sleepController.openLogDialog()
                    },
                    onLogMoodClick = {
                        moodController.openLogDialog()
                    }
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
                SleepFeature.MainRoute(
                    modifier = Modifier.padding(padding),
                    controller = sleepController
                )
            }

            AppScreen.Steps -> {
                StepCounterScreen(
                    modifier = Modifier.padding(padding)
                )
            }

            AppScreen.Mood -> {
                MoodFeature.MainRoute(
                    modifier = Modifier.padding(padding),
                    controller = moodController
                )
            }
        }

        if (!isAuthScreen) {
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

            MoodFeature.DialogHost(
                controller = moodController
            )

            SleepFeature.DialogHost(
                controller = sleepController
            )
        }
    }
}

@Composable
fun HomeScreen(
    context: Context,
    modifier: Modifier = Modifier,
    foodItems: List<FoodItem>,
    currentUsername: String?,
    currentUserId: Long?,
    isGuest: Boolean,
    onLogOutClick: () -> Unit,
    onSwitchAccountClick: () -> Unit,
    onLogMealClick: () -> Unit,
    onWorkoutClick: () -> Unit,
    onLogSleepClick: () -> Unit,
    onLogMoodClick: () -> Unit
) {
    val sharedPreferences = context.getSharedPreferences("user_stats", Context.MODE_PRIVATE)

    var workout by remember {
        mutableStateOf(sharedPreferences.getString("workout", "45 min") ?: "45 min")
    }

    var hydration by remember {
        mutableStateOf(sharedPreferences.getString("hydration", "6 cups") ?: "6 cups")
    }

    var showSettings by remember { mutableStateOf(false) }
    var showMedicineWizard by remember { mutableStateOf(false) }
    var showReminderList by remember { mutableStateOf(false) }

    val database = AppDatabase.getDatabase(context)
    val reminderRepository = remember {
        ReminderRepository(database.ReminderDao())
    }

    val scope = rememberCoroutineScope()

    var reminders by remember { mutableStateOf(listOf<ReminderEntry>()) }

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            reminders = reminderRepository.getAllReminders(currentUserId)
        }
    }

    val calorieGoal = 2500
    val totalCalories = foodItems.sumOf { it.calories }
    val calorieDisplay = "$totalCalories / $calorieGoal"

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

            Row {
                IconButton(onClick = { showReminderList = true }) {
                    Text("🔔")
                }

                IconButton(onClick = { showSettings = true }) {
                    Text("⚙️")
                }
            }
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
            SleepFeature.HomeSummaryCard(
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
            title = {
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 24.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(currentUsername ?: "Unknown", fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Account type", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(if (isGuest) "Guest" else "Normal", fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("User ID", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${currentUserId ?: "Unknown"}", fontSize = 14.sp)
                    }

                    Text(
                        text = "Dashboard Stats",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = workout,
                        onValueChange = { workout = it },
                        label = { Text("Workout") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = hydration,
                        onValueChange = { hydration = it },
                        label = { Text("Hydration") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = onLogOutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Log out", fontSize = 20.sp)
                    }

                    Button(
                        onClick = onSwitchAccountClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Switch account", fontSize = 20.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        sharedPreferences.edit()
                            .putString("workout", workout)
                            .putString("hydration", hydration)
                            .apply()

                        showSettings = false
                    }
                ) {
                    Text("Save", fontSize = 20.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Close", fontSize = 20.sp)
                }
            }
        )
    }

    if (showMedicineWizard) {
        MedicineWizard(
            onClose = { showMedicineWizard = false },
            onReminderCreated = { reminder ->
                val userId = currentUserId ?: 0L

                scope.launch {
                    reminderRepository.addReminder(userId, reminder)
                    reminders = reminderRepository.getAllReminders(userId)
                }
            }
        )
    }

    if (showReminderList) {
        ReminderListPopup(
            reminders = reminders,
            onDeleteReminder = { reminder ->
                val userId = currentUserId ?: 0L

                scope.launch {
                    reminderRepository.deleteReminder(userId, reminder.id)
                    reminders = reminderRepository.getAllReminders(userId)
                }
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
