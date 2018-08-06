package com.sammyscl.calendar.interfaces

import android.util.SparseArray
import com.sammyscl.calendar.models.DayYearly
import java.util.*

interface YearlyCalendar {
    fun updateYearlyCalendar(events: SparseArray<ArrayList<DayYearly>>, hashCode: Int)
}
