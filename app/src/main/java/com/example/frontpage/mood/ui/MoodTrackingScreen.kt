package com.example.frontpage.mood.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.mood.model.MoodTrackingUiState

@Composable
fun MoodTrackingRoute(
    modifier: Modifier = Modifier,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: MoodViewModel = viewModel()
) {
    val context = LocalContext.current
    val trackingState by viewModel.trackingState.collectAsState()

    MoodTrackingScreen(
        modifier = modifier,
        state = trackingState,
        onMoodSelected = { moodValue ->
            viewModel.selectMood(moodValue)
        },
        onNoteChanged = { note ->
            viewModel.updateNote(note)
        },
        onSaveMood = {
            viewModel.saveMood {
                Toast.makeText(
                    context,
                    "Mood saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

                onSaved()
            }
        },
        onBack = onBack
    )
}

@Composable
fun MoodTrackingDialogRoute(
    onSaved: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: MoodViewModel = viewModel()
) {
    val context = LocalContext.current
    val trackingState by viewModel.trackingState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetTrackingForm()
    }

    MoodTrackingDialog(
        state = trackingState,
        onMoodSelected = { moodValue ->
            viewModel.selectMood(moodValue)
        },
        onNoteChanged = { note ->
            viewModel.updateNote(note)
        },
        onSaveMood = {
            viewModel.saveMood {
                Toast.makeText(
                    context,
                    "Mood saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

                onSaved()
            }
        },
        onDismiss = {
            viewModel.resetTrackingForm()
            onDismiss()
        }
    )
}

@Composable
fun MoodTrackingScreen(
    modifier: Modifier = Modifier,
    state: MoodTrackingUiState,
    onMoodSelected: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onSaveMood: () -> Unit,
    onBack: () -> Unit
) {
    MoodTrackingContent(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        title = "How are you feeling today?",
        subtitle = "Choose your mood and optionally add a short note.",
        state = state,
        onMoodSelected = onMoodSelected,
        onNoteChanged = onNoteChanged,
        onSaveMood = onSaveMood,
        onCancel = onBack,
        saveButtonText = "Save Mood",
        cancelButtonText = "Back"
    )
}

@Composable
fun MoodTrackingDialog(
    state: MoodTrackingUiState,
    onMoodSelected: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onSaveMood: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodTrackingContent(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                title = "Log Mood",
                subtitle = "How are you feeling right now?",
                state = state,
                onMoodSelected = onMoodSelected,
                onNoteChanged = onNoteChanged,
                onSaveMood = onSaveMood,
                onCancel = onDismiss,
                saveButtonText = "Save",
                cancelButtonText = "Cancel"
            )
        }
    }
}

@Composable
private fun MoodTrackingContent(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    state: MoodTrackingUiState,
    onMoodSelected: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onSaveMood: () -> Unit,
    onCancel: () -> Unit,
    saveButtonText: String,
    cancelButtonText: String
) {
    val moods = listOf(
        1 to "😞 Very bad",
        2 to "😕 Bad",
        3 to "😐 Okay",
        4 to "🙂 Good",
        5 to "😄 Great"
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.forEach { mood ->
                    Row {
                        RadioButton(
                            selected = state.selectedMood == mood.first,
                            onClick = {
                                onMoodSelected(mood.first)
                            }
                        )

                        Text(
                            text = mood.second,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.note,
            onValueChange = onNoteChanged,
            label = { Text("Add a note, optional") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(cancelButtonText)
            }

            Button(
                onClick = onSaveMood,
                enabled = !state.isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (state.isSaving) {
                        "Saving..."
                    } else {
                        saveButtonText
                    }
                )
            }
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}