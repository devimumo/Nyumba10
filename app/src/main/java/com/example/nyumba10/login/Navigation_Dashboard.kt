package com.example.nyumba10.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Admin.Admin
import com.example.nyumba10.Dashboard.Admin.Crime_data_fuul_details
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.Dashboard.GroupChat.GroupChat
import com.example.nyumba10.Dashboard.MyAccount.MyAccount
import com.example.nyumba10.Dashboard.MyAssociation.MyAssociation
import com.example.nyumba10.Dashboard.ReportCrime.ReportCrime
import com.example.nyumba10.Dashboard.Security.Security
import com.example.nyumba10.Dashboard.Sos.Sos
import com.example.nyumba10.Dashboard.Stats.Statistics
import com.example.nyumba10.R
import com.example.nyumba10.login.Repository.Navigation_dashboard_repository
import com.example.nyumba10.login.ViewModels.Navigation_dashboard_viewmodel
import com.example.nyumba10.login.factory.Navigation_dashboard_factory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private  var  REQUEST_LOCATION = 199
private lateinit var mRequestQueue: RequestQueue //reque
private lateinit var navigation_dashboard_factory: Navigation_dashboard_factory//reque
private lateinit var navigation_dashboard_repository: Navigation_dashboard_repository //reque
private lateinit var navigation_dashboard_viewmodel: Navigation_dashboard_viewmodel //reque
private var googleMap: GoogleMap? = null

class Navigation_Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_dashboard_activity)


        check_if_GPS_is_enabled()

        dashboard_map()

        mRequestQueue = Volley.newRequestQueue(applicationContext) //assigning the requestqueue
        navigation_dashboard_factory= Navigation_dashboard_factory((mRequestQueue))
        navigation_dashboard_repository= Navigation_dashboard_repository(mRequestQueue)
        navigation_dashboard_viewmodel=ViewModelProvider(this, navigation_dashboard_factory).get(Navigation_dashboard_viewmodel::class.java)

        var crime_data_observer=Observer<ArrayList<crime_incidences_data_class>>{data->


            set_markers_to_map(data)

        }


        navigation_dashboard_viewmodel.Crime_data_array.observe(this,crime_data_observer)
    }


   private  fun dashboard_map()
    {
        mapFragment = supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL;

            googleMap?.uiSettings?.isCompassEnabled=true
            googleMap?.uiSettings?.isZoomControlsEnabled=true


         /*   if (!polygon_list.isEmpty())
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
            }*/
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


    fun onMarkerClick(marker: Marker) {

        var marker_tag = marker.tag as String?

        val intent = Intent(this, Crime_data_fuul_details::class.java)


        //  bundle.putSerializable("user_data", crime_arraylist_tosend_to_full_details_acitivity as Serializable)
        intent.putExtra("crime_incidences_data_to_json_string_to_send",marker_tag)
        startActivity(intent)
    }


    //set markers to map
    private fun set_markers_to_map(data: ArrayList<crime_incidences_data_class>){

        for (i in 0..data.size-1)
        {

            var crime_desc=data[i].crime_description
            var id_no=data[i].id_no
            var crime_incident_type=data[i].incident_type
            var crime_location_description=data[i].location_description
            var crime_mobile_no=data[i].mobile_no
            var crime_latlong=data[i].latong
            var crime_tag=data[i].tag
            var crime_time=data[i].time

          //  var lat=getlatlong(crime_latlong,"lat")
          //  var long=getlatlong(crime_latlong,"long")

           // var marker_location= LatLng(lat.toDouble(),long.toDouble())

           //var tto=(JSONArray(crime_latlong)[0] )

            var crime_data_at_i=change_object_to_json_string(data[i])

            var crime_data_at_i_JSONOBJECT=JSONObject(crime_data_at_i)

            var  listLatLng_todb_array= JSONArray(crime_data_at_i_JSONOBJECT.getString("latong"))

            var lat=getlatlong(listLatLng_todb_array,"lat")
            var long=getlatlong(listLatLng_todb_array,"long")

            var marker_location= LatLng(lat.toDouble(),long.toDouble())


            set_marker(crime_time,marker_location,crime_desc,crime_incident_type,crime_tag,data[i])


            if(i == (data.size -1)){

                var focus_location = marker_location

                val currentPlace = CameraPosition.Builder()
                    .target(focus_location)
                    .tilt(65.5f).zoom(16.5f).build()

                googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
            }


        }
    }

    private fun getlatlong(listlang: JSONArray, name: String): String
    {

        var latlong_jsonobject=listlang.getJSONObject(0)
        var latlong=latlong_jsonobject.getString(name)

        return latlong
    }


    
    //set_markers
    fun set_marker(crimeTimeAndDateValue: String, markerLocation: LatLng, crimeDescription: String, incident_type: String, marker_tag: String, data: crime_incidences_data_class)
    {
        var crime_happened_duration=crime_happened_duration(crimeTimeAndDateValue)

        var marker_tag_with_data_payload=change_object_to_json_string(data)
        Log.d("to_json",marker_tag_with_data_payload)
        when(incident_type)
        {
            "crime"->{

                var crime_pin=bitmapDescriptorFromVector(this,R.drawable.crime_location_pin_red)

                val markerOptions =
                    MarkerOptions().snippet(crimeDescription).title(crime_happened_duration).position(markerLocation)
                    .icon(crime_pin)
                // (bitmapDescriptorFromVector(this, R.drawable.ic_car_white_24dp)
                //  .icon(BitmapDescriptorFactory.fromResource(crime_pin))

                val map_marker = googleMap?.addMarker(markerOptions)
                map_marker?.tag=marker_tag_with_data_payload
                map_marker?.showInfoWindow()
            }
            "suspicious"->{
                var suspicious_pin=bitmapDescriptorFromVector(this,R.drawable.suspicious_location_pin_orange)

                val markerOptions =
                    MarkerOptions().snippet(crimeDescription).title(crime_happened_duration).position(markerLocation)
                    .icon(suspicious_pin)
                val map_marker = googleMap?.addMarker(markerOptions)
                map_marker?.tag=marker_tag_with_data_payload
                map_marker?.showInfoWindow()
            }
            else->{
                var safety_pin=bitmapDescriptorFromVector(this, R.drawable.safety_location_pin_green)

                val markerOptions =
                    MarkerOptions().snippet(crimeDescription).title(crime_happened_duration).position(markerLocation)
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

    private fun change_object_to_json_string(data: crime_incidences_data_class): String
    {
        val jsonTut: String = Gson().toJson(data)

        return  jsonTut
    }

    private fun change_object_to_json_strings(data: String): String
    {
        val jsonTut: String = Gson().toJson(data)

        return  jsonTut
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {



            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)

            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    //Check if GPS location is enabled for device

    private fun check_if_GPS_is_enabled() {

        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Checking GPS is enabled
        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
      //  Toast.makeText(this,mGPS.toString(),Toast.LENGTH_LONG).show()


        if (mGPS.toString().equals("true"))
        {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                app_location_permissions()

            }
            else{
                Toast.makeText(this,"App does not have location permission",Toast.LENGTH_LONG).show()

            }

        }
        else
        {
            startActivity( Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));


        }


    }


    // allows navigation to other activities in the app
    fun Card_click(view: android.view.View) {
        when (view.id)
        {
            R.id.my_association->{


                val intent= Intent(this, MyAssociation::class.java)
                val bundle = Bundle()
            //    bundle.putSerializable("user_data", crime_data_arraylist as Serializable)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.sos->{
                val intent= Intent(this, Sos::class.java)
                startActivity(intent)
            }

            R.id.my_account->{

                val intent= Intent(this, MyAccount::class.java)
                startActivity(intent)
            }

            R.id.report_crime->{

                val intent= Intent(this, ReportCrime::class.java)
                startActivity(intent)
            }

            R.id.chat->{

                Log.d("Chatsss","clicked on chat")

                val intent= Intent(this, GroupChat::class.java)
                startActivity(intent)
            }

            R.id.statistics->{

                /*  CoroutineScope(Dispatchers.IO).launch {
                      var instanse=association_chat_db_instances()
                      instanse.delete_sata_on_table(applicationContext)
                  }*/

                val intent= Intent(this, Statistics::class.java)
                startActivity(intent)
            }

            R.id.admin->{

                val intent= Intent(this, Admin::class.java)
                startActivity(intent)
            }

            R.id.security->{

                val intent= Intent(this, Security::class.java)
                startActivity(intent)
            }

        }

    }

    //asking for map permissions when navigation dashboard activity is entered
    @RequiresApi(Build.VERSION_CODES.M)
    private fun app_location_permissions() {


        if (ActivityCompat.checkSelfPermission( applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)

            }
          requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)

        }
        else
        {
            // dashboard_map()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode)
        {
            //location permission
            199->{
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the

                }
                else
                {
                    Toast.makeText(this,"Location permission not granted for the app.\nYou " +
                            "might not be able to access location related services.",Toast.LENGTH_LONG).show()
                }
            }
        }
    }





    override fun onResume() {
        super.onResume()

check_if_GPS_is_enabled()
    }

}