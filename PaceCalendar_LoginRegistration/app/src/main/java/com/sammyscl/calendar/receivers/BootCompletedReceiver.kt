package com.sammyscl.calendar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sammyscl.calendar.extensions.dbHelper
import com.sammyscl.calendar.extensions.notifyRunningEvents
import com.sammyscl.calendar.extensions.recheckCalDAVCalendars
import com.sammyscl.calendar.extensions.scheduleAllEvents

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // try just getting a reference to the db so it updates in time
        context.dbHelper

        Thread {
            context.apply {
                scheduleAllEvents()
                notifyRunningEvents()
                recheckCalDAVCalendars {}
            }
        }.start()
    }
}
