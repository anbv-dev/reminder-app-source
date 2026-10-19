package com.olaf.rereminder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olaf.rereminder.data.Reminder
import com.olaf.rereminder.data.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortMode {
    SCHEDULED_TIME,
    TITLE,
    RECENTLY_MODIFIED
}

class HomeViewModel(private val repository: ReminderRepository) : ViewModel() {

    private val _sortMode = MutableStateFlow(SortMode.SCHEDULED_TIME)
    val sortMode: StateFlow<SortMode> = _sortMode

    val sortedReminders: StateFlow<List<Reminder>> = combine(
        repository.reminders,
        _sortMode
    ) { list, sort ->
        val pending = list.filter { !it.isCompleted }
        val completed = list.filter { it.isCompleted }.sortedByDescending { it.modifiedAt }

        val sortedPending = when (sort) {
            SortMode.SCHEDULED_TIME -> pending.sortedBy { it.scheduledTime }
            SortMode.TITLE -> pending.sortedBy { it.title.lowercase() }
            SortMode.RECENTLY_MODIFIED -> pending.sortedByDescending { it.modifiedAt }
        }

        sortedPending + completed
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overdueCount: StateFlow<Int> = repository.reminders
        .map { list ->
            val now = System.currentTimeMillis()
            list.count { !it.isCompleted && it.isEnabled && it.scheduledTime < now }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
    }

    fun toggleReminder(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateToggle(id, enabled)
        }
    }

    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.saveReminder(reminder)
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    class Factory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
