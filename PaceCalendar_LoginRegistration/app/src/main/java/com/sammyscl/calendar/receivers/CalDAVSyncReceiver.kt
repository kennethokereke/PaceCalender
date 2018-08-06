package com.sammyscl.calendar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sammyscl.calendar.extensions.recheckCalDAVCalendars

class CalDAVSyncReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.recheckCalDAVCalendars {}
    }
}
