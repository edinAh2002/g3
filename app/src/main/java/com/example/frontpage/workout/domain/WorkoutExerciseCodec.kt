package com.example.frontpage.workout.domain

fun encodeExercises(exercises: List<String>): String {
    return exercises.joinToString(separator = "\n")
}

fun decodeExercises(exercisesText: String): List<String> {
    return exercisesText
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }
}