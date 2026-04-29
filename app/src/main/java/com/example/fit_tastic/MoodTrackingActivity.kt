package com.example.fit_tastic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodTrackingActivity extends Activity {

    private RadioGroup moodRadioGroup;
    private EditText moodNoteEditText;
    private Button saveMoodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracking);

        moodRadioGroup = findViewById(R.id.moodRadioGroup);
        moodNoteEditText = findViewById(R.id.moodNoteEditText);
        saveMoodButton = findViewById(R.id.saveMoodButton);

        saveMoodButton.setOnClickListener(v -> saveMoodEntry());
    }

    private void saveMoodEntry() {
        int selectedId = moodRadioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show();
            return;
        }

        int moodValue = getMoodValueFromSelectedId(selectedId);
        String note = moodNoteEditText.getText().toString().trim();
        String todayDate = getTodayDate();

        getSharedPreferences("MoodPrefs", MODE_PRIVATE)
                .edit()
                .putInt(todayDate + "_mood", moodValue)
                .putString(todayDate + "_note", note)
                .apply();

        Toast.makeText(this, "Mood saved successfully", Toast.LENGTH_SHORT).show();
    }

    private int getMoodValueFromSelectedId(int selectedId) {
        if (selectedId == R.id.moodVeryBad) {
            return 1;
        } else if (selectedId == R.id.moodBad) {
            return 2;
        } else if (selectedId == R.id.moodOkay) {
            return 3;
        } else if (selectedId == R.id.moodGood) {
            return 4;
        } else if (selectedId == R.id.moodGreat) {
            return 5;
        } else {
            return 0;
        }
    }

    private String getTodayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(new Date());
    }
}