package com.example.nyumba10.Dashboard.Admin

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_recyclerview_adapter
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.Security.Encrypt
import kotlinx.android.synthetic.main.fragment_incidences_crimes.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

private  var rootView: View? = null
private val crime_data_arraylist = ArrayList<crime_incidences_data_class>()

class Incidences_Crimes : Fragment() {


       override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_incidences_crimes, container, false)
retreive_crime_data(rootView!!)
        return  rootView
    }

    private fun retreive_crime_data(view: View)
    {
        val MyPreferences = "mypref"
        var sharedPreferences =  (view.context).getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        var crime_data= sharedPreferences.getString("crime_data","");

        if (crime_data!!.isEmpty())
        {
            retreive_crime_data_volley(view)

        }
        else
        {
set_crime_data_to_recycler_adapter(crime_data,view)
        }

    }

    private fun set_crime_data_to_recycler_adapter(crimeData: String,view: View) {


        var recycler_view = view.crime_list_recycler_view

        recycler_view?.layoutManager = LinearLayoutManager(context)
        (recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(true)

        crime_data_arraylist.clear()

        var crime_data_jsonobject= JSONObject(crimeData)
        var crime_data_json_array=crime_data_jsonobject.getJSONArray("crime_data")


        for (i in 0..crime_data_json_array.length()-1)
        {


            var data_at_position=crime_data_json_array.getJSONObject(i)
            var data=  crime_incidences_data_class(
                data_at_position.getString("listLatLng_todb"),
                data_at_position.getString("id_no"),
                data_at_position.getString("marker_tag"),
                data_at_position.getString("crime_time_and_date_value"),
                data_at_position.getString("incident_type"),
                data_at_position.getString("crime_description"),
                data_at_position.getString("location_description")



            )

Log.d("crime_data_arraylist", crime_data_arraylist.toString())

            crime_data_arraylist.add(data)

crime_data_arraylist.sortByDescending {
    it.time
}
        }

        var sortedList: List<crime_incidences_data_class> = crime_data_arraylist.sortedWith(compareBy {
            it.time
        })

        var adap = rootView?.context?.let {
            crime_incidences_recyclerview_adapter(it, crime_data_arraylist
            )
        }
        recycler_view?.layoutManager = LinearLayoutManager(context)
        adap?.notifyDataSetChanged()
        recycler_view?.adapter = adap
        //(recycler_view.layoutManager as LinearLayoutManager).reverseLayout=true
        (recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(false)

    }

    private fun retreive_crime_data_volley(view: View) {

            val requestQueue = Volley.newRequestQueue(view.context)
            val login_url="https://daudi.azurewebsites.net/nyumbakumi/get_crime_data/get_crimes.php";
            val stringRequest: StringRequest = object : StringRequest(Method.POST, login_url, Response.Listener { response ->

                try {

                    var response_json_object= JSONObject(response)
                    var response=response_json_object.getString("response")
                    var  crime_data_value=response_json_object.getString("data")

                    Log.d("set_crime_incident",crime_data_value)

                    if (response.equals("successful")) {
                        set_crime_data_to_recycler_adapter(crime_data_value,view)

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
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()
                    val encrypt = Encrypt()
                    val MyPreferences = "mypref"
                    val sharedPreferences =
                        context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                    // String session_id= sharedPreferences.getString("sessions_ids","");

                    //   params["session_ids"] = session_idss!!

                    return params
                }
            }
            requestQueue.add(stringRequest)

        }

}