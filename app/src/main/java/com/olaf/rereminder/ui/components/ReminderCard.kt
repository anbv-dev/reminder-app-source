package com.olaf.rereminder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olaf.rereminder.data.Reminder
import com.olaf.rereminder.util.DateTimeUtils

@Composable
fun ReminderCard(
    reminder: Reminder,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInactive = !reminder.isEnabled || reminder.isCompleted
    val relativeTime = if (!reminder.isCompleted && reminder.isEnabled) {
        DateTimeUtils.getRelativeTimeCapsule(reminder.scheduledTime)
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            } else if (!reminder.isEnabled) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isInactive) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Icon Badge in a circular container (Preserves original reReminder visual language)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (reminder.isCompleted) {
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        } else if (!reminder.isEnabled) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        reminder.isCompleted -> Icons.Default.Check
                        reminder.isEnabled -> Icons.Default.Alarm
                        else -> Icons.Default.AlarmOff
                    },
                    contentDescription = if (reminder.isEnabled) "Active reminder" else "Inactive reminder",
                    tint = when {
                        reminder.isCompleted -> MaterialTheme.colorScheme.outline
                        reminder.isEnabled -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Main Text Group
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (reminder.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        } else if (!reminder.isEnabled) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    ),
                    maxLines = 3
                )

                if (reminder.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = if (reminder.isCompleted) 0.4f else 0.75f
                            )
                        ),
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metadata Row: Scheduled Time + Relative Capsule or Completed Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time Label Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = DateTimeUtils.formatScheduleDateTime(reminder.scheduledTime),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = if (reminder.isCompleted) 0.5f else 0.9f
                                )
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Relative Time Pill (when active and pending)
                    if (relativeTime != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                        ) {
                            Text(
                                text = relativeTime,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    } else if (reminder.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right Toggle Switch
            Switch(
                checked = reminder.isEnabled && !reminder.isCompleted,
                onCheckedChange = { checked -> onToggle(checked) },
                enabled = !reminder.isCompleted
            )
        }
    }
}
