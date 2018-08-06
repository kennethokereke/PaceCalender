package com.sammyscl.calendar.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import com.sammyscl.calendar.fragmentscal.MonthFragment
import com.sammyscl.calendar.helpers.DAY_CODE
import com.sammyscl.calendar.interfaces.NavigationListener

class MyMonthPagerAdapter(fm: FragmentManager, private val mCodes: List<String>, private val mListener: NavigationListener) : FragmentStatePagerAdapter(fm) {
    private val mFragments = SparseArray<MonthFragment>()

    override fun getCount() = mCodes.size

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        val code = mCodes[position]
        bundle.putString(DAY_CODE, code)

        val fragment = MonthFragment()
        fragment.arguments = bundle
        fragment.listener = mListener

        mFragments.put(position, fragment)

        return fragment
    }

    fun updateCalendars(pos: Int) {
        for (i in -1..1) {
            mFragments[pos + i]?.updateCalendar()
        }
    }
}
