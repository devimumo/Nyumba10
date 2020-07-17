package com.example.nyumba10.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.nyumba10.Dashboard.Admin.Admin
import com.example.nyumba10.Dashboard.Chat.Chat
import com.example.nyumba10.Dashboard.History.History
import com.example.nyumba10.Dashboard.MyAccount.MyAccount
import com.example.nyumba10.Dashboard.MyAssociation.MyAssociation
import com.example.nyumba10.Dashboard.ReportCrime.ReportCrime
import com.example.nyumba10.Dashboard.Security.Security
import com.example.nyumba10.Maps.MapsActivity
import com.example.nyumba10.Maps.Maps_activity
import com.example.nyumba10.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import org.json.JSONArray
import org.json.JSONObject

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
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        map_permissions()


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

        else
        {
            dashboard_map()
        }

//        googleMap.isMyLocationEnabled = true
    }


    fun dashboard_map()
    {
        mapFragment = supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it

            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL;
            get_my_association_polygon_list()

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


      /*  if (polygon != null) {
            polygon?.remove()
            val fillColor = COLOR_BLUE_LINES
            val strokecolor = COLOR_BLUE_ARGB
            val polygonoptions: PolygonOptions =
                PolygonOptions().addAll(listLatLngs_arraylist).clickable(true).fillColor(
                    strokecolor).strokeColor(COLOR_BLUE_LINES.toInt())
            polygon =googleMap?.addPolygon(polygonoptions)

            polygon?.fillColor= fillColor.toInt()
            polygon?.strokeColor = strokecolor


            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(focus_location, 16f))

        }
*/
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

}