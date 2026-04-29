package com.example.frontpage

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight


@Composable
fun NutritionScreen(
    padding: PaddingValues,
    foodItems: List<FoodItem>,
    onBackToHome: () -> Unit,
    onLogMealClick: () -> Unit,
    onDeleteFood: (FoodItem) -> Unit
) {
    val calorieGoal = 2500
    val totalCalories = foodItems.sumOf { it.calories }
    val remainingCalories = calorieGoal - totalCalories

    val caloriesCardColor = if (totalCalories > calorieGoal) {
        Color(0xFFFFCDD2)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val breakfastItems = foodItems.filter { it.mealType == "Breakfast" }
    val lunchItems = foodItems.filter { it.mealType == "Lunch" }
    val dinnerItems = foodItems.filter { it.mealType == "Dinner" }
    val snackItems = foodItems.filter { it.mealType == "Snacks" }

    Column(
        modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nutrition", style = MaterialTheme.typography.headlineSmall)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = caloriesCardColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Daily Calories", style = MaterialTheme.typography.titleMedium)
                Text("$totalCalories / $calorieGoal kcal")

                LinearProgressIndicator(
                    progress = {
                        (totalCalories.toFloat() / calorieGoal).coerceIn(0f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (remainingCalories >= 0) {
                    Text("$remainingCalories kcal remaining")
                } else {
                    Text("⚠️ ${-remainingCalories} kcal over goal")
                }
            }
        }

        MealSection(
            title = "BREAKFAST",
            foodItems = breakfastItems,
            onDeleteFood = onDeleteFood
        )

        MealSection(
            title = "LUNCH",
            foodItems = lunchItems,
            onDeleteFood = onDeleteFood
        )

        MealSection(
            title = "DINNER",
            foodItems = dinnerItems,
            onDeleteFood = onDeleteFood
        )

        MealSection(
            title = "SNACKS",
            foodItems = snackItems,
            onDeleteFood = onDeleteFood
        )

        Button(
            onClick = onLogMealClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Log Meal")
        }
    }
}

@Composable
fun MealSection(
    title: String,
    foodItems: List<FoodItem>,
    onDeleteFood: (FoodItem) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        HorizontalDivider()

        if (foodItems.isEmpty()) {
            Text("No meals logged.")
        } else {
            foodItems.forEach { food ->
                SwipeToDeleteFoodCard(
                    food = food,
                    onDeleteFood = onDeleteFood
                )
            }
        }
    }
}

@Composable
fun SwipeToDeleteFoodCard(
    food: FoodItem,
    onDeleteFood: (FoodItem) -> Unit
) {
    var showDeleteButton by remember { mutableStateOf(false) }
    var dragAmount by remember { mutableFloatStateOf(0f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        showDeleteButton = dragAmount < -50f
                        dragAmount = 0f
                    },
                    onHorizontalDrag = { _, dragAmountChange ->
                        dragAmount += dragAmountChange
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(food.name, style = MaterialTheme.typography.titleMedium)
                Text("${food.calories} kcal")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Protein: ${food.protein}g")
                    Text("Carbs: ${food.carbs}g")
                    Text("Fat: ${food.fat}g")
                }
            }
        }

        if (showDeleteButton) {
            Card(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                onClick = {
                    onDeleteFood(food)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}