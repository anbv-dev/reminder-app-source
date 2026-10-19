package com.olaf.rereminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.olaf.rereminder.ReminderApplication
import com.olaf.rereminder.alarm.AlarmScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED) {

            val app = context.applicationContext as? ReminderApplication ?: return
            val reminders = app.repository.reminders.value
            val now = System.currentTimeMillis()

            for (reminder in reminders) {
                if (reminder.isEnabled && !reminder.isCompleted && reminder.scheduledTime > now) {
                    AlarmScheduler.schedule(context, reminder)
                }
            }
        }
    }
}
