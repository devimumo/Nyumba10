package com.example.nyumba10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.nyumba10.login.Login

class Splash : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        handler = Handler()
        handler.postDelayed({

            // Delay and Start Activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        } , 45) // here we're delaying to startActivity after 3seconds

    }
}