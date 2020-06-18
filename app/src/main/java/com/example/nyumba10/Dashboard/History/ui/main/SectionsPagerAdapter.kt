package com.example.nyumba10.Dashboard.History.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.nyumba10.Dashboard.History.CrimesReported
import com.example.nyumba10.Dashboard.History.Events
import com.example.nyumba10.R

private val TAB_TITLES = arrayOf(
    R.string.History1,
    R.string.History2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        when(position)
        {
            0->
            {
                return CrimesReported()
            }
            1->
            {

                return Events()
            }

            else->return CrimesReported()
        }     }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}