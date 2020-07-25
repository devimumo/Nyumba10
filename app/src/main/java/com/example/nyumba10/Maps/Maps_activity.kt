package com.example.nyumba10.Maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nyumba10.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONObject


class Maps_activity : AppCompatActivity() {
    private var listLatLngs: ArrayList<LatLng> = ArrayList()

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


                val markerOptions = MarkerOptions().position(it)
                val marker =
                    googleMap.addMarker(markerOptions)
                val latlongjsonobject = JSONObject()
                //  listLatLngs.add(it)

                var lat_t=it.latitude
                var long_t=it.longitude
                latlongjsonobject.put("lat",lat_t)
                latlongjsonobject.put("long",long_t)



                var der="\""+lat_t.toString()+","+long_t.toString()+"\""
                var der_e=lat_t.toString()+","+long_t.toString()

                listLatLngs.add(LatLng(lat_t,long_t))

                listLatLng_todb.add(latlongjsonobject.toString())

                var ttr=JSONArray(listLatLngs).toString()
                Log.d("listLatLngs",listLatLng_todb.toString())
                listMarkers.add(marker)
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

Toast.makeText(this,"puly",Toast.LENGTH_LONG).show()
       if (polygon!=null)
       {
           polygon?.remove()

           val polygonoptions: PolygonOptions=PolygonOptions().addAll(listLatLngs).clickable(true)
           polygon=googleMap.addPolygon(polygonoptions)
           val fillColor = COLOR_WHITE_ARGB
           val strokecolor=COLOR_ORANGE_ARGB
           polygon?.fillColor=fillColor
           polygon?.strokeColor=strokecolor
       }

        polygon?.remove()
        val polygonoptions: PolygonOptions=PolygonOptions().addAll(listLatLngs).clickable(true)
        polygon=googleMap.addPolygon(polygonoptions)
        val fillColor = COLOR_WHITE_ARGB
        val strokecolor=COLOR_ORANGE_ARGB
        polygon?.fillColor=fillColor
        polygon?.strokeColor=strokecolor

    }

    fun polygon_remove(view: View) {

        listLatLngs.clear()
        googleMap.clear()
        polygon?.remove()
        listMarkers.clear()
    }
}