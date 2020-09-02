package com.example.nyumba10.Dashboard.ReportCrime

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.login.DashBoard
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.crime_info_layout.view.*
import kotlinx.android.synthetic.main.reportcrime.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var listLatLng_todb: ArrayList<String>? = ArrayList()
var current_location: Location? =null

private lateinit var fusedLocationClient: FusedLocationProviderClient
private var listMarkers: ArrayList<Marker> =ArrayList()

private var listLatLngs_arraylist: ArrayList<LatLng> = ArrayList()
private var polygon: Polygon? =null
private var listLatLngs: ArrayList<LatLng> = ArrayList()
lateinit var mapFragment : SupportMapFragment
var googleMap: GoogleMap? = null
private var my_location: LatLng?=null
private var access_fine_location_code = 1
private var access_fine_location_name = "Access fine location"
private var incident_type_value_from_radio="crime"

private var crime_time_and_date_value = ""
private var crime_time_value = ""
private var crime_date_value = ""
private var location_description=""
private var crime_time_text_value = ""
private var crime_date_text_value = ""
private var location_permission=Manifest.permission.ACCESS_FINE_LOCATION

private val COLOR_WHITE_ARGB = -0x1
private val COLOR_BLUE_LINES = -0xFC97CBD8
private val COLOR_PURPLE_ARGB = -0x7e387c
private val COLOR_ORANGE_ARGB = -0xa80e9
private val COLOR_BLUE_ARGB = -0x657db

class ReportCrime : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reportcrime)

        val actionBar = supportActionBar
        actionBar!!.title="Report Crime"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        map_permissions(access_fine_location_name, access_fine_location_code, location_permission)
        set_default_time_values()
        get_last_location()


        mapFragment = supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
         mapFragment.getMapAsync {
             googleMap=it
          googleMap?.setOnMapClickListener {

        googleMap?.clear()
        listMarkers.clear()
        listLatLng_todb?.clear()

        get_my_association_polygon_list("clicked")

        Toast.makeText(this,"iko hapa",Toast.LENGTH_LONG).show()
        val markerOptions = MarkerOptions().title("title").snippet("Crime 3h").position(it)

        val marker = googleMap?.addMarker(markerOptions)
        marker?.showInfoWindow()
        val latlongjsonobject = JSONObject()
        //  listLatLngs.add(it)

        var lat_t=it.latitude
        var long_t=it.longitude
        latlongjsonobject.put("lat",lat_t)
        latlongjsonobject.put("long",long_t)

        listLatLng_todb?.add(latlongjsonobject.toString())
        val markers_location=LatLng(lat_t,long_t)
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(markers_location, 18f))
        // var ttr=JSONArray(listLatLngs).toString()
        Log.d("listLatLngs",listLatLng_todb.toString())
        listMarkers.add(marker!!)
    }

}

    }


    /*public override fun onBackPressed() {
        super.onBackPressed()
        var intent=Intent(this,DashBoard::class.java)
        startActivity(intent)
    }*/
    private fun set_default_time_values() {

        crime_time_text_value = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date()).toString()
        crime_date_text_value=SimpleDateFormat(" dd ,MM, yyyy", Locale.getDefault()).format(Date()).toString()
        incident_time.text= crime_time_text_value
        incident_date.text= crime_date_text_value

        crime_time_value = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date()).toString()
        crime_date_value = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toString()
        crime_time_and_date_value= crime_date_value+ crime_time_value
        Log.d("crime_time_and_date", crime_time_and_date_value)

    }

    private fun get_my_association_polygon_list(state: String) {


        val MyPreferences = "mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");


        val primary_residense_polygon_list = sharedPreferences.getString("primary_residense_polygon_list", "")
        Log.d("primary_residense",primary_residense_polygon_list!!)


        polygon(primary_residense_polygon_list!!,state)

    }

private fun show_request_permission_dialog(    name: String,    requestCode: Int,    permission: String)
{


    val builder = AlertDialog.Builder(this)
    builder.setMessage("Do you want to grant "+name+" this app")
        .setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog, id ->
           ActivityCompat.requestPermissions(this@ReportCrime, arrayOf(permission),requestCode)
            })
        .setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, id ->
                // User cancelled the dialog
            })
    // Create the AlertDialog object and return it
    builder.create()
    builder.show()

}

    @RequiresApi(Build.VERSION_CODES.M)
    private fun map_permissions(name: String,request_code: Int,permission: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            when {
                ContextCompat.checkSelfPermission( this, permission) == PackageManager.PERMISSION_GRANTED -> {
                    googleMap?.isMyLocationEnabled = true
                   // snack_bar("",ReportCrime.)
                    android.widget.Toast.makeText(this,"Location permission is granted",android.widget.Toast.LENGTH_SHORT).show()

                    dashboard_map("unchecked")
                }

                shouldShowRequestPermissionRationale(permission) -> show_request_permission_dialog(name,request_code,permission)

                else -> {
                    ActivityCompat.requestPermissions(this,arrayOf(permission), request_code)

                }
            }
        }
        else
        {
            googleMap?.isMyLocationEnabled = true

            dashboard_map("unchecked")
        }

    }


   private fun dashboard_map(checked: String)
    {

        if (!checked.equals("checked")) {

            mapFragment =
                supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
            mapFragment.getMapAsync(OnMapReadyCallback {
                googleMap = it

                googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL;
             //   googleMap?.isMyLocationEnabled = true

                get_my_association_polygon_list("opening")

            })
        }
        else
        {

            mapFragment =
                supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
            mapFragment.getMapAsync(OnMapReadyCallback {
                googleMap = it

                googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL;
                //googleMap?.isMyLocationEnabled = true
                get_last_location()

            })


    }
    }

    private fun get_last_location() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                }
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 3)
            }
            else
            {                get_last_fused_location()            }

        }        else        {           get_last_fused_location()        }



    }

    @SuppressLint("MissingPermission")
    fun get_last_fused_location():LatLng?
    {
        googleMap?.isMyLocationEnabled = true

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@ReportCrime)

        fusedLocationClient.lastLocation?.addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                current_location = location
                if (current_location != null) {
                    val latitude = current_location?.latitude
                    val longitude = current_location?.longitude

                    googleMap?.isMyLocationEnabled = true

                    // val markerOptions = MarkerOptions().snippet("This is your location").icon(BitmapDescriptorFactory.fromResource(R.drawable.robber)).title("crime").position(LatLng(
                    val location_acquired=LatLng(latitude!!, longitude!!)

                    val markerOptions = MarkerOptions().snippet("This is your location").title("").position(location_acquired)

                    val map_marker= googleMap?.addMarker(markerOptions)
                    map_marker?.showInfoWindow()

                    val latlongjsonobject = JSONObject()

                    listLatLng_todb?.clear()
                    latlongjsonobject.put("lat",latitude)
                    latlongjsonobject.put("long",longitude)

                    listLatLng_todb?.add(latlongjsonobject.toString())

                     my_location=LatLng(latitude,longitude)



                } else {

                    Toast.makeText(this,"You location not acquired.Please enable location settings".toString(), Toast.LENGTH_LONG).show()
                }

            }
Log.d("my_location",my_location.toString())
        return my_location

    }

  private  fun date_picker()
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val time=c.get(Calendar.HOUR_OF_DAY)





        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in TextView
            var date_today = (monthOfYear+1).toString()

if (monthOfYear<10)
{

    date_today="0"+date_today
   // incident_date.setText("" + dayOfMonth + ", " + date_today + ", " + year)


   // crime_date_value= (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()



    if (dayOfMonth<10)
    {
        incident_date.setText("" + "0"+dayOfMonth + ", " + date_today + ", " + year)

        crime_date_value= (year.toString()+date_today.toString()+"0"+dayOfMonth.toString()).toString()

    }
    else
    {
        incident_date.setText("" + dayOfMonth + ", " + date_today + ", " + year)

        crime_date_value= (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()

    }

}
            else{
if (dayOfMonth<10)
{
    crime_date_value= (year.toString()+date_today.toString()+"0"+dayOfMonth.toString()).toString()

}
    else
{
    crime_date_value= (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()

}
}
            crime_time_and_date_value=crime_date_value.toString()+crime_time_value
            Log.d("crime_time_and_datess", crime_time_and_date_value.toString())

        }, year, month, day)

        //calcuate in milliseconds the number of days-minimum days the date picke should show
        var mindate_value=(c.timeInMillis-(10*24*60*60*1000)).toString()

        dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpd.getDatePicker().setMinDate(mindate_value.toLong());

        dpd.show()
    }

   private fun time_picker(this_view: View)
    {
        val c = Calendar.getInstance()


        val time_pick=TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val currentTime_hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
            val currentTime_minute = SimpleDateFormat("mm", Locale.getDefault()).format(Date())

            c.set(Calendar.HOUR_OF_DAY,hourOfDay)
            c.set(Calendar.MINUTE,minute)
            view.setIs24HourView(true)



            if (currentTime_hour.toInt()<hourOfDay)
            {
              //  Toast.makeText(this,"Time cannot be beyond now! Try again",Toast.LENGTH_LONG).show()
                Snackbar.make(this_view, "Choose the correct time.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                     time_picker(view)
            }
            else
            {
                if (currentTime_minute.toInt()<minute)
                {
                  //  Toast.makeText(this,"Time cannot be beyond now! Try again",Toast.LENGTH_LONG).show()
                    Snackbar.make(this_view, "Choose the correct time.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                      time_picker(view)
                }
                else
                {


                    incident_time.text=SimpleDateFormat("HH:mm:a").format(c.time)

                    var secs=10
                  //  crime_time_value =(hourOfDay.toString()+minute.toString()+secs.toString()).toString()
                    crime_time_and_date_value= crime_date_value.toString()+SimpleDateFormat("HHmmss").format(c.time).toString()

                    Log.d("crime_time_and_date", crime_time_and_date_value)

                }
            }




        }

        //val milli=((hhd*3600)+52*60*)

TimePickerDialog(this,time_pick,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show()


        }


    //function for showing my association polygon
    fun polygon( listLatLngs: String,state: String) {
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

       polygon?.remove()

        var fill=0x2035BFE4.toInt();
        val fillColor = COLOR_BLUE_LINES
        val strokecolor = 0xFF008577.toInt()
        val polygonoptions: PolygonOptions =
            PolygonOptions().addAll(listLatLngs_arraylist).clickable(true).strokeWidth(2F).strokeColor(strokecolor)
       polygon = googleMap?.addPolygon(polygonoptions)


        val currentPlace = CameraPosition.Builder()
            .target(focus_location)
            .tilt(35.5f).zoom(15.5f).build()

        googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
       // googleMap?.animateCamera( CameraUpdateFactory.newLatLngZoom(focus_location, 16f))

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1-> {

                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the

                   googleMap?.isMyLocationEnabled = true
                       dashboard_map("checked")
                } else {

                  android.widget.Toast.makeText( this,"Location permission is needed", android.widget.Toast.LENGTH_SHORT).show()

                }
                return
            }
            3->{

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    googleMap?.isMyLocationEnabled = true
                   get_last_fused_location()

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    android.widget.Toast.makeText(
                        this,
                        "Location permission is needed",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

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

    fun check_box(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.use_my_location -> {
               if(checked)
               {
                   animate_camera_to_my_location()

               }else
               {
                   dashboard_map("unchecked")

               }

                }

                R.id.anonimously -> {


                }

            }
        }
    }

    fun click(view: View) {

        when(view.id){

            R.id.get_date->{
                date_picker()
            }

            R.id.time_picking->{
                time_picker(view)
            }
            R.id.report->
            {
                report_crime_volley(view)
            }
        }


    }

    private fun animate_camera_to_my_location()
    {
        var location= get_last_fused_location()
        if (location==null)
        {
            Toast.makeText(this,"Your location not acquired",Toast.LENGTH_LONG).show()
        }
        else
        {
            googleMap?.clear()
            listMarkers.clear()
            listLatLng_todb?.clear()

            get_my_association_polygon_list("clicked")
              val markerOptions = MarkerOptions().snippet("This is your location").title("").position(location!!)

              val map_marker= googleMap?.addMarker(markerOptions)
              map_marker?.showInfoWindow()
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(location, 18f))
        }

    }


    private fun report_crime_volley(view: View) {
        login_progressBar.visibility= View.VISIBLE
        report.visibility= View.GONE

        val requestQueue= Volley.newRequestQueue(this)
        val url="https://daudi.azurewebsites.net/nyumbakumi/report_crime/report_crime.php"
        val stringRequest=object : StringRequest(Request.Method.POST,url, Response.Listener { response->

            Log.d("response", response)
            val jsonObject = JSONObject(response)
            val response = jsonObject.getString("response")
if (response.equals("successful"))
{

    login_progressBar.visibility= View.GONE
    report.visibility= View.VISIBLE
    snack_bar("crime reported successfully",view)

    reset_map(listLatLng_todb.toString())
}
            else
{

    login_progressBar.visibility= View.GONE
    report.visibility= View.VISIBLE
    snack_bar("crime not reported .Please try again",view)
}

        }, Response.ErrorListener {


            login_progressBar.visibility= View.GONE
            report.visibility= View.VISIBLE
            val error_handler= Volley_ErrorListener_Handler()
            error_handler.check_error(it,view)

        })
        {

            override fun getParams(): MutableMap<String, String> {
                val params:MutableMap<String,String > =HashMap()

                val MyPreferences = "mypref"
                val sharedPreferences = view.context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                 var session_id= sharedPreferences.getString("sessions_ids","");


                val id_no = sharedPreferences.getString("id_no", "")
                val association_id = sharedPreferences.getString("association_id", "")
                val phone_number = sharedPreferences.getString("phone_number", "")

               if (anonimously.isChecked)
               {
                   params.put("id_no","anonimous")
                   params.put("phone_number","0")
                   params.put("association_id",association_id!!)
                   params.put("crime_time_and_date_value", crime_time_and_date_value.toString())
                   params.put("crime_description", crime_description.text.toString())
                   params.put("incident_date", crime_date_value.toString())
                   params.put("incident_time", incident_time.text.toString())
                   params.put("incident_type", incident_type_value_from_radio)
                   params.put("location_description", location_description)


                   if (listLatLng_todb.isNullOrEmpty())
                   {

                       snack_bar("knaungua hapa",view)

                       params.put("listLatLng_todb", get_last_location().toString())

                   }
                   else
                   {
                       params.put("listLatLng_todb", listLatLng_todb.toString())

                   }
               }
                else
               {
                   params.put("id_no",id_no.toString())
                   params.put("phone_number",phone_number!!)
                   params.put("association_id",association_id!!)
                   params.put("crime_time_and_date_value", crime_time_and_date_value.toString())
                   params.put("crime_description", crime_description.text.toString())
                   params.put("incident_date", crime_date_value.toString())
                   params.put("incident_time", incident_time.text.toString())
                   params.put("incident_type", incident_type_value_from_radio)
                   params.put("location_description", location_description)


                   if (listLatLng_todb.isNullOrEmpty())
                      {
                       params.put("listLatLng_todb", get_last_location().toString())

                   }
                   else
                   {
                       params.put("listLatLng_todb", listLatLng_todb.toString())

                   }

                   Log.d("params",params.toString())

               }



                return  params
            }


        }
        requestQueue.add(stringRequest)
    }

    private fun reset_map(last_location: String) {


        var json_array=JSONArray(last_location)

       var jsonObject= json_array.getJSONObject(0)
        var lat=jsonObject.getString("lat").toDouble()
        var long=jsonObject.getString("long").toDouble()
var location=LatLng(lat,long)


        val markerOptions = MarkerOptions().snippet(crime_description.text.toString()).title("crime").position(location)

        val map_marker= googleMap?.addMarker(markerOptions)
        map_marker?.showInfoWindow()

        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location, 17f))

    }


    private fun get_crime_info_alert_dialog()
    {
        val builder = AlertDialog.Builder(this)
        // Get the layout inflater
        val inflater: LayoutInflater = LayoutInflater.from(this)
        var inflated=inflater.inflate(R.layout.crime_info_layout, null)
       builder.setView(inflated)


builder.setPositiveButton("Okay",
    DialogInterface.OnClickListener { dialog, id ->
        var text_crime_description_radio=inflated.crime_description_radio.text
        location_description=inflated.location_description.text.toString()
        var checked_incident_type=inflated.incident_type_radio
        crime_description.text=text_crime_description_radio
    })
builder.setCancelable(true)



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
            // Add action buttons

        builder.create()
        builder.show()
    }

    fun snack_bar(string: String?, view: View) {
        val mysnackbar = Snackbar.make(view, string!!, Snackbar.LENGTH_LONG)
        mysnackbar.show()
    }

    fun onclick(view: View) {

        when(view.id){
            R.id.crime_description->{
                get_crime_info_alert_dialog()
            }
        }
    }

    fun crime_info_onclick(view: View) {
        if (view is RadioButton) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.crime_radio -> {
                    if(checked)
                    {
           incident_type_value_from_radio="crime"
                    }

                }
                R.id.suspicious_radio -> {
                    if(checked)
                    {

                        incident_type_value_from_radio="suspicious"

                    }

                }
                R.id.safety_radio -> {
                    if(checked)
                    {

                        incident_type_value_from_radio="safety"

                    }

                }


            }
        }


    }

    private fun change_time(): String
    {
        var time_changed=""



        return  time_changed

    }


}

