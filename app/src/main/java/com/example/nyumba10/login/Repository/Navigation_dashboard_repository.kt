package com.example.nyumba10.login.Repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.util.HashMap



private val crime_data_arraylist = ArrayList<crime_incidences_data_class>()

class Navigation_dashboard_repository (var requestQueue: RequestQueue){

    private  var responsevalue: String=""
val crime_data_arraylist_value= MutableLiveData<ArrayList<crime_incidences_data_class>>()

    fun network_fetch(): String
    {

       // all_articles_data_set.clear()


        requestQueue.start();

        val builder: Uri.Builder = Uri.Builder()
        builder.scheme("https")
            .authority("daudi.azurewebsites.net")
            .appendPath("nyumbakumi")
            .appendPath("get_crime_data")
            .appendPath("get_crimes.php")

          //  .appendQueryParameter("q", "Kenya")
            //.appendQueryParameter("apiKey", "581ecd6a0ec4430fb1cb37f97ff1a26e")
        val myUrl: String = builder.build().toString()


        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, myUrl, null,{ response ->
            try {
               // responsevalue ="success"

                val response_json_object= response
                val responsevalue=response_json_object.getString("response")
                var  crime_data_value=response_json_object.getString("data")
                Log.d("response_value",response.toString())


                iterate_crime_data(crime_data_value)

            }catch (e: JSONException)
            {
              //  responsevalue ="JSONException"
                Log.d("JSONEXCEPTION",e.toString())

            }
        },{
            Log.d("volley_error",it.toString())

         //   responsevalue ="VolleyError"

        }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"]="Mozilla/5.0"
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)

        jsonObjectRequest.setRetryPolicy(
            DefaultRetryPolicy(
                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT            )
        )

        return responsevalue

    }

    private  fun iterate_crime_data(crime_data: String)
    {

       // save_crime_data_JSON(crime_data)
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
              //  set_marker(crime_time_and_date_value,marker_location,crime_description,incident_type,marker_tag,data)

                if(i == (crime_data_array.length()-1)){

                    crime_data_arraylist_value.value= crime_data_arraylist

                }

            }



        }

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


}