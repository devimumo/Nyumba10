package com.example.nyumba10.DashboardMapUpdate

import android.view.View
import androidx.annotation.MainThread
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.login.DashBoard
import com.example.nyumba10.login.Navigation_Dashboard
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
private val Dashboard_instanse=Navigation_Dashboard()

class get_marker_to_update_details_from_onmessagereceived {
  // private var mRequestQueue = Volley.newRequestQueue() //assigning the requestqueue

suspend fun get_marker_to_update_details_from_onmessagereceived(message: String)
{


    var message_JSONobject=JSONObject(message)
    var mobile_no_value=message_JSONobject.getString("mobile_no")

    var crime_description=message_JSONobject.getString("crime_description")
    var crime_time_and_date_value=message_JSONobject.getString("crime_time_and_date_value")
    var listLatLng_todb=message_JSONobject.getString("listLatLng_todb")
    var sender_id_no=message_JSONobject.getString("sender_id_no")
    var incident_type=message_JSONobject.getString("incident_type")
    var marker_tag=message_JSONobject.getString("marker_tag")
    var location_descr=message_JSONobject.getString("location_description")

    var data=  crime_incidences_data_class(
           listLatLng_todb,
           sender_id_no,
          marker_tag,
         crime_time_and_date_value,
         incident_type,
          crime_description,
          location_descr,
        mobile_no_value
    )

    var  listLatLng_todb_array= JSONArray(listLatLng_todb)

    var lat=getlatlong(listLatLng_todb_array,"lat")
    var long=getlatlong(listLatLng_todb_array,"long")
    var marker_location= LatLng(lat.toDouble(),long.toDouble())

    withContext(Dispatchers.Main) {
        Dashboard_instanse.set_marker(crime_time_and_date_value, marker_location, crime_description,incident_type,marker_tag,data)
    }

}
    private fun getlatlong(listlang: JSONArray,name: String): String
    {

        var latlong_jsonobject=listlang.getJSONObject(0)
        var latlong=latlong_jsonobject.getString(name)


        return latlong
    }

}