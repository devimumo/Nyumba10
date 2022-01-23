package com.example.nyumba10

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.nyumba10.Dashboard.MyAssociation.Association_search.Association_add
import com.example.nyumba10.login.Confirm_account_activation_pin
import com.example.nyumba10.login.Login
import com.example.nyumba10.login.Profile
import com.example.nyumba10.login.RegisterActivity

class Splash : AppCompatActivity() {

    lateinit var handler: Handler
    private var confirm_account_using_otp: String="!confirmed"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        createChannel("Chat_notifications","Chat notifications")

        val actionBar = supportActionBar
        actionBar!!.hide()
        handler = Handler()
        handler.postDelayed({


            val MyPreferences = "mypref"
            val sharedPreferences =
                getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
            // String session_id= sharedPreferences.getString("sessions_ids","");


            //val profile_status = sharedPreferences.getString("profile_status", "!updated")
            val registration_status = sharedPreferences.getString("registration_status", "!registered")
            confirm_account_using_otp =
                sharedPreferences.getString("account_regstration_pin", "!confirmed").toString()
var profile_status=sharedPreferences.getString("profile_status", "!updated")
            var association_add_status=sharedPreferences.getString("association_add_status", "!updated")


            // Delay and Start Activity

            if (registration_status.equals("registered"))
            {

                if (confirm_account_using_otp.equals("!confirmed"))
                {
                    val intent = Intent(this, Confirm_account_activation_pin::class.java)
                    startActivity(intent)
                }
                else
                {

                    if (profile_status.equals("updated"))
                    {

                        if (association_add_status.equals("updated"))
                        {
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        }
                        else{
                            val intent = Intent(this, Association_add::class.java)
                            startActivity(intent)
                        }

                    }
                    else{
                        val intent = Intent(this, Profile::class.java)
                        startActivity(intent)
                    }

                }

            }
           else
            {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            finish()
        } , 2500) // here we're delaying to startActivity after 3seconds

    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance

                NotificationManager.IMPORTANCE_LOW
            )
            // TODO: Step 2.6 disable badges for this channel

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        // TODO: Step 1.6 END create channel
    }
}