package com.olaf.rereminder.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateTimeUtils {

    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH)

    fun formatScheduleDateTime(epochMillis: Long): String {
        val reminderDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMillis),
            ZoneId.systemDefault()
        )
        val reminderDate = reminderDateTime.toLocalDate()
        val today = LocalDate.now()

        val timeStr = reminderDateTime.format(timeFormatter).lowercase(Locale.ENGLISH)

        return if (reminderDate == today) {
            "Today · $timeStr"
        } else {
            val dateStr = reminderDateTime.format(dateFormatter)
            "$dateStr · $timeStr"
        }
    }

    fun getRelativeTimeCapsule(epochMillis: Long, nowMillis: Long = System.currentTimeMillis()): String? {
        if (epochMillis <= nowMillis) return null

        val diffMinutes = ChronoUnit.MINUTES.between(
            Instant.ofEpochMilli(nowMillis),
            Instant.ofEpochMilli(epochMillis)
        )
        val diffHours = ChronoUnit.HOURS.between(
            Instant.ofEpochMilli(nowMillis),
            Instant.ofEpochMilli(epochMillis)
        )
        val diffDays = ChronoUnit.DAYS.between(
            Instant.ofEpochMilli(nowMillis),
            Instant.ofEpochMilli(epochMillis)
        )
        val diffMonths = ChronoUnit.MONTHS.between(
            Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()).toLocalDate(),
            Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        )

        return when {
            diffMinutes < 1 -> "in < 1 min"
            diffMinutes < 60 -> "in $diffMinutes ${if (diffMinutes == 1L) "min" else "mins"}"
            diffHours < 24 -> "in $diffHours ${if (diffHours == 1L) "hour" else "hours"}"
            diffDays < 30 -> "in $diffDays ${if (diffDays == 1L) "day" else "days"}"
            else -> "in $diffMonths ${if (diffMonths == 1L) "month" else "months"}"
        }
    }

    fun getOverdueCount(reminders: List<com.olaf.rereminder.data.Reminder>, nowMillis: Long = System.currentTimeMillis()): Int {
        return reminders.count { !it.isCompleted && it.isEnabled && it.scheduledTime < nowMillis }
    }
}
