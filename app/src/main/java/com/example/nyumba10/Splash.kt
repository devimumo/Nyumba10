package com.example.nyumba10

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.nyumba10.login.Login
import com.example.nyumba10.login.Profile
import com.example.nyumba10.login.RegisterActivity

class Splash : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        handler = Handler()
        handler.postDelayed({


            val MyPreferences = "mypref"
            val sharedPreferences =
                getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
            // String session_id= sharedPreferences.getString("sessions_ids","");


            //val profile_status = sharedPreferences.getString("profile_status", "!updated")
            val registration_status = sharedPreferences.getString("registration_status", "!registered")

            // Delay and Start Activity

            if (registration_status.equals("registered"))
            {

                val intent = Intent(this, Login::class.java)
                startActivity(intent)


            }
           else
            {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            finish()
        } , 4500) // here we're delaying to startActivity after 3seconds

    }
}