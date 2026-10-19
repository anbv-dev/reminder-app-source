package com.olaf.rereminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.olaf.rereminder.ReminderApplication
import com.olaf.rereminder.alarm.AlarmScheduler
import com.olaf.rereminder.ui.alarm.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(AlarmScheduler.EXTRA_REMINDER_ID) ?: return

        val app = context.applicationContext as? ReminderApplication ?: return
        val reminder = app.repository.getReminderById(reminderId) ?: return

        if (!reminder.isEnabled || reminder.isCompleted) {
            return
        }

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_REMINDER_ID, reminderId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(alarmIntent)
    }
}
