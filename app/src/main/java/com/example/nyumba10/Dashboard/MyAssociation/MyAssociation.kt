package com.example.nyumba10.Dashboard.MyAssociation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.example.nyumba10.Dashboard.MyAssociation.ui.main.SectionsPagerAdapter
import com.example.nyumba10.R
import com.example.nyumba10.login.Login
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout


class MyAssociation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myassociation)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->

val cc=view.context
          //  val intent = Intent(this, Members::class.java)
           // startActivity(intent)
          /*  val manager: android.app.FragmentManager? = fragmentManager
            val transaction: android.app.FragmentTransaction? = manager.beginTransaction()
            transaction.add(R.id.container, Members(), YOUR_FRAGMENT_STRING_TAG)
            transaction.addToBackStack(null)
            transaction.commit()   */
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}