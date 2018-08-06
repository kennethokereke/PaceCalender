package com.sammyscl.calendar.interfaces

import android.content.Context
import com.sammyscl.calendar.models.DayMonthly

interface MonthlyCalendar {
    fun updateMonthlyCalendar(context: Context, month: String, days: ArrayList<DayMonthly>, checkedEvents: Boolean)
}
