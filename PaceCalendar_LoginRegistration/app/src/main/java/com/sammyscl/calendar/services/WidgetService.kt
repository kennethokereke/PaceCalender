package com.sammyscl.calendar.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.sammyscl.calendar.adapters.EventListWidgetAdapter

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = EventListWidgetAdapter(applicationContext)
}
