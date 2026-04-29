package com.example.frontpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NutritionScreen(
    padding: PaddingValues,
    foodItems: List<FoodItem>,
    onBackToHome: () -> Unit,
    onLogMealClick: () -> Unit
) {
    val calorieGoal = 2500
    val totalCalories = foodItems.sumOf { it.calories }
    val remainingCalories = calorieGoal - totalCalories

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

        Card(modifier = Modifier.fillMaxWidth()) {
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
                    Text("${-remainingCalories} kcal over goal")
                }
            }
        }

        MealSection(
            title = "BREAKFAST",
            foodItems = breakfastItems
        )

        MealSection(
            title = "LUNCH",
            foodItems = lunchItems
        )

        MealSection(
            title = "DINNER",
            foodItems = dinnerItems
        )

        MealSection(
            title = "SNACKS",
            foodItems = snackItems
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
    foodItems: List<FoodItem>
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
                Card(modifier = Modifier.fillMaxWidth()) {
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
            }
        }
    }
}