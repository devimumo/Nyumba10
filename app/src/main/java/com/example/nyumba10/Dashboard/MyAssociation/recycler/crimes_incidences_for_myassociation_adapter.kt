package com.example.nyumba10.Dashboard.MyAssociation.recycler

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.nyumba10.Dashboard.Admin.Crime_data_fuul_details
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_recyclerview_adapter
import com.example.nyumba10.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.crime_incidences_recycler_layout.view.*
import org.json.JSONArray
import java.io.Serializable
import java.text.SimpleDateFormat

private  var crime_arraylist_tosend_to_full_details_acitivity= ArrayList<crime_incidences_data_class>()
private   lateinit var intent: Intent

class crimes_incidences_for_myassociation(var context: Context, var crimeIncidencesarraylist: ArrayList<crime_incidences_data_class> ):
    RecyclerView.Adapter<crimes_incidences_for_myassociation.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=LayoutInflater.from(parent.context).inflate(R.layout.crime_incidences_recycler_layout,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {


        return  crimeIncidencesarraylist.size
    }

    override fun onBindViewHolder(holder: crimes_incidences_for_myassociation.ViewHolder, position: Int) {

        val user_data: crime_incidences_data_class=crimeIncidencesarraylist[position]

        var holder_value=holder.itemview
        holder.itemview.tag=user_data.tag.toString()
        holder.itemview.time_value.text=get_time(user_data.time)

        if (user_data.crime_description.equals(""))
        {
            holder.itemview.crime_description.text=user_data.incident_type

        }
        else
        {
            holder.itemview.crime_description.text=user_data.crime_description

        }
        holder.itemview.incident_type.text=user_data.incident_type
        if (user_data.incident_type.equals("crime"))
        {

            holder_value.linear.setBackgroundColor(Color.parseColor("#C61212"))

        }
        else if (user_data.incident_type.equals("suspicious"))
        {
            holder_value.linear.setBackgroundColor(Color.parseColor("#FF9800"))

        }
        else
        {
            holder_value.linear.setBackgroundColor(Color.parseColor("#098C0F"))

        }
        //holder.itemview.location_latlong.text=(get_latlong(user_data.latong)).toString()

        holder_value.setOnClickListener {

            intent = Intent(context, Crime_data_fuul_details::class.java)

           // crime_arraylist_tosend_to_full_details_acitivity.clear()
            //  val trips_intent = Intent(this@MainActivity, TripsRecycler::class.java)
          //  crime_arraylist_tosend_to_full_details_acitivity.add(user_data)

            var crime_incidences_data_to_json_string_to_send=change_object_to_json_string(user_data)
          //  val bundle = Bundle()
          //  bundle.putSerializable("user_data", crime_arraylist_tosend_to_full_details_acitivity as Serializable)
            intent.putExtra("crime_incidences_data_to_json_string_to_send",crime_incidences_data_to_json_string_to_send)
            context.startActivity(intent)


            Toast.makeText(it.context,holder_value.tag.toString(), Toast.LENGTH_LONG).show()
        }



    }
    private fun getlatlong(listlang: JSONArray, name: String): String
    {

        var latlong_jsonobject=listlang.getJSONObject(0)
        var latlong=latlong_jsonobject.getString(name)

        return latlong
    }

    private fun get_latlong(listLatLng_todb: String): com.google.android.gms.maps.model.LatLng
    {
        var  listLatLng_todb_array= JSONArray(listLatLng_todb)

        var lat=getlatlong(listLatLng_todb_array,"lat")
        var long=getlatlong(listLatLng_todb_array,"long")
        var marker_location=com.google.android.gms.maps.model.LatLng(lat.toDouble(), long.toDouble())


        return marker_location
    }

    private fun get_time(time: String): String
    {
        var time_changed=""

        try {

            var simple_date_fomart= SimpleDateFormat("yyyyMMddHHmmss")
            var date=simple_date_fomart.parse(time)
            var new_date_fomart= SimpleDateFormat("dd-MM-yyyy HH:mm a")

            time_changed=new_date_fomart.format(date)

        }catch (e: Exception)
        {
            var time_changed=time

        }


        return  time_changed
    }
    private fun change_object_to_json_string(data: crime_incidences_data_class): String
    {
        val crime_incidences_data_to_json_string: String = Gson().toJson(data)

        return  crime_incidences_data_to_json_string
    }

    class ViewHolder(val itemview: View): RecyclerView.ViewHolder(itemview)
    {



    }
}