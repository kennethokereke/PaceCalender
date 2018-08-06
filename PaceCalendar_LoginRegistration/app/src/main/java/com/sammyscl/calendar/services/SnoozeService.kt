package com.sammyscl.calendar.services

import android.app.IntentService
import android.content.Intent
import com.sammyscl.calendar.extensions.config
import com.sammyscl.calendar.extensions.dbHelper
import com.sammyscl.calendar.extensions.rescheduleReminder
import com.sammyscl.calendar.helpers.EVENT_ID

class SnoozeService : IntentService("Snooze") {
    override fun onHandleIntent(intent: Intent) {
        val eventId = intent.getIntExtra(EVENT_ID, 0)
        val event = dbHelper.getEventWithId(eventId)
        rescheduleReminder(event, config.snoozeTime)
    }
}
