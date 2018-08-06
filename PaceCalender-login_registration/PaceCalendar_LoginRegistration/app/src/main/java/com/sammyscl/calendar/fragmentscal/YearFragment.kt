package com.sammyscl.calendar.fragmentscal

import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sammyscl.R

import com.sammyscl.calendar.activities.MainActivityCal
import com.sammyscl.calendar.extensions.config
import com.sammyscl.calendar.helpers.YEAR_LABEL
import com.sammyscl.calendar.helpers.YearlyCalendarImpl
import com.sammyscl.calendar.interfaces.YearlyCalendar
import com.sammyscl.calendar.models.DayYearly
import com.sammyscl.calendar.views.SmallMonthView
import com.simplemobiletools.commons.extensions.getAdjustedPrimaryColor
import com.simplemobiletools.commons.extensions.updateTextColors
import kotlinx.android.synthetic.main.fragment_year.view.*
import org.joda.time.DateTime
import java.util.*

class YearFragment : Fragment(), YearlyCalendar {
    private var mYear = 0
    private var mSundayFirst = false
    private var lastHash = 0
    private var mCalendar: YearlyCalendarImpl? = null

    lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_year, container, false)
        mYear = arguments!!.getInt(YEAR_LABEL)
        context!!.updateTextColors(mView.calendar_holder)
        setupMonths()

        mCalendar = YearlyCalendarImpl(this, context!!, mYear)

        return mView
    }

    override fun onPause() {
        super.onPause()
        mSundayFirst = context!!.config.isSundayFirst
    }

    override fun onResume() {
        super.onResume()
        val sundayFirst = context!!.config.isSundayFirst
        if (sundayFirst != mSundayFirst) {
            mSundayFirst = sundayFirst
            setupMonths()
        }
        updateCalendar()
    }

    fun updateCalendar() {
        mCalendar?.getEvents(mYear)
    }

    private fun setupMonths() {
        val dateTime = DateTime().withDate(mYear, 2, 1).withHourOfDay(12)
        val days = dateTime.dayOfMonth().maximumValue
        mView.month_2.setDays(days)

        val res = resources
        markCurrentMonth(res)

        for (i in 1..12) {
            val monthView = mView.findViewById<SmallMonthView>(res.getIdentifier("month_" + i, "id", context!!.packageName))
            var dayOfWeek = dateTime.withMonthOfYear(i).dayOfWeek().get()
            if (!mSundayFirst) {
                dayOfWeek--
            }

            monthView.firstDay = dayOfWeek
            monthView.setOnClickListener {
                (activity as MainActivityCal).openMonthFromYearly(DateTime().withDate(mYear, i, 1))
            }
        }
    }

    private fun markCurrentMonth(res: Resources) {
        val now = DateTime()
        if (now.year == mYear) {
            val monthLabel = mView.findViewById<TextView>(res.getIdentifier("month_${now.monthOfYear}_label", "id", context!!.packageName))
            monthLabel.setTextColor(context!!.getAdjustedPrimaryColor())

            val monthView = mView.findViewById<SmallMonthView>(res.getIdentifier("month_${now.monthOfYear}", "id", context!!.packageName))
            monthView.todaysId = now.dayOfMonth
        }
    }

    override fun updateYearlyCalendar(events: SparseArray<ArrayList<DayYearly>>, hashCode: Int) {
        if (!isAdded)
            return

        if (hashCode == lastHash) {
            return
        }

        lastHash = hashCode
        val res = resources
        for (i in 1..12) {
            val monthView = mView.findViewById<SmallMonthView>(res.getIdentifier("month_$i", "id", context!!.packageName))
            monthView.setEvents(events.get(i))
        }
    }
}
