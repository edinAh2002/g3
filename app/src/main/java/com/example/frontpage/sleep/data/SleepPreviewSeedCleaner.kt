package com.example.frontpage.sleep.data

import android.content.Context
import androidx.core.content.edit
import com.example.frontpage.mood.data.MoodRepository

class SleepPreviewSeedCleaner(
    context: Context,
    private val sleepLogDataSource: SleepLogDataSource,
    private val moodRepository: MoodRepository
) {
    private val preferences = context.getSharedPreferences(
        DEBUG_PREVIEW_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    suspend fun removeFakeMoodSleepContextLinks(userId: Long) {
        repeat(DEBUG_SLEEP_CONTEXT_SEED_COUNT) { index ->
            sleepLogDataSource.deleteSleep(
                userId = userId,
                id = DEBUG_SLEEP_CONTEXT_SEED_ID_BASE + index
            )
        }

        val fakeMoodIds = moodRepository.getAllMoods(userId)
            .filter { entry ->
                entry.note == DEBUG_MOOD_SLEEP_CONTEXT_PREVIEW_NOTE
            }
            .map { entry -> entry.id }

        moodRepository.deleteMoods(
            userId = userId,
            moodIds = fakeMoodIds
        )

        preferences.edit {
            remove("$DEBUG_MOOD_SLEEP_CONTEXT_SEED_KEY_PREFIX$userId")
        }
    }

    private companion object {
        private const val DEBUG_PREVIEW_PREFS_NAME = "debug_preview_seed_preferences"
        private const val DEBUG_MOOD_SLEEP_CONTEXT_SEED_KEY_PREFIX =
            "fake_mood_sleep_context_links_seeded_user_"
        private const val DEBUG_SLEEP_CONTEXT_SEED_ID_BASE = 9_120_526_000L
        private const val DEBUG_SLEEP_CONTEXT_SEED_COUNT = 5
        private const val DEBUG_MOOD_SLEEP_CONTEXT_PREVIEW_NOTE =
            "Temporary preview seed for Mood and Sleep Context Links."
    }
}
