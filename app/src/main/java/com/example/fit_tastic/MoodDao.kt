package com.example.fit_tastic;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMood(MoodEntry moodEntry);

    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    List<MoodEntry> getAllMoodEntries();

    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    MoodEntry getMoodByDate(String date);
}