package com.example.nyumba10.Dashboard.MyAssociation.Association_search

import android.Manifest
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.R
import com.example.nyumba10.login.DashBoard
import com.example.nyumba10.login.Login
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_register.progress
import kotlinx.android.synthetic.main.association_add.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.util.HashMap

private lateinit var fusedLocationClient: FusedLocationProviderClient
private var listLatLngs_arraylist: ArrayList<LatLng> = ArrayList()

private var association_id_value: String =""
private var current_location: Location? =null
val statement_data = ArrayList<associations_data_class>()
var list = ArrayList<String>()
private var chosen_county=""

var sub_counties_list = ArrayList<String>()
lateinit var mapFragment : SupportMapFragment
lateinit var googleMap: GoogleMap


private var polygon: Polygon? =null

private val COLOR_WHITE_ARGB = -0x1
private val COLOR_GREEN_ARGB = -0xc771c4
private val COLOR_PURPLE_ARGB = -0x7e387c
private val COLOR_ORANGE_ARGB = -0xa80e9
private val COLOR_BLUE_ARGB = -0x657db
var rootView_association_add: View? = null


class Association_add : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.association_add)

        rootView_association_add = window.decorView.rootView

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        get_last_location()

        save_association_membership_details.setOnClickListener {
            save_association_details_to_db(this)
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it

            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
        })

        sub_county_value.setOnClickListener {
           // sub_counties_alert(it)
        }
        counties_alert.setOnClickListener {
            counties_alert(it)

        }

        list = get_counties("county")
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView: SearchView = searchview as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                counties_card_view.visibility=View.GONE
                associations_details_card.visibility=View.GONE
                fetch_associations_volley(query)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                statement_data.clear()
                if (newText.isEmpty()) {
                    recycler_view.visibility = View.GONE
                } else {

                    fetch_associations_volley(newText)

                }

                return false
            }
        })

    }

    private fun get_last_location() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                }
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            }

            return
        }

        fusedLocationClient.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                current_location = location
                if (current_location != null) {
                    val latitude = current_location?.latitude
                    val longitude = current_location?.longitude

                    googleMap.isMyLocationEnabled = true
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                latitude!!, longitude!!
                            ), 15f
                        )
                    )
                } else {

                    Toast.makeText(
                        this,
                        "You location not acquired.Please enable location settings".toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }

            }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                    get_last_location()

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    android.widget.Toast.makeText(
                        this,
                        "location permission is needed",
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
              /*  if (chosen_county.trim().equals("machakos", ignoreCase = true)) {

                    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL;

                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                -1.494237, 37.284213
                            ), 12f
                        )
                    )


                }*/
                var sub_v: String = "sub_county"

                sub_counties_list = get_sub_counties(chosen_county)

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
        builder.setTitle(R.string.title_activity_admin).setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, sub_counties_list)
            , DialogInterface.OnClickListener { dialog, which ->
                var chosen_county = sub_counties_list.get(which)
                sub_county_value.setText(chosen_county)

                counties_card_view.visibility=View.GONE
                associations_details_card.visibility=View.GONE

                searchview.visibility=View.VISIBLE
                recycler_view.visibility=View.VISIBLE


                var sub_v: String = "sub_county"


                // The 'which' argument contains the index position
                // of the selected item
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

                                    sub_counties_alert(rootView_association_add!!)
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


    fun polygon(view: View, listLatLngs: String) {
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

        var teeee = listLatLngs_arraylist[0]


     /*   if (polygon != null) {
            polygon?.remove()

            val polygonoptions: PolygonOptions =
                PolygonOptions().addAll(listLatLngs_arraylist).clickable(true)
            polygon = googleMap.addPolygon(polygonoptions)
            val fillColor = COLOR_WHITE_ARGB
            val strokecolor = COLOR_ORANGE_ARGB
            polygon?.fillColor = fillColor
            polygon?.strokeColor = strokecolor

            Toast.makeText(this, teeee.toString(), Toast.LENGTH_LONG).show()

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(teeee, 12f))

        }*/

        var fill=0x2035BFE4.toInt();
       // val fillColor = COLOR_BLUE_LINES
        val strokecolor = 0xFF008577.toInt()
        polygon?.remove()
        val polygonoptions: PolygonOptions =
            PolygonOptions().addAll(listLatLngs_arraylist).clickable(true).strokeWidth(2F).fillColor(
                fill
            ).strokeColor(strokecolor)
        polygon = googleMap.addPolygon(polygonoptions)
        val fillColor = COLOR_WHITE_ARGB
      //  val strokecolor = COLOR_ORANGE_ARGB
        // polygon?.fillColor=fillColor
        polygon?.strokeColor = strokecolor

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(teeee, 16f))


    }


    private fun fetch_associations_volley(query: String) {




        Log.d("query_value", query)
        if (query.isEmpty()) {
            statement_data.clear()
        }
        val url ="https://daudi.azurewebsites.net/nyumbakumi/my_associations/association_search.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,Response.Listener { response ->
                Log.i("Responsed", response)
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(response)
                    //   val responses = jsonObject.getString("response")
                    when (response) {
                        "unsuccessful" -> {
                            Toast.makeText(applicationContext, "Unsuccessful", Toast.LENGTH_LONG)
                                .show()

                            counties_card_view.visibility=View.VISIBLE

                            progress!!.visibility = View.GONE

                        }
                        //
                        else -> {
                            val data = jsonObject.getJSONArray("association_list")
                            if (data.length() == 0) {

                                recycler_view.visibility = View.GONE
                                counties_card_view.visibility=View.VISIBLE
                                sub_coounty_progress.visibility = View.GONE

                                Toast.makeText(this,"No such association",Toast.LENGTH_LONG).show()
                                statement_data.clear()
                            } else {
                                recycler_view.visibility = View.VISIBLE
                                sub_coounty_progress.visibility = View.GONE

                                for (i in 0..data.length() - 1) {

                                    val association_data = data.getJSONObject(i)
                                    val association_data_array = associations_data_class(
                                        association_data.getString("association_name"),
                                        association_data.getString("county"),
                                        association_data.getString("sub_county"),
                                        association_data.getString("association_id"),
                                        association_data.getString("association_polygon_list")
                                    )


                                    val adap = Associations_search_adapter(statement_data, this,rootView_association_add)



                                    statement_data.add(association_data_array)

                                    Log.d("association_data", statement_data.toString())

                                    recycler_view.layoutManager = LinearLayoutManager(this)

                                    adap.notifyDataSetChanged()
                                    recycler_view.adapter = adap
                                }
                                //progressbar!!.visibility = View.INVISIBLE
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.i("Volley_Error", error.toString())
                //  progressbar!!.visibility = View.INVISIBLE
            }) {



            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                var params: MutableMap<String, String> =
                    HashMap()


                params.put("county", county_value.text.toString())
                params.put("sub_county_value", sub_county_value.text.toString())
                params.put("query", query)

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
    }

    fun association_add_activity_click(view: View) {

        when (view.id) {

         /*   R.id.association_name_card -> {

                counties_card_view.visibility = View.GONE
                associations_details_card.visibility = View.GONE
                searchview.visibility = View.VISIBLE
                search_description_text.visibility = View.VISIBLE
                recycler_view.visibility = View.VISIBLE
            }*/

        }

    }

    fun save_association_details_to_db(context: Context) {

        
        var association_id_value=association_id.text.toString()
        save_association_membership_details.visibility=View.GONE

        save_association_details_progressbar.visibility=View.VISIBLE
        val requestQueue = Volley.newRequestQueue(context)

        val url = "https://daudi.azurewebsites.net/nyumbakumi/my_associations/save_association_details.php";
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener {

Log.d("associat_add_res",it.toString())
                try {


                    var jsonObject = JSONObject(it)

                    var response=jsonObject.getString("response")
                    when(response){
                        "successful"->{

                            save_association_membership_details.visibility=View.VISIBLE

                            save_association_details_progressbar.visibility=View.GONE

                            val MyPreferences="mypref"
                            val sharedPreferences =
                                getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()




                            editor.remove("association_add_status");
                            editor.putString("association_add_status", "updated")
                            editor.remove("primary_residense_polygon_list");
                            editor.putString("primary_residense_polygon_list", listLatLngs_arraylist.toString())

                            editor.apply()


                            save_association_details_progressbar.visibility=View.GONE

                            send_firebasetopic_subscription(association_id_value)

                            val intent= Intent(this, Login::class.java)
                            startActivity(intent)
                        }
                        else-> {

                            save_association_membership_details.visibility=View.VISIBLE

                            save_association_details_progressbar.visibility=View.GONE

                            Toast.makeText(this, "Records not saved", Toast.LENGTH_LONG).show()
                            save_association_details_progressbar.visibility = View.GONE

                        }}
                }catch (e: JSONException)
                {

                    save_association_membership_details.visibility=View.VISIBLE

                    save_association_details_progressbar.visibility=View.GONE
                    Log.d("JSONexec_error",e.toString())

                }




            }, Response.ErrorListener {
                Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show()
                save_association_details_progressbar.visibility = View.GONE
                save_association_membership_details.visibility=View.VISIBLE


            }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()
                    val MyPreferences = "mypref"
                    val sharedPreferences =
                        getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                    // String session_id= sharedPreferences.getString("sessions_ids","");


                    val id_no_value = sharedPreferences.getString("id_no", "")


                    params["id_no_value"] = id_no_value!!
                    params["association_id"] = association_id_value
                    params["residence_type_value"] = "Primary"


                    return params
                }
            }


    requestQueue.add(stringRequest)
}

    private fun send_firebasetopic_subscription(associationIdValue: String) {

        FirebaseMessaging.getInstance().subscribeToTopic(associationIdValue)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {

                }
                Toast.makeText(this,"Records saved successfully", Toast.LENGTH_LONG).show()

            }




    }

    override fun onBackPressed() {

       // super.onBackPressed()

        val MyPreferences = "mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val association_add_status = sharedPreferences.getString("association_add_status", "!updated")
        if (association_add_status.equals("!updated"))
        {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("You have not conluded registration. \n" +"Do you still want exit?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        // super.onBackPressed()

                        finish()

                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog

                    })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        }
        else{
            super.onBackPressed()

        }
    }






}





