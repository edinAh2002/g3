package com.example.fit_tastic

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodTrackingActivity : AppCompatActivity() {

    private lateinit var moodRadioGroup: RadioGroup
    private lateinit var moodNoteEditText: EditText
    private lateinit var saveMoodButton: Button

    private lateinit var backToHomeButton: Button

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mood_tracking)

        moodRadioGroup = findViewById(R.id.moodRadioGroup)
        moodNoteEditText = findViewById(R.id.moodNoteEditText)
        saveMoodButton = findViewById(R.id.saveMoodButton)
        backToHomeButton = findViewById(R.id.backToHomeButton)
        database = AppDatabase.getDatabase(this)

        saveMoodButton.setOnClickListener {
            saveMoodEntry()
        }
        backToHomeButton.setOnClickListener {
            finish()
        }
    }

    private fun saveMoodEntry() {
        val selectedId = moodRadioGroup.checkedRadioButtonId

        if (selectedId == -1) {
            Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show()
            return
        }

        val moodValue = getMoodValueFromSelectedId(selectedId)
        val note = moodNoteEditText.text.toString().trim()
        val todayDate = getTodayDate()

        val moodEntry = MoodEntry(
            date = todayDate,
            moodValue = moodValue,
            note = note
        )

        lifecycleScope.launch {
            database.moodDao().insertMood(moodEntry)

            Toast.makeText(
                this@MoodTrackingActivity,
                "Mood saved successfully",
                Toast.LENGTH_SHORT
            ).show()

            moodRadioGroup.clearCheck()
            moodNoteEditText.text.clear()
        }
    }

    private fun getMoodValueFromSelectedId(selectedId: Int): Int {
        return when (selectedId) {
            R.id.moodVeryBad -> 1
            R.id.moodBad -> 2
            R.id.moodOkay -> 3
            R.id.moodGood -> 4
            R.id.moodGreat -> 5
            else -> 0
        }
    }

    private fun getTodayDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}