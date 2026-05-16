package com.example.frontpage.theme.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.frontpage.theme.model.PageThemeEntry
import com.example.frontpage.theme.model.PageThemePreferenceEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PageThemeDao {
    @Query(
        """
        SELECT * FROM page_theme_preferences
        WHERE userId = :userId
        """
    )
    fun observePreferencesForUser(userId: Long): Flow<List<PageThemePreferenceEntry>>

    @Query(
        """
        SELECT * FROM page_theme_entries
        WHERE userId = :userId
        ORDER BY createdAtMillis ASC
        """
    )
    fun observeCustomPresetsForUser(userId: Long): Flow<List<PageThemeEntry>>

    @Query(
        """
        SELECT presetId FROM page_theme_preferences
        WHERE userId = :userId AND target = :target
        LIMIT 1
        """
    )
    suspend fun getPreferencePresetId(
        userId: Long,
        target: String
    ): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreference(preference: PageThemePreferenceEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomPreset(preset: PageThemeEntry)
}
