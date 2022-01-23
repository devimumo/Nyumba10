package com.example.nyumba10.Dashboard.Admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_crime_data_fuul_details.*
import kotlinx.android.synthetic.main.members_data_recyclerview_layout.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class Crime_data_fuul_details : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private  var time=""
    private  var location_description=""
    private  var time_from_crime_data=""
    private var mobile_no_value=""

    private lateinit var latlong: LatLng
    private  var incident_type=""
    private var crime_arraylist_tosend_to_full_details_acitivity: List<crime_incidences_data_class> = ArrayList<crime_incidences_data_class>()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_data_fuul_details)

        var intent=intent
        var crime_data=
            change_to_crime_incidences_data_class(intent.getStringExtra("crime_incidences_data_to_json_string_to_send")!!)
        if (!crime_data.equals("")) {

            set_data_to_view(crime_data)
        } else {

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        imageView4.setOnClickListener {
calling_permission()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var title=incident_type+"  "+crime_happened_duration(time_from_crime_data)

var marker=MarkerOptions().title(incident_type).position(latlong)

       var map_marker_set= mMap.addMarker(marker)
        map_marker_set.showInfoWindow()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled=true
        mMap.uiSettings.isCompassEnabled=true
        mMap.uiSettings.isZoomControlsEnabled=true

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong,16f))
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
      //  mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
     //   mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

private fun set_data_to_view(crime_data: crime_incidences_data_class)
{
    time=get_time(crime_data.time)
    time_from_crime_data=crime_data.time
    latlong=get_latlong(crime_data.latong)
    incident_type=crime_data.incident_type
    location_description=crime_data.location_description
    mobile_no_value=crime_data.mobile_no

    if (mobile_no_value.length>5)
    {

    }
    else{

    }
    incident_type_textview.text=incident_type
    crime_description_textview.text=crime_data.crime_description
    time_textview.text=time
    location_description_textview.text=location_description
}

    private fun crime_happened_duration(crimeTimeAndDateValue: String):String
    {


        var time_value= SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

        val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        var timev=simpleDateFormat.parse(time_value)
        var crimev=simpleDateFormat.parse(crimeTimeAndDateValue)

        var differe=timev.time-crimev.time
        var days=differe/(1000*60*60*24)
        var hour=(differe-(1000*60*60*24*days))/(1000*60*60)
        var mins=(differe - (1000*60*60*24*days) - (1000*60*60*hour)) / (1000*60);

        // Log.d("differe",timev.time.toString())

        Log.d("time_difference","days: "+days.toString()+"----hours:"+hour.toString()+"--mins:"+mins+"--"+timev+"--"+crimev)

        var crime_happened_duration=""

        if (days>0)
        {

            if (days.toInt()==1)
            {
                crime_happened_duration=days.toString()+" day"

            }
            else{
                crime_happened_duration=days.toString()+" days"

            }
        }
        else
        {
            if (hour>1)
            {
                crime_happened_duration=hour.toString()+" hours"

            }
            else
            {
                crime_happened_duration=mins.toString()+" mins"

            }
        }

        return  crime_happened_duration
    }

    private fun getlatlong(listlang: JSONArray, name: String): String
    {

        var latlong_jsonobject=listlang.getJSONObject(0)
        var latlong=latlong_jsonobject.getString(name)

        return latlong
    }

    private fun get_latlong(listLatLng_todb: String): com.google.android.gms.maps.model.LatLng
    {
        var  listLatLng_todb_array= JSONArray(listLatLng_todb)

        var lat=getlatlong(listLatLng_todb_array,"lat")
        var long=getlatlong(listLatLng_todb_array,"long")
        var marker_location=com.google.android.gms.maps.model.LatLng(lat.toDouble(), long.toDouble())


        return marker_location
    }

    private fun get_time(time: String): String
    {
        var time_changed=""

        try {

            var simple_date_fomart= SimpleDateFormat("yyyyMMddHHmmss")
            var date=simple_date_fomart.parse(time)
            var new_date_fomart= SimpleDateFormat("dd-MM-yyyy HH:mm a")

            time_changed=new_date_fomart.format(date)

        }catch (e: Exception)
        {
            var time_changed=time

        }

        return  time_changed
    }


    private fun change_to_crime_incidences_data_class(data:String): crime_incidences_data_class
    {

        var data_fro_marker=
            Gson().fromJson<crime_incidences_data_class>(data,crime_incidences_data_class::class.java)
        //  Log.d("data_fro_marker",data_fro_marker.toString())
        return  data_fro_marker
    }


    private fun call_member_function(phonenumber: String)
    {


        val caLl_intent= Intent(Intent.ACTION_CALL)
        caLl_intent.data= Uri.parse("tel:"+"+254"+mobile_no_value)
        startActivity(caLl_intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun calling_permission() {
        if (ActivityCompat.checkSelfPermission( this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {

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