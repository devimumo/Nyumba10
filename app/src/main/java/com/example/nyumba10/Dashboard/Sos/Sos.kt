package com.example.nyumba10.Dashboard.Sos

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nyumba10.R
import com.example.nyumba10.login.Firebase_Instance_id.Get_firebase_Instanse_id
import kotlinx.android.synthetic.main.sos.*

class Sos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sos)

        val actionBar = supportActionBar
        actionBar!!.title="SOS"


instanse_id.setOnClickListener {

    get_instanse_id()
}

    }

    fun get_instanse_id()
    {
        val MyPreferences="mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)


        var id_no=sharedPreferences.getString("id_no","")
        Get_firebase_Instanse_id().get_instanse_id(id_no!!,this)

    }
}