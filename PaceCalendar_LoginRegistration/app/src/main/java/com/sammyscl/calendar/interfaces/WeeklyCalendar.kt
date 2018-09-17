package com.sammyscl.calendar.interfaces

import com.sammyscl.calendar.models.Event

interface WeeklyCalendar {
    fun updateWeeklyCalendar(events: ArrayList<Event>)
}
