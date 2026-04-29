package com.example.fit_tastic

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var moodEntriesContainer: LinearLayout
    private lateinit var addMoodButton: Button
    private lateinit var backToHomeButton: Button

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mood_history)

        moodEntriesContainer = findViewById(R.id.moodEntriesContainer)
        addMoodButton = findViewById(R.id.addMoodButton)
        backToHomeButton = findViewById(R.id.backToHomeButton)

        database = AppDatabase.getDatabase(this)

        addMoodButton.setOnClickListener {
            val intent = Intent(this, MoodTrackingActivity::class.java)
            startActivity(intent)
        }

        backToHomeButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadMoodEntries()
    }

    private fun loadMoodEntries() {
        lifecycleScope.launch {
            val moodEntries = database.moodDao().getAllMoodEntries()

            moodEntriesContainer.removeAllViews()

            if (moodEntries.isEmpty()) {
                val emptyTextView = TextView(this@MoodHistoryActivity)
                emptyTextView.text = "No mood entries yet. Log your first mood!"
                emptyTextView.textSize = 16f
                emptyTextView.setPadding(0, 24, 0, 24)

                moodEntriesContainer.addView(emptyTextView)
                return@launch
            }

            moodEntries.forEach { moodEntry ->
                val card = createMoodEntryCard(moodEntry)
                moodEntriesContainer.addView(card)
            }
        }
    }

    private fun createMoodEntryCard(moodEntry: MoodEntry): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(32, 24, 32, 24)
        card.setBackgroundColor(Color.rgb(240, 240, 240))

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 24)
        card.layoutParams = layoutParams

        val dateTextView = TextView(this)
        dateTextView.text = moodEntry.date
        dateTextView.textSize = 18f
        dateTextView.setTypeface(null, android.graphics.Typeface.BOLD)

        val moodTextView = TextView(this)
        moodTextView.text = "Mood: ${getMoodLabel(moodEntry.moodValue)}"
        moodTextView.textSize = 16f
        moodTextView.setPadding(0, 8, 0, 0)

        val noteTextView = TextView(this)
        noteTextView.text = if (moodEntry.note.isBlank()) {
            "No note added"
        } else {
            "Note: ${moodEntry.note}"
        }
        noteTextView.textSize = 15f
        noteTextView.setPadding(0, 8, 0, 0)

        card.addView(dateTextView)
        card.addView(moodTextView)
        card.addView(noteTextView)

        return card
    }

    private fun getMoodLabel(moodValue: Int): String {
        return when (moodValue) {
            1 -> "😞 Very bad"
            2 -> "😕 Bad"
            3 -> "😐 Okay"
            4 -> "🙂 Good"
            5 -> "😄 Great"
            else -> "Unknown"
        }
    }
}