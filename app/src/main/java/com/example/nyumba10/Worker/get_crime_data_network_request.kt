package com.example.nyumba10.Worker

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.Security.Encrypt
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.util.HashMap

private var crime_data=""
class get_crime_data_network_request {

    public fun set_crime_incident_markers(context: Context): String
    {
        val requestQueue = Volley.newRequestQueue(context)
        val login_url="https://daudi.azurewebsites.net/nyumbakumi/get_crime_data/get_crimes.php";
        val stringRequest: StringRequest = object : StringRequest(Method.POST, login_url, Response.Listener { response ->

            try {

                var response_json_object= JSONObject(response)
                var response=response_json_object.getString("response")
                var  crime_data_value=response_json_object.getString("data")

                Log.d("set_crime_incident",crime_data_value)

                if (response.equals("successful")) {

                  crime_data= crime_data_value
                }
                else{
                    crime_data=""

                }

            }
            catch (e: JSONException)
            {
                Log.d("JSONException",e.toString())
                crime_data="error"

            }
            catch (e : ParseException)
            {
                Log.d("ParseException",e.toString())
                crime_data="error"

            }


        }, Response.ErrorListener {


            val err= Volley_ErrorListener_Handler()

            //  err.check_error(it,view)


        })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                val encrypt = Encrypt()
                val MyPreferences = "mypref"
                val sharedPreferences =
                    context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                // String session_id= sharedPreferences.getString("sessions_ids","");

                //   params["session_ids"] = session_idss!!

                return params
            }
        }
        requestQueue.add(stringRequest)

    return  crime_data
    }
}