package com.olaf.rereminder

import com.olaf.rereminder.data.Reminder
import com.olaf.rereminder.util.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DateTimeUtilsTest {

    @Test
    fun testRelativeTimeCapsule_pastReturnsNull() {
        val now = 1_000_000L
        val past = now - 1000L
        assertNull(DateTimeUtils.getRelativeTimeCapsule(past, now))
    }

    @Test
    fun testRelativeTimeCapsule_minutes() {
        val now = 10_000_000L
        val twoMinsLater = now + (2 * 60 * 1000L)
        assertEquals("in 2 mins", DateTimeUtils.getRelativeTimeCapsule(twoMinsLater, now))
    }

    @Test
    fun testRelativeTimeCapsule_hours() {
        val now = 10_000_000L
        val threeHoursLater = now + (3 * 3600 * 1000L)
        assertEquals("in 3 hours", DateTimeUtils.getRelativeTimeCapsule(threeHoursLater, now))
    }

    @Test
    fun testOverdueCount() {
        val now = 100_000L
        val reminders = listOf(
            Reminder(title = "1", scheduledTime = now - 5000L, isCompleted = false, isEnabled = true),
            Reminder(title = "2", scheduledTime = now - 1000L, isCompleted = false, isEnabled = true),
            Reminder(title = "3", scheduledTime = now - 2000L, isCompleted = true, isEnabled = false),
            Reminder(title = "4", scheduledTime = now + 5000L, isCompleted = false, isEnabled = true)
        )
        val count = DateTimeUtils.getOverdueCount(reminders, now)
        assertEquals(2, count)
    }
}
