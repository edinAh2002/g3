package com.example.frontpage.mood.domain

import com.example.frontpage.mood.model.MoodScalePreset
import java.util.Locale

object MoodLabelUtils {

    fun getMoodLabel(
        moodValue: Int,
        preset: MoodScalePreset = MoodScalePreset.Default
    ): String {
        return preset.optionForValue(moodValue).label
    }

    fun getMoodDescription(
        moodValue: Int,
        preset: MoodScalePreset = MoodScalePreset.Default
    ): String {
        return preset.optionForValue(moodValue).description
    }

    fun formatMoodAverage(value: Double?): String {
        return value?.let { average ->
            "${String.format(Locale.getDefault(), "%.1f", average)} / 5"
        } ?: "No data"
    }

    fun formatMoodChange(value: Double?): String {
        return value?.let { change ->
            val sign = if (change > 0) "+" else ""
            "$sign${String.format(Locale.getDefault(), "%.1f", change)}"
        } ?: "--"
    }
}
