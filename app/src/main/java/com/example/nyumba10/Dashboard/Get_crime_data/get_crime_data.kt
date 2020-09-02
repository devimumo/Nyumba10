package com.example.nyumba10.Dashboard.Get_crime_data

import android.content.Context
import android.util.Log
import android.view.View
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Stats.Statistics
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException


private  var return_value: String=""
private   var  crime_data_value: String=""
class get_crime_data {


     fun get_crime_data(from_what_time: String,to_which_time: String,incident_type:String,context: Context,view: View)
    {

        val requestQueue = Volley.newRequestQueue(context)
        val url="https://daudi.azurewebsites.net/nyumbakumi/get_crime_data/get_crimes.php?from_what_time="+from_what_time+"&to_which_time="+to_which_time+"&incident_type="+incident_type;
        val stringRequest: StringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
            Log.d("response_crime_data",response)

            try {

                var response_json_object= JSONObject(response)
                var response=response_json_object.getString("response")


                if (response.equals("successful")) {
                    crime_data_value=response_json_object.getString("data")
                   // Log.d("set_crime_incidents",crime_data_value)
                    if (crime_data_value.isEmpty())
                    {
                     //   return_value="!data"
                        snack_bar("no data",view)

                    }
                    else
                    {
                        var instanse=Statistics()
                        instanse.iterate_crime_data(crime_data_value,view)
                        snack_bar("Successful",view)

                        // return_value=crime_data_value
                    }

                    //  iterate_crime_data(crime_data_value)
                }
                else{
                //    return_value="!data"
                }
            }
            catch (e: JSONException)
            {
                return_value="error"

                Log.d("JSONException",e.toString())

            }
            catch (e : ParseException)
            {
                return_value="error"

                Log.d("ParseException",e.toString())

            }


        }, Response.ErrorListener {

            val err= Volley_ErrorListener_Handler()
            return_value="error"
             err.check_error(it,view)

        })
        {
          /*  @Throws(AuthFailureError::class)
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
            }*/
        }
        requestQueue.add(stringRequest)

    }

   private fun snack_bar(string: String?, view: View) {
        val mysnackbar = Snackbar.make(view, string!!, Snackbar.LENGTH_LONG)
        mysnackbar.show()
    }

}