package com.olaf.rereminder.ui.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.olaf.rereminder.data.Reminder
import com.olaf.rereminder.util.DateTimeUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderScreen(
    initialReminder: Reminder?,
    onSave: (Reminder) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialReminder?.title ?: "") }
    var description by remember { mutableStateOf(initialReminder?.description ?: "") }

    var selectedCalendar by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                if (initialReminder != null) {
                    timeInMillis = initialReminder.scheduledTime
                } else {
                    add(Calendar.HOUR_OF_DAY, 1)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (initialReminder == null) "New Reminder" else "Edit Reminder") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (initialReminder != null) {
                        IconButton(onClick = { onDelete(initialReminder.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete reminder")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                singleLine = false,
                maxLines = 3
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                singleLine = false,
                maxLines = 4
            )

            Text(
                text = "Scheduled: " + DateTimeUtils.formatScheduleDateTime(selectedCalendar.timeInMillis),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val year = selectedCalendar.get(Calendar.YEAR)
                        val month = selectedCalendar.get(Calendar.MONTH)
                        val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)
                        DatePickerDialog(context, { _, y, m, d ->
                            val newCal = selectedCalendar.clone() as Calendar
                            newCal.set(Calendar.YEAR, y)
                            newCal.set(Calendar.MONTH, m)
                            newCal.set(Calendar.DAY_OF_MONTH, d)
                            selectedCalendar = newCal
                        }, year, month, day).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select Date")
                }

                OutlinedButton(
                    onClick = {
                        val hour = selectedCalendar.get(Calendar.HOUR_OF_DAY)
                        val minute = selectedCalendar.get(Calendar.MINUTE)
                        TimePickerDialog(context, { _, h, m ->
                            val newCal = selectedCalendar.clone() as Calendar
                            newCal.set(Calendar.HOUR_OF_DAY, h)
                            newCal.set(Calendar.MINUTE, m)
                            newCal.set(Calendar.SECOND, 0)
                            selectedCalendar = newCal
                        }, hour, minute, false).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select Time")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (initialReminder != null) {
                OutlinedButton(
                    onClick = { onDelete(initialReminder.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete reminder")
                }
            }

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val updated = (initialReminder ?: Reminder(
                            title = title.trim(),
                            scheduledTime = selectedCalendar.timeInMillis
                        )).copy(
                            title = title.trim(),
                            description = description.trim(),
                            scheduledTime = selectedCalendar.timeInMillis,
                            isEnabled = true,
                            isCompleted = false
                        )
                        onSave(updated)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}
