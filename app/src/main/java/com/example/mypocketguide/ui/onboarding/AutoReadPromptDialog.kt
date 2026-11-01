package com.example.mypocketguide.ui.onboarding

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mypocketguide.R
import com.example.mypocketguide.ui.theme.Gray

@Composable
fun AutoReadPromptDialog(
    onEnable: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* no-op */ },
        title = {
            Text(
                stringResource(R.string.onboarding_auto_read_title),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        text = {
            Text(
                stringResource(R.string.onboarding_auto_read_message),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(
                onClick = onEnable,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    stringResource(R.string.onboarding_enable),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDecline,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    stringResource(R.string.onboarding_not_now),
                    style = MaterialTheme.typography.labelLarge,
                    color = Gray
                )
            }
        }
    )
}