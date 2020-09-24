package com.example.nyumba10.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Admin.Admin
import com.example.nyumba10.Dashboard.Admin.Crime_data_fuul_details
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.Dashboard.GroupChat.GroupChat
import com.example.nyumba10.Dashboard.History.History
import com.example.nyumba10.Dashboard.MyAccount.MyAccount
import com.example.nyumba10.Dashboard.MyAssociation.MyAssociation
import com.example.nyumba10.Dashboard.ReportCrime.ReportCrime
import com.example.nyumba10.Dashboard.Security.Security
import com.example.nyumba10.Dashboard.Sos.Sos
import com.example.nyumba10.Dashboard.Stats.Statistics
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.Security.Encrypt
import com.example.nyumba10.Worker.get_crime_data_network_request
import com.example.nyumba10.login.ViewModels.Dashboard_viewmodel
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dashboard.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var listLatLngs_arraylist: ArrayList<LatLng> = ArrayList()
private var polygon: Polygon? =null
private var listLatLngs: ArrayList<LatLng> = ArrayList()
private var listMarkers: ArrayList<Marker> =ArrayList()
lateinit var mapFragment : SupportMapFragment
private var googleMap: GoogleMap? = null
private var get_crime_data_network_request_instanse=get_crime_data_network_request()
private val COLOR_WHITE_ARGB = -0x1
private val COLOR_BLUE_LINES = -0xFC97CBD8
private val COLOR_PURPLE_ARGB = -0x7e387c
private val COLOR_ORANGE_ARGB = -0xa80e9
private val COLOR_BLUE_ARGB = -0x657db
private val crime_data_arraylist = ArrayList<crime_incidences_data_class>()
private var from_time_value: String="all"
private var to_time_value: String="all"
private var incident_type: String="all"

var rootView_dashboard: View? = null
private lateinit var polygon_list: String
class DashBoard : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        val actionBar = supportActionBar
       // actionBar!!.hide()

        val MyPreferences = "mypref"
        var sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

        var designation=sharedPreferences.getString("designation","MEMBER").toString()




        var dashboardViewmodel=ViewModelProvider(this).get(Dashboard_viewmodel::class.java)
        polygon_list= sharedPreferences.getString("primary_residense_polygon_list","").toString()
        if (polygon_list.isEmpty())
        {

            CoroutineScope(Dispatchers.IO).launch {

                //   dashboardViewmodel.get_polygon_list_volley(application)
            }
          //  dashboardViewmodel.get_polygon_list_volley(application)
        }
        else
        {
            dashboardViewmodel.set_list(polygon_list)
        }
        Log.d("primary_residense_", polygon_list)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        if (designation.equals("MEMBER"))
        {
            admin.visibility=View.GONE
            security.visibility=View.GONE
        }
        rootView_dashboard = window.decorView.rootView

//check if location permissions is enabled for devices over android version M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            map_permissions()
        }
        else{
            set_crime_incident_markers()
            dashboard_map()
        }
    }

    fun onMarkerClick(marker: Marker) {

        var marker_tag = marker.tag as String?

        val intent = Intent(rootView_dashboard!!.context, Crime_data_fuul_details::class.java)


        //  bundle.putSerializable("user_data", crime_arraylist_tosend_to_full_details_acitivity as Serializable)
        intent.putExtra("crime_incidences_data_to_json_string_to_send",marker_tag)
        startActivity(intent)
        Log.d("marker_tag_value",change_to_crime_incidences_data_class(marker_tag!!).toString())
    }
    fun get_polygon_list_volley() {


        val requestQueue = Volley.newRequestQueue(application.applicationContext)

        val url =
            "https://daudi.azurewebsites.net/nyumbakumi/login/get_association_polygon.php";
        val stringRequest: StringRequest =object : StringRequest(Method.POST, url, Response.Listener {


            var response = JSONObject(it)
            var state = response.getString("state")
            if (state.equals("successful")) {
               polygon_list = response.getString("list")

                Log.d("primary_residense_", polygon_list)

                val MyPreferences = "mypref"
                var sharedPreferences =application.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("primary_residense_polygon_list")
                editor.putString(
                    "primary_residense_polygon_list",
                    polygon_list
                )

                editor.apply()

                polygon(polygon_list)

            } else {
                polygon_list = ""
                //Toast.makeText(this,"N",Toast.LENGTH_LONG).show()
            }

        }, Response.ErrorListener {

        }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                val MyPreferences = "mypref"
                val sharedPreferences =
                    application.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                var association_id = sharedPreferences.getString("association_id", "");

                params["association_id"] = association_id!!

                return params
            }

        }
        requestQueue.add(stringRequest)

    }

    private fun get_my_association_polygon_list() {



        /*val MyPreferences = "mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val primary_residense_polygon_list = sharedPreferences.getString("primary_residense_polygon_list", "")
        Log.d("primary_residense",primary_residense_polygon_list!!)*/


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
        // val _url="https://daudi.azurewebsites.net/nyumbakumi/get_crime_data/get_crimes.php?from_what_time="+ from_time_value+"&to_which_time="+ to_time_value+"&incident_type="+incident_type;
        val _url="https://daudi.azurewebsites.net/nyumbakumi/get_crime_data/get_crimes.php"

        val stringRequest: StringRequest = object : StringRequest(Method.GET, _url, Response.Listener { response ->

            try {
                var response_json_object= JSONObject(response)
                var response=response_json_object.getString("response")
                var  crime_data_value=response_json_object.getString("data")

                Log.d("set_crime_incident",crime_data_value)

                if (response.equals("successful")) {

                    //iterates over the arrray of crime data
                    iterate_crime_data(crime_data_value)
                }
                else{

                }

            }
            catch (e: JSONException)
            {
                Log.d("JSONException",e.toString())

            }
            catch (e : ParseException)
            {
                Log.d("ParseException",e.toString())

            }


        }, Response.ErrorListener {


            val err= Volley_ErrorListener_Handler()

            //  err.check_error(it,view)


        })
        {
            /*   @Throws(AuthFailureError::class)
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
               }*/
        }
        requestQueue.add(stringRequest)




    }

    private  fun iterate_crime_data(crime_data: String)
    {

        save_crime_data_JSON(crime_data)
        var crime_data_jsonobject= JSONObject(crime_data)
        var crime_data_array=crime_data_jsonobject.getJSONArray("crime_data")

      if ((crime_data_array.length()>0))
          {
              Log.d("crime_data_array",crime_data_array.toString()+"---"+crime_data_array.length())

              for (i in 0..crime_data_array.length() - 1) {

                  var crime_data_array_jsonobjects=crime_data_array.getJSONObject(i)
                  var id_no=crime_data_array_jsonobjects.getString("id_no")
                  var mobile_no=crime_data_array_jsonobjects.getString("mobile_no")
                  var crime_time_and_date_value=crime_data_array_jsonobjects.getString("crime_time_and_date_value")
                  crime_time_and_date_value=crime_time_and_date_value.replace("\"","")
                  var incident_type=crime_data_array_jsonobjects.getString("incident_type")
                  var marker_tag=crime_data_array_jsonobjects.getString("marker_tag")


                  var listLatLng_todb=crime_data_array_jsonobjects.getString("listLatLng_todb")
                  var  listLatLng_todb_array= JSONArray(listLatLng_todb)

                  var lat=getlatlong(listLatLng_todb_array,"lat")
                  var long=getlatlong(listLatLng_todb_array,"long")

                  var marker_location= LatLng(lat.toDouble(),long.toDouble())
                  var crime_description=crime_data_array_jsonobjects.getString("crime_description")

                  Log.d("lat_looong",crime_time_and_date_value.toString())

                  var data=  crime_incidences_data_class(
                      crime_data_array_jsonobjects.getString("listLatLng_todb"),
                      crime_data_array_jsonobjects.getString("id_no"),
                      crime_data_array_jsonobjects.getString("marker_tag"),
                      crime_data_array_jsonobjects.getString("crime_time_and_date_value"),
                      crime_data_array_jsonobjects.getString("incident_type"),
                      crime_data_array_jsonobjects.getString("crime_description"),
                      crime_data_array_jsonobjects.getString("location_description"),
                      crime_data_array_jsonobjects.getString("mobile_no")


                      )

                  crime_data_arraylist.add(data)
                  //function call to set incident markers .is passing location,crime description and time value
                  set_marker(crime_time_and_date_value,marker_location,crime_description,incident_type,marker_tag,data)
              }

          }

    }



    private fun save_crime_data_JSON(crime_data: String)
    {
        val MyPreferences = "mypref"
        var sharedPreferences =  getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.remove("crime_data")

        editor.putString("crime_data", crime_data)
    }

    private fun getlatlong(listlang: JSONArray,name: String): String
    {

        var latlong_jsonobject=listlang.getJSONObject(0)
        var latlong=latlong_jsonobject.getString(name)

        return latlong
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {



                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)

                draw(Canvas(bitmap))
                BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
     fun set_marker(crimeTimeAndDateValue: String, markerLocation: LatLng, crimeDescription: String,incident_type: String,marker_tag: String,data: crime_incidences_data_class)
    {
    var crime_happened_duration=crime_happened_duration(crimeTimeAndDateValue)

var marker_tag_with_data_payload=change_object_to_json_string(data)
        Log.d("to_json",marker_tag_with_data_payload)
        when(incident_type)
        {
            "crime"->{

                var crime_pin=bitmapDescriptorFromVector(rootView_dashboard!!.context,R.drawable.crime_location_pin_red)

                val markerOptions =MarkerOptions().snippet(crimeDescription).title(crime_happened_duration).position(markerLocation)
                    .icon(crime_pin)
               // (bitmapDescriptorFromVector(this, R.drawable.ic_car_white_24dp)
                  //  .icon(BitmapDescriptorFactory.fromResource(crime_pin))

                val map_marker = googleMap?.addMarker(markerOptions)
                map_marker?.tag=marker_tag_with_data_payload
                map_marker?.showInfoWindow()
            }
            "suspicious"->{
                var suspicious_pin=bitmapDescriptorFromVector(rootView_dashboard!!.context,R.drawable.suspicious_location_pin_orange)

                val markerOptions =MarkerOptions().snippet(crimeDescription).title(crime_happened_duration).position(markerLocation)
                    .icon(suspicious_pin)
                val map_marker = googleMap?.addMarker(markerOptions)
                map_marker?.tag=marker_tag_with_data_payload
                map_marker?.showInfoWindow()
            }
            else->{
                var safety_pin=bitmapDescriptorFromVector(rootView_dashboard!!.context, R.drawable.safety_location_pin_green)

                val markerOptions =MarkerOptions().snippet(crimeDescription).title(crime_happened_duration).position(markerLocation)
                    .icon(safety_pin)
                val map_marker = googleMap?.addMarker(markerOptions)
                map_marker?.tag=marker_tag_with_data_payload
                map_marker?.showInfoWindow()

            }
        }

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
    fun dashboard_map()
    {
        mapFragment = supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL;

            googleMap?.uiSettings?.isCompassEnabled=true
            googleMap?.uiSettings?.isZoomControlsEnabled=true


            if (!polygon_list.isEmpty())
            {
                polygon(polygon_list)

            }
            else {
                googleMap?.setOnMapLoadedCallback {
                    if (!polygon_list.isEmpty())
                    {
                        polygon(polygon_list)

                    }

                }
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
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
                return@OnMapReadyCallback
            }
            googleMap?.isMyLocationEnabled = true
            googleMap?.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener() {
               onMarkerClick(it)

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


        val currentPlace = CameraPosition.Builder()
            .target(focus_location)
            .tilt(65.5f).zoom(16.5f).build()

        googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace))

       // googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(focus_location, 16f))

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
                val bundle = Bundle()
                bundle.putSerializable("user_data", crime_data_arraylist as Serializable)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.sos->{
                    val intent= Intent(this,Sos::class.java)
                 startActivity(intent)
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

                val intent= Intent(this,GroupChat::class.java)
                startActivity(intent)
            }

            R.id.statistics->{

              /*  CoroutineScope(Dispatchers.IO).launch {
                    var instanse=association_chat_db_instances()
                    instanse.delete_sata_on_table(applicationContext)
                }*/

                val intent= Intent(this,Statistics::class.java)
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


    private fun change_object_to_json_string(data: crime_incidences_data_class): String
    {
        val jsonTut: String = Gson().toJson(data)

        return  jsonTut
    }

    private fun change_to_crime_incidences_data_class(data:String): String
     {

         var data_fro_marker=Gson().fromJson<crime_incidences_data_class>(data,crime_incidences_data_class::class.java)
       //  Log.d("data_fro_marker",data_fro_marker.toString())
return  data_fro_marker.toString()
    }

}