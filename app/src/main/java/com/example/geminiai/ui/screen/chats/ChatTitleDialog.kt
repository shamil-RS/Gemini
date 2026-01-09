package com.example.geminiai.ui.screen.chats

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.geminiai.R

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ChatTitleDialog(
    initialTitle: String,
    onConfirmRequest: (title: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    var title by rememberSaveable { mutableStateOf(initialTitle) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .widthIn(max = screenWidthDp - 40.dp)
            .heightIn(max = screenHeightDp - 80.dp),
        title = { Text(text = stringResource(R.string.chat_title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = stringResource(R.string.chat_title_dialog_description))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    value = title,
                    singleLine = true,
                    isError = title.length > 50,
                    supportingText = {
                        if (title.length > 50) {
                            Text(stringResource(R.string.title_length_limit, title.length))
                        }
                    },
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.chat_title)) }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && title != initialTitle,
                onClick = {
                    onConfirmRequest(title)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.update))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}