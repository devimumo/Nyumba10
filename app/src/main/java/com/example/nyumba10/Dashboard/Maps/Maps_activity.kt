package com.example.nyumba10.Dashboard.Maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps_activity.*
import kotlinx.android.synthetic.main.association_add.*
import kotlinx.android.synthetic.main.association_add.county_value
import kotlinx.android.synthetic.main.association_add.sub_coounty_progress
import kotlinx.android.synthetic.main.association_add.sub_county_value
import kotlinx.android.synthetic.main.association_layout.*
import kotlinx.android.synthetic.main.association_name_alert_dialog.view.*
import kotlinx.android.synthetic.main.crime_info_layout.view.*
import kotlinx.android.synthetic.main.reportcrime.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.util.HashMap
private var list = ArrayList<String>()
private var chosen_county=""
private var chosen_sub_county=""
private var created_by=""
private var association_name_value=""

private var rootView_map_activity: View? =null
private var sub_counties_list = ArrayList<String>()
private var listLatLngs: ArrayList<LatLng> = ArrayList()

class Maps_activity : AppCompatActivity() {

    private var listLatLng_todb: ArrayList<String> = ArrayList()

    private var listMarkers: ArrayList<Marker> =ArrayList()
    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap
    private var polygon: Polygon? =null

    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_GREEN_ARGB = -0xc771c4
    private val COLOR_PURPLE_ARGB = -0x7e387c
    private val COLOR_ORANGE_ARGB = -0xa80e9
    private val COLOR_BLUE_ARGB = -0x657db

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_activity)

      list = get_counties("county")


        rootView_map_activity = window.decorView.rootView

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it

            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
          //  googleMap.setMinZoomPreference(10.0f)

           // googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN;

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {



                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                }
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            }
            googleMap.isMyLocationEnabled = true


            ////*****************************************************************************************************



            googleMap.setOnMapClickListener {

                if (chosen_county.isEmpty()) {


                    Toast.makeText(this, "Please choose county ", Toast.LENGTH_LONG).show()
                    counties_alert(rootView_map_activity!!)

                } else

                {
                val markerOptions = MarkerOptions().position(it)
                val marker =
                    googleMap.addMarker(markerOptions)
                val latlongjsonobject = JSONObject()
                //  listLatLngs.add(it)

                var lat_t = it.latitude
                var long_t = it.longitude
                latlongjsonobject.put("lat", lat_t)
                latlongjsonobject.put("long", long_t)


                var der = "\"" + lat_t.toString() + "," + long_t.toString() + "\""
                var der_e = lat_t.toString() + "," + long_t.toString()

                listLatLngs.add(LatLng(lat_t, long_t))

                listLatLng_todb.add(latlongjsonobject.toString())

                var ttr = JSONArray(listLatLngs).toString()
                Log.d("listLatLngs", listLatLng_todb.toString())
                listMarkers.add(marker)
            }
            }

            ////*****************************************************************************************************

            val location1 = LatLng(-1.540079, 37.259456)
          //  var mylocation=LatLng(location1)


           googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(LatLng(-1.540079, 37.259456
           ),17f))



          /*  val location2 = LatLng(9.89,78.11)
            googleMap.addMarker(MarkerOptions().position(location2).title("Madurai"))


            val location3 = LatLng(13.00,77.00)
            googleMap.addMarker(MarkerOptions().position(location3).title("Bangalore"))*/

        })
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    googleMap.isMyLocationEnabled = true

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    android.widget.Toast.makeText(
                        this,
                        "Contact permission is needed",
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

    fun polygon(view: View) {

if (chosen_county.isEmpty())
{


    Toast.makeText(this,"Please choose county ",Toast.LENGTH_LONG).show()
counties_alert(view)

}
        else
{
  if (listLatLngs.isEmpty() )
  {
      Toast.makeText(this,"Please create a polygon first",Toast.LENGTH_LONG).show()

  }else
  {


      if (polygon!=null)
      {
          polygon?.remove()


          get_association_name_alert_dialog()
          val polygonoptions: PolygonOptions=PolygonOptions().addAll(listLatLngs).clickable(true)
          polygon=googleMap.addPolygon(polygonoptions)
          //   val fillColor = COLOR_WHITE_ARGB
          val strokecolor=COLOR_ORANGE_ARGB
          //  polygon?.fillColor=fillColor
          polygon?.strokeColor=strokecolor
      }
      get_association_name_alert_dialog()


      polygon?.remove()
      val polygonoptions: PolygonOptions=PolygonOptions().addAll(listLatLngs).clickable(true)
      polygon=googleMap.addPolygon(polygonoptions)
      //  val fillColor = COLOR_WHITE_ARGB
      val strokecolor=COLOR_ORANGE_ARGB
      //  polygon?.fillColor=fillColor
      polygon?.strokeColor=strokecolor
  }
}



    }


    private fun get_association_name_alert_dialog()
    {
        val builder = AlertDialog.Builder(this)
        // Get the layout inflater
        val inflater: LayoutInflater = LayoutInflater.from(this)
        var inflated=inflater.inflate(R.layout.association_name_alert_dialog, null)
        builder.setView(inflated)
        builder.setPositiveButton("Okay",
            DialogInterface.OnClickListener { dialog, id ->
                association_name_value=inflated.association_name_from_alert_dialog.text.toString()

                button.visibility=View.GONE
                save_new_association_data.visibility=View.VISIBLE

            })
        builder.setCancelable(true)

        builder.create()
        builder.show()
    }
    fun polygon_remove(view: View) {

        listLatLngs.clear()
        googleMap.clear()
        polygon?.remove()
        listMarkers.clear()

        button.visibility=View.VISIBLE
    }

    private fun send_created_association_to_db(view: View)
    {

        val MyPreferences = "mypref"
        val sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

        created_by=sharedPreferences.getString("id_no","name").toString()
        map_progress_bar.visibility= View.VISIBLE


        val requestQueue = Volley.newRequestQueue(this)
        val url="https://daudi.azurewebsites.net/nyumbakumi/my_associations/create_associations.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            Log.d("assoc_response",response)

            try {

                var response_json_object= JSONObject(response)
                var status=response_json_object.getString("response")

                when(status)
                {
                    "successful"->{
                        Toast.makeText(this,"Association created successfully",Toast.LENGTH_LONG).show()
                        save_new_association_data.visibility= View.VISIBLE
                        map_progress_bar.visibility= View.GONE

                    }
                    "error"->{
                        save_new_association_data.visibility= View.VISIBLE

                        map_progress_bar.visibility= View.GONE
                    }
                    "unsuccessful"->{

                        save_new_association_data.visibility= View.VISIBLE

                        map_progress_bar.visibility= View.GONE
                    }
                }

            }
            catch (e: JSONException)
            {

                save_new_association_data.visibility= View.VISIBLE

                map_progress_bar.visibility= View.GONE
                Log.d("JSONException",e.toString())

            }
            catch (e : ParseException)
            {

                save_new_association_data.visibility= View.VISIBLE

                map_progress_bar.visibility= View.GONE
                Log.d("ParseException",e.toString())

            }


        }, Response.ErrorListener {

            val err= Volley_ErrorListener_Handler()
            save_new_association_data.visibility= View.VISIBLE


            map_progress_bar.visibility= View.GONE
            err.check_error(it,view)

        })


        {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                //    val times = Time_function()
                //  params["time_"] = times.current_time()
                //  params["date_"] = times.current_date()

                params["county"] = chosen_county
                params["sub_county"] = chosen_sub_county
                params["association_polygon_list"] = listLatLng_todb.toString()
                params["association_name"] = association_name_value
                params["created_by"] = created_by


                return params
            }


        }
        requestQueue.add(stringRequest)

    }



    fun counties_alert(view: View) {

        val MyPreferences = "mypref"
        val sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // var vv= sharedPreferences.getString("sessions_ids","");


        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle(R.string.title_activity_admin).setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
            , DialogInterface.OnClickListener { dialog, which ->
                chosen_county = list.get(which)
                county_value.setText(chosen_county)

                var sub_v: String = "sub_county"

                sub_counties_list = get_sub_counties(
                    chosen_county
                )

                Log.d("sub_counties_list", sub_counties_list.toString())

                // The 'which' argument contains the index position
                // of the selected item
            })

        val alert = builder.create()
        alert.show()
    }

    fun sub_counties_alert(view: View) {
        sub_coounty_progress.visibility=View.GONE

        val MyPreferences = "mypref"
        val sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // var vv= sharedPreferences.getString("sessions_ids","");


        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle(R.string.title_activity_admin).setCancelable(false).setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, sub_counties_list)
            , DialogInterface.OnClickListener { dialog, which ->
                chosen_sub_county = sub_counties_list.get(which)
                sub_county_value.setText(chosen_sub_county)
            })

        val alert = builder.create()

        alert.show()
    }

    private fun get_counties(value: String): ArrayList<String> {


        val url =
            "https://daudi.azurewebsites.net/nyumbakumi/my_associations/get_counties.php"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->

                if (response.equals("unsuccessful")) {
                    Log.d("counties_data", response)
                } else {
                    Log.d("counties_data", response)


                    var jsonObject = JSONObject(response)
                    var jsonarray = jsonObject.getJSONArray("counties_list")


                    for (i in 0 until jsonarray.length()) {
                        list.add(jsonarray.getJSONObject(i).getString("name"))
                    }

                    val MyPreferences = "mypref"
                    var sharedPreferences =
                        getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                    // String session_ide= sharedPreferences.getString("sessions_ids","");
                    val editor = sharedPreferences.edit()


                    // String phone_number_= phone_number.getText().toString().trim();
                    editor.remove("counties_json")
                    editor.putString("counties_json", response)
                    // editor.putString("phone_numbers",phone_number_);
                    editor.apply()
                }


            }, Response.ErrorListener {
                Log.i("Volley_Error", it.toString())

            }) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()


                    params["value"] = value

                    return params
                }

            }

        val requestQueue = Volley.newRequestQueue(this)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                80000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        return list
    }


    private fun get_sub_counties(value: String): ArrayList<String> {
        sub_county_value.visibility=View.GONE
        sub_coounty_progress.visibility=View.VISIBLE
        val url =
            "https://daudi.azurewebsites.net/nyumbakumi/my_associations/get_counties.php"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->

                Log.d("sub_response",response)

                try {
                    var jsonObject = JSONObject(response)
                    var response=jsonObject.getString("response")

                    when(response)
                    {
                        "successful"->{
                            //  sub_coounty_progress.visibility=View.GONE

                            var json_data = jsonObject.getString("data")
                            var jsonarray= JSONArray("["+json_data+"]")
                            sub_counties_list.clear()


                            for (i in 0 .. jsonarray.length()) {

                                //  for (i in 0 until jsonarray.length()) {
                                sub_counties_list.add(jsonarray[i].toString())
                                Log.d("sub_respons","sub="+ sub_counties_list.toString()+"i="+i+"jsonarray="+jsonarray.length())
                                if (i+1==jsonarray.length())
                                {
                                    sub_county_value.visibility=View.VISIBLE

                                    sub_counties_alert(rootView_map_activity!!)
                                }
                            }
                            val MyPreferences = "mypref"
                            var sharedPreferences =
                                getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                            // String session_ide= sharedPreferences.getString("sessions_ids","");
                            val editor = sharedPreferences.edit()


                            // String phone_number_= phone_number.getText().toString().trim();
                            editor.remove("counties_json")



                            editor.putString("counties_json", response)
                            // editor.putString("phone_numbers",phone_number_);
                            editor.apply()

                        }
                        "unsuccessful"->{}
                        "error"->{}
                        else->
                        {}

                    }
                    var data = jsonObject.getString("data")
                }catch (e: JSONException)
                {}catch (e: ParseException)
                {

                }



            }, Response.ErrorListener {
                Log.i("Volley_Error", it.toString())

            }) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()


                    params["value"] = value

                    return params
                }




            }






        val requestQueue = Volley.newRequestQueue(this)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                80000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        return sub_counties_list
    }

    fun maps_onclick(view: View) {

        when(view.id)
        {
            R.id.counties_alert->{
                counties_alert(view)
            }
            R.id.save_new_association_data->{

                send_created_association_to_db(view)
            }
            R.id.sub_county_value->{

                sub_counties_alert(view)
            }


        }
    }
}