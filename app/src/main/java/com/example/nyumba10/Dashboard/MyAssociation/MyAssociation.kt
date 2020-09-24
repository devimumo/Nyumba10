package com.example.nyumba10.Dashboard.MyAssociation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.Dashboard.MyAssociation.Association_search.Association_add
import com.example.nyumba10.Dashboard.MyAssociation.ui.main.SectionsPagerAdapter
import com.example.nyumba10.R
import com.example.nyumba10.login.Login
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_crime_data_fuul_details.*
import java.util.ArrayList


class MyAssociation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myassociation)

        val actionBar = supportActionBar
        //actionBar!!.title="My association"
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
val cc=view.context
            val intent = Intent(this, Association_add::class.java)
            startActivity(intent)

        }
    }
}