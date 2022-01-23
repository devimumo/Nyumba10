package com.example.nyumba10.Dashboard.Sos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.nyumba10.R
import com.example.nyumba10.login.Firebase_Instance_id.Get_firebase_Instanse_id
import kotlinx.android.synthetic.main.sos.*

class Sos : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sos)

        val actionBar = supportActionBar
        actionBar!!.title="SOS"
calling_permission()

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




    private fun call_member_function(phonenumber: String)
    {


        val caLl_intent= Intent(Intent.ACTION_CALL)
        caLl_intent.data= Uri.parse("tel:"+"+254"+713836954)
        startActivity(caLl_intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun calling_permission() {
        if (ActivityCompat.checkSelfPermission( this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 6)

            }
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 6)
            Toast.makeText(this,"Calling permission required", Toast.LENGTH_LONG).show()

        }
        else
        {
            call_member_function("")
        }

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            6-> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    call_member_function("")

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    android.widget.Toast.makeText(
                        this,
                        "Call permission is denied",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

                    //  finish()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}