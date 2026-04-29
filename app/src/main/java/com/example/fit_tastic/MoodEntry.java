package com.example.fit_tastic;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "mood_entries",
        indices = {@Index(value = {"date"}, unique = true)}
)
public class MoodEntry {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "mood_value")
    public int moodValue;

    @ColumnInfo(name = "note")
    public String note;

    public MoodEntry(String date, int moodValue, String note) {
        this.date = date;
        this.moodValue = moodValue;
        this.note = note;
    }
}