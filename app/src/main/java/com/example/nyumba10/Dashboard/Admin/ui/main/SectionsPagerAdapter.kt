package com.example.nyumba10.Dashboard.Admin.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.nyumba10.Dashboard.Admin.Incidences_Crimes
import com.example.nyumba10.Dashboard.MyAssociation.Members
import com.example.nyumba10.R

private val TAB_TITLES = arrayOf(
    R.string.Admin1,
    R.string.Admin2
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
                return Incidences_Crimes()
            }
            1->
            {

                return Members()
            }

            else->return Members()
        }     }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}