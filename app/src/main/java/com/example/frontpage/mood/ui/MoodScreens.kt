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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodLogView
import com.example.frontpage.mood.model.MoodTrackingUiState

@Composable
fun MoodLogRoute(
    modifier: Modifier = Modifier,
    onLogNewMood: () -> Unit,
    viewModel: MoodViewModel = viewModel()
) {
    val moodEntries by viewModel.moodEntries.collectAsState()
    val averageMood by viewModel.averageMood.collectAsState()
    val activeLogView by viewModel.activeLogView.collectAsState()
    val latestMood by viewModel.latestMood.collectAsState()

    var moodBeingEdited by remember {
        mutableStateOf<MoodEntry?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.loadMoods()
    }

    MoodLogScreen(
        modifier = modifier,
        moodEntries = moodEntries,
        averageMood = averageMood,
        activeLogView = activeLogView,
        latestMood = latestMood,
        onShowList = {
            viewModel.showListView()
        },
        onShowWeek = {
            viewModel.showWeekView()
        },
        onShowSummary = {
            viewModel.showSummaryView()
        },
        onLogNewMood = onLogNewMood,
        onEditMood = { moodEntry ->
            moodBeingEdited = moodEntry
        },
        onDeleteMood = { moodEntry ->
            viewModel.deleteMood(moodEntry)
        }
    )

    moodBeingEdited?.let { moodEntry ->
        EditMoodDialog(
            moodEntry = moodEntry,
            onDismiss = {
                moodBeingEdited = null
            },
            onSave = { newMoodValue, newNote ->
                viewModel.updateExistingMood(
                    moodEntry = moodEntry,
                    newMoodValue = newMoodValue,
                    newNote = newNote
                )
                moodBeingEdited = null
            }
        )
    }
}

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
fun MoodLogScreen(
    modifier: Modifier = Modifier,
    moodEntries: List<MoodEntry>,
    averageMood: Double?,
    activeLogView: MoodLogView,
    latestMood: MoodEntry?,
    onShowList: () -> Unit,
    onShowWeek: () -> Unit,
    onShowSummary: () -> Unit,
    onLogNewMood: () -> Unit,
    onEditMood: (MoodEntry) -> Unit,
    onDeleteMood: (MoodEntry) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mood Log",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Track how your mood changes over time.",
            style = MaterialTheme.typography.bodyMedium
        )

        MoodViewSwitcher(
            activeLogView = activeLogView,
            onShowList = onShowList,
            onShowWeek = onShowWeek,
            onShowSummary = onShowSummary
        )

        Button(
            onClick = onLogNewMood,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log New Mood")
        }

        when (activeLogView) {
            MoodLogView.List -> {
                MoodListView(
                    moodEntries = moodEntries,
                    averageMood = averageMood,
                    onEditMood = onEditMood,
                    onDeleteMood = onDeleteMood
                )
            }

            MoodLogView.Week -> {
                MoodWeekView(
                    moodEntries = moodEntries,
                    averageMood = averageMood,
                    onEditMood = onEditMood,
                    onDeleteMood = onDeleteMood
                )
            }

            MoodLogView.Summary -> {
                MoodSummaryView(
                    moodEntries = moodEntries,
                    averageMood = averageMood,
                    latestMood = latestMood
                )
            }
        }
    }
}

@Composable
fun MoodViewSwitcher(
    activeLogView: MoodLogView,
    onShowList: () -> Unit,
    onShowWeek: () -> Unit,
    onShowSummary: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onShowList,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (activeLogView == MoodLogView.List) {
                    "List ✓"
                } else {
                    "List"
                }
            )
        }

        Button(
            onClick = onShowWeek,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (activeLogView == MoodLogView.Week) {
                    "Week ✓"
                } else {
                    "Week"
                }
            )
        }

        Button(
            onClick = onShowSummary,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (activeLogView == MoodLogView.Summary) {
                    "Summary ✓"
                } else {
                    "Summary"
                }
            )
        }
    }
}

@Composable
fun MoodListView(
    moodEntries: List<MoodEntry>,
    averageMood: Double?,
    onEditMood: (MoodEntry) -> Unit,
    onDeleteMood: (MoodEntry) -> Unit
) {
    AverageMoodCard(
        title = "Average Mood",
        averageMood = averageMood
    )

    if (moodEntries.isEmpty()) {
        EmptyMoodCard()
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            moodEntries.forEach { moodEntry ->
                MoodEntryCard(
                    moodEntry = moodEntry,
                    onEditMood = onEditMood,
                    onDeleteMood = onDeleteMood
                )
            }
        }
    }
}

@Composable
fun MoodWeekView(
    moodEntries: List<MoodEntry>,
    averageMood: Double?,
    onEditMood: (MoodEntry) -> Unit,
    onDeleteMood: (MoodEntry) -> Unit
) {
    Text(
        text = "This week: ${MoodDateUtils.getCurrentWeekDisplayRange()}",
        style = MaterialTheme.typography.titleMedium
    )

    AverageMoodCard(
        title = "This Week's Average Mood",
        averageMood = averageMood
    )

    if (moodEntries.isEmpty()) {
        EmptyMoodCard(
            message = "No moods logged this week yet."
        )
    } else {
        val entriesByDate = moodEntries.groupBy { moodEntry ->
            moodEntry.date
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            entriesByDate.forEach { dateGroup ->
                val date = dateGroup.key
                val entriesForDate = dateGroup.value

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "${MoodDateUtils.getDayNameFromDate(date)} · $date",
                            style = MaterialTheme.typography.titleMedium
                        )

                        entriesForDate.forEach { moodEntry ->
                            MoodEntryCard(
                                moodEntry = moodEntry,
                                onEditMood = onEditMood,
                                onDeleteMood = onDeleteMood
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoodSummaryView(
    moodEntries: List<MoodEntry>,
    averageMood: Double?,
    latestMood: MoodEntry?
) {
    val entryCount = moodEntries.size
    val bestMood = moodEntries.maxByOrNull { moodEntry ->
        moodEntry.moodValue
    }

    val lowestMood = moodEntries.minByOrNull { moodEntry ->
        moodEntry.moodValue
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryStatCard(
            title = "Latest Mood",
            value = latestMood?.let {
                "${MoodLabelUtils.getMoodLabel(it.moodValue)} · ${it.date} at ${it.time}"
            } ?: "Not logged yet"
        )

        SummaryStatCard(
            title = "Average Mood",
            value = averageMood?.let {
                "${String.format("%.1f", it)} / 5"
            } ?: "No data yet"
        )

        SummaryStatCard(
            title = "Total Entries",
            value = entryCount.toString()
        )

        SummaryStatCard(
            title = "Best Mood",
            value = bestMood?.let {
                "${MoodLabelUtils.getMoodLabel(it.moodValue)} · ${it.date}"
            } ?: "No data yet"
        )

        SummaryStatCard(
            title = "Lowest Mood",
            value = lowestMood?.let {
                "${MoodLabelUtils.getMoodLabel(it.moodValue)} · ${it.date}"
            } ?: "No data yet"
        )
    }
}

@Composable
fun AverageMoodCard(
    title: String,
    averageMood: Double?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title)

            Text(
                text = averageMood?.let {
                    "${String.format("%.1f", it)} / 5"
                } ?: "No mood data yet",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun SummaryStatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun EmptyMoodCard(
    message: String = "No mood entries yet. Log your first mood!"
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun MoodEntryCard(
    moodEntry: MoodEntry,
    onEditMood: (MoodEntry) -> Unit,
    onDeleteMood: (MoodEntry) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${moodEntry.date} at ${moodEntry.time}",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                Text("Mood: ")
                Text(MoodLabelUtils.getMoodLabel(moodEntry.moodValue))
            }

            Text(
                text = if (moodEntry.note.isBlank()) {
                    "No note added"
                } else {
                    "Note: ${moodEntry.note}"
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        onEditMood(moodEntry)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = {
                        onDeleteMood(moodEntry)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun EditMoodDialog(
    moodEntry: MoodEntry,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var selectedMood by remember {
        mutableIntStateOf(moodEntry.moodValue)
    }

    var note by remember {
        mutableStateOf(moodEntry.note)
    }

    val moods = listOf(
        1 to "😞 Very bad",
        2 to "😕 Bad",
        3 to "😐 Okay",
        4 to "🙂 Good",
        5 to "😄 Great"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Mood")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                moods.forEach { mood ->
                    Row {
                        RadioButton(
                            selected = selectedMood == mood.first,
                            onClick = {
                                selectedMood = mood.first
                            }
                        )

                        Text(
                            text = mood.second,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(selectedMood, note)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
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
    val moods = listOf(
        1 to "😞 Very bad",
        2 to "😕 Bad",
        3 to "😐 Okay",
        4 to "🙂 Good",
        5 to "😄 Great"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Choose your mood and optionally add a short note.",
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

        Button(
            onClick = onSaveMood,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (state.isSaving) {
                    "Saving..."
                } else {
                    "Save Mood"
                }
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}