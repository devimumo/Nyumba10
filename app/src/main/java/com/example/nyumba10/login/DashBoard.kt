package com.example.nyumba10.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Admin.Admin
import com.example.nyumba10.Dashboard.History.History
import com.example.nyumba10.Dashboard.MyAccount.MyAccount
import com.example.nyumba10.Dashboard.MyAssociation.MyAssociation
import com.example.nyumba10.Dashboard.ReportCrime.ReportCrime
import com.example.nyumba10.Dashboard.Security.Security
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.Maps.Maps_activity
import com.example.nyumba10.R
import com.example.nyumba10.Security.Encrypt
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var listLatLngs_arraylist: ArrayList<LatLng> = ArrayList()
private var polygon: Polygon? =null
private var listLatLngs: ArrayList<LatLng> = ArrayList()
private var listMarkers: ArrayList<Marker> =ArrayList()
lateinit var mapFragment : SupportMapFragment
private var googleMap: GoogleMap? = null

private val COLOR_WHITE_ARGB = -0x1
private val COLOR_BLUE_LINES = -0xFC97CBD8
private val COLOR_PURPLE_ARGB = -0x7e387c
private val COLOR_ORANGE_ARGB = -0xa80e9
private val COLOR_BLUE_ARGB = -0x657db

class DashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            map_permissions()
        }
        else{
            dashboard_map()
        }






    }

    private fun get_my_association_polygon_list() {

        val MyPreferences = "mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val primary_residense_polygon_list = sharedPreferences.getString("primary_residense_polygon_list", "")
        Log.d("primary_residense",primary_residense_polygon_list)


        polygon(primary_residense_polygon_list!!)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun map_permissions() {
        if (ActivityCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)

            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            Toast.makeText(this,"Maps permission required",Toast.LENGTH_LONG).show()
            dashboard_map()

        }
        else
        {
            dashboard_map()
        }

       googleMap?.isMyLocationEnabled = true
    }


    private fun set_crime_incident_markers()
    {
        val requestQueue = Volley.newRequestQueue(this)
        val login_url="https://daudi.azurewebsites.net/nyumbakumi/get_crime_data/get_crimes.php";
        val stringRequest: StringRequest = object : StringRequest(Method.POST, login_url, Response.Listener { response ->


            var response_json_object=JSONObject(response)
            var response=response_json_object.getString("response")

            if (response.equals("successful")) {

                var crime_data=response_json_object.getString("data")
                var crime_data_jsonobject=JSONObject(crime_data)
                var crime_data_array=crime_data_jsonobject.getJSONArray("crime_data")
                Log.d("crime_data_array",crime_data_array.toString()+"---"+crime_data_array.length())



                for (i in 0..crime_data_array.length() - 1) {

                    var crime_data_array_jsonobjects=crime_data_array.getJSONObject(i)
                    var id_no=crime_data_array_jsonobjects.getString("id_no")
                    var mobile_no=crime_data_array_jsonobjects.getString("mobile_no")
                    var crime_time_and_date_value=crime_data_array_jsonobjects.getString("crime_time_and_date_value")
                    var ccte=crime_time_and_date_value.replace("\"","")


                    var listLatLng_todb=crime_data_array_jsonobjects.getString("listLatLng_todb")
                   var  listLatLng_todb_array=JSONArray(listLatLng_todb)
                    var lat=getlatlong(listLatLng_todb_array,"lat")
                    var long=getlatlong(listLatLng_todb_array,"long")
                     var marker_location=LatLng(lat.toDouble(),long.toDouble())
                    var crime_description=crime_data_array_jsonobjects.getString("crime_description")
                    var incident_date=crime_data_array_jsonobjects.getString("incident_date")
                    var incident_time=crime_data_array_jsonobjects.getString("incident_time")

var time_d=incident_date+"--"+incident_time
                    Log.d("lat_looong",ccte.toString())

                    set_marker(ccte,marker_location,crime_description,time_d)


                }
            }

            }, Response.ErrorListener {


                val err= Volley_ErrorListener_Handler()

              //  err.check_error(it,view)


            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                val encrypt = Encrypt()
                val MyPreferences = "mypref"
                val sharedPreferences =
                    getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                // String session_id= sharedPreferences.getString("sessions_ids","");

             //   params["session_ids"] = session_idss!!

                return params
            }
        }
        requestQueue.add(stringRequest)

    }

    private fun getlatlong(listlang: JSONArray,name: String): String
    {

           var latlong_jsonobject=listlang.getJSONObject(0)
        var latlong=latlong_jsonobject.getString(name)




        return latlong
    }

    private fun set_marker(crimeTimeAndDateValue: String, markerLocation: LatLng, crimeDescription: String, timeD: String)
    {
         //var time_value=SimpleDateFormat("yyyyMMddHHmmss")
        var time_value=SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

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
           crime_happened_duration=days.toString()+" days"
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



            val markerOptions =MarkerOptions().snippet(crimeDescription).title(crime_happened_duration).position(markerLocation)

            val map_marker = googleMap?.addMarker(markerOptions)
        map_marker?.tag=markerLocation
        map_marker?.showInfoWindow()

    }

    fun dashboard_map()
    {



        mapFragment = supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL;
            get_my_association_polygon_list()

            googleMap?.setOnMapLoadedCallback {


                Toast.makeText(this,"Maps loaded",Toast.LENGTH_LONG).show()
            }
            //  googleMap?.isMyLocationEnabled = true
            googleMap?.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener() {

                var tag_marker=it.tag
                Log.d("tag_marker",tag_marker.toString())
                return@OnMarkerClickListener false
            });


        })



    }

//function for showing my association polygon
    fun polygon( listLatLngs: String) {
        var listLatLngs = listLatLngs.replace("\\", "")

        Log.d("listteee", listLatLngs)
        var data = JSONArray(listLatLngs)
        //   val data = jsonObject.getJSONArray("contribution_statement")


        for (i in 0..data.length() - 1) {

            var langlot_values: JSONObject = data[i] as JSONObject
            var lat = langlot_values.getString("lat")
            var long = langlot_values.getString("long")

            listLatLngs_arraylist.add(LatLng(lat.toDouble(), long.toDouble()))

        }

        var focus_location = listLatLngs_arraylist[0]


    set_crime_incident_markers()


    polygon?.remove()

    var fill=0x2035BFE4.toInt();
    val fillColor = COLOR_BLUE_LINES
    val strokecolor = 0xFF008577.toInt()
        val polygonoptions: PolygonOptions =
            PolygonOptions().addAll(listLatLngs_arraylist).clickable(true).strokeWidth(2F).fillColor(
                fill
            ).strokeColor(strokecolor)
        polygon =googleMap?.addPolygon(polygonoptions)

     //   polygon?.strokeColor = strokecolor
   // polygon?.fillColor= fillColor

    googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(focus_location, 16f))


    }

    override fun onBackPressed() {



        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want exit")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                   // super.onBackPressed()

                    val intent = Intent(this@DashBoard, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("EXIT", true)
                    startActivity(intent)
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog

                })
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }
    fun Card_click(view: View) {


        when (view.id)
        {
            R.id.my_association->{


                val intent= Intent(this,MyAssociation::class.java)
                startActivity(intent)
            }

            R.id.sos->{
                //    val intent= Intent(this,::class.java)
                //  startActivity(intent)
            }

            R.id.my_account->{

                val intent= Intent(this,MyAccount::class.java)
                startActivity(intent)
            }

            R.id.report_crime->{

                val intent= Intent(this,ReportCrime::class.java)
                startActivity(intent)
            }

            R.id.chat->{

                val intent= Intent(this,Maps_activity::class.java)
                startActivity(intent)
            }

            R.id.history->{

                val intent= Intent(this,History::class.java)
                startActivity(intent)
            }

            R.id.admin->{

                val intent= Intent(this,Admin::class.java)
                startActivity(intent)
            }

            R.id.security->{

                val intent= Intent(this,Security::class.java)
                startActivity(intent)
            }

        }

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            2 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    googleMap?.isMyLocationEnabled = true
                    dashboard_map()


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    android.widget.Toast.makeText(
                        this,
                        "Location permission is needed",
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