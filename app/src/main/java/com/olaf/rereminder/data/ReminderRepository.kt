package com.olaf.rereminder.data

import android.content.Context
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ReminderRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private val mutex = Mutex()
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val storageFile: File
        get() {
            val storageContext = ContextCompat.createDeviceProtectedStorageContext(context) ?: context
            return File(storageContext.filesDir, "reminders.json")
        }

    init {
        loadReminders()
    }

    fun loadReminders() {
        try {
            val file = storageFile
            if (file.exists()) {
                val content = file.readText()
                if (content.isNotBlank()) {
                    val list = json.decodeFromString<List<Reminder>>(content)
                    _reminders.value = list
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _reminders.value = emptyList()
    }

    suspend fun saveReminder(reminder: Reminder) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val current = _reminders.value.toMutableList()
            val index = current.indexOfFirst { it.id == reminder.id }
            val updated = reminder.copy(modifiedAt = System.currentTimeMillis())
            if (index >= 0) {
                current[index] = updated
            } else {
                current.add(0, updated)
            }
            persistAndEmit(current)
        }
    }

    suspend fun deleteReminder(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val current = _reminders.value.filterNot { it.id == id }
            persistAndEmit(current)
        }
    }

    suspend fun updateToggle(id: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val current = _reminders.value.map {
                if (it.id == id) {
                    it.copy(isEnabled = enabled, modifiedAt = System.currentTimeMillis())
                } else it
            }
            persistAndEmit(current)
        }
    }

    suspend fun markCompleted(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val now = System.currentTimeMillis()
            val current = _reminders.value.map {
                if (it.id == id) {
                    it.copy(isCompleted = true, isEnabled = false, dismissedAt = now, modifiedAt = now)
                } else it
            }
            persistAndEmit(current)
        }
    }

    suspend fun snoozeReminder(id: String, snoozeDurationMs: Long = 10 * 60 * 1000L) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val now = System.currentTimeMillis()
            val newScheduledTime = now + snoozeDurationMs
            val current = _reminders.value.map {
                if (it.id == id) {
                    it.copy(
                        scheduledTime = newScheduledTime,
                        isEnabled = true,
                        isCompleted = false,
                        modifiedAt = now
                    )
                } else it
            }
            persistAndEmit(current)
        }
    }

    fun getReminderById(id: String): Reminder? {
        return _reminders.value.find { it.id == id }
    }

    private fun persistAndEmit(list: List<Reminder>) {
        try {
            val file = storageFile
            val parent = file.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }
            file.writeText(json.encodeToString(list))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _reminders.value = list
    }
}
