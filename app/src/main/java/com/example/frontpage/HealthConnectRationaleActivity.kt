package com.example.frontpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.ui.theme.FrontPageTheme

class HealthConnectRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrontPageTheme {
                HealthConnectRationaleScreen(
                    onDoneClick = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun HealthConnectRationaleScreen(
    onDoneClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Health Connect Sleep Access",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "FIT-TASTIC can read sleep sessions from Health Connect only after you grant access. Sleep data is used to import logs, show sleep trends, and improve sleep insights inside this app.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "The app does not use this access to read unrelated Health Connect data.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}
