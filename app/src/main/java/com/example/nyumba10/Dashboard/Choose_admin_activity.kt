package com.example.nyumba10.Dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nyumba10.Dashboard.Maps.Maps_activity
import com.example.nyumba10.R
import kotlinx.android.synthetic.main.choose_admin_activity.*

class Choose_admin_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_admin_activity)

        button3.setOnClickListener {
            var intent= Intent(this, Events::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            var intent=Intent(this, Maps_activity::class.java)
            startActivity(intent)
        }
    }
}