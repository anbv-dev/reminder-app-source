package com.olaf.rereminder.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olaf.rereminder.ReminderApplication
import com.olaf.rereminder.alarm.AlarmScheduler
import com.olaf.rereminder.data.Reminder
import com.olaf.rereminder.ui.edit.EditReminderScreen
import com.olaf.rereminder.ui.home.HomeScreen
import com.olaf.rereminder.ui.home.HomeViewModel
import com.olaf.rereminder.ui.theme.ReminderTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ReminderApplication

        setContent {
            ReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var editingReminder by remember { mutableStateOf<Reminder?>(null) }
                    var isCreatingNew by remember { mutableStateOf(false) }

                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModel.Factory(app.repository)
                    )

                    if (isCreatingNew || editingReminder != null) {
                        EditReminderScreen(
                            initialReminder = editingReminder,
                            onSave = { reminder ->
                                homeViewModel.saveReminder(reminder)
                                AlarmScheduler.schedule(this@MainActivity, reminder)
                                isCreatingNew = false
                                editingReminder = null
                            },
                            onDelete = { reminderId ->
                                homeViewModel.deleteReminder(reminderId)
                                AlarmScheduler.cancel(this@MainActivity, reminderId)
                                isCreatingNew = false
                                editingReminder = null
                            },
                            onDismiss = {
                                isCreatingNew = false
                                editingReminder = null
                            }
                        )
                    } else {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onAddReminderClick = { isCreatingNew = true },
                            onReminderClick = { reminder -> editingReminder = reminder },
                            onToggleReminder = { reminder, enabled ->
                                homeViewModel.toggleReminder(reminder.id, enabled)
                                val updated = reminder.copy(isEnabled = enabled)
                                if (enabled) {
                                    AlarmScheduler.schedule(this@MainActivity, updated)
                                } else {
                                    AlarmScheduler.cancel(this@MainActivity, reminder.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
