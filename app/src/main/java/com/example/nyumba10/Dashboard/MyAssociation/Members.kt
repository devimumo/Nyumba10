package com.example.nyumba10.Dashboard.MyAssociation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_recyclerview_adapter
import com.example.nyumba10.Dashboard.MyAssociation.recycler.Members_data_recyclerview_adapter
import com.example.nyumba10.Dashboard.MyAssociation.recycler.members_data_class
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import kotlinx.android.synthetic.main.fragment_incidences_crimes.view.*
import kotlinx.android.synthetic.main.fragment_incidences_crimes.view.crime_list_recycler_view
import kotlinx.android.synthetic.main.fragment_members.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException

private lateinit var members_view: View
private lateinit var   volley_error_handler_instane: Volley_ErrorListener_Handler
private   var  members_data_arraylist= ArrayList<members_data_class>()
private  var designation: String="MEMBER"
private var adap: Members_data_recyclerview_adapter? =null
class Members : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        members_view= inflater.inflate(R.layout.fragment_members, container, false)

        val MyPreferences = "mypref"
        val sharedPreferences =
            context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        var association_id= sharedPreferences?.getString("association_id","");
         designation= sharedPreferences?.getString("designation","MEMBER").toString();


        if (association_id.equals(""))
        {
            members_view.members_progressBar.visibility=View.GONE
        Toast.makeText(members_view.context,"No members data",Toast.LENGTH_LONG).show()
        }else
        {
            get_association_members(association_id, members_view)

        }
   return  members_view
    }


    private fun get_association_members(associationId: String?,view: View)
    {

        val requestQueue = Volley.newRequestQueue(view.context)

        var url="https://daudi.azurewebsites.net/nyumbakumi/association_members/get_my_association_members.php?association_id="+associationId

        var stringRequest=object : StringRequest(Method.GET,url,Response.Listener {response->

            try {

                Log.d("membersa_",response.toString())

                var response_object=JSONObject(response)
                var status=response_object.getString("status")
                var data=response_object.getString("data")

              //  Log.d("members_data_array",data.toString())
               if (status.equals("success"))
               {
                     var members_data_array_object=JSONObject(data)
                     var  members_data_array=members_data_array_object.getJSONArray("members_data")
                   Log.d("members_data_array",members_data_array.toString())

                  iterate_members_data(members_data_array)

               }
               else
               {
                   members_view.members_progressBar.visibility=View.GONE

                   Toast.makeText(members_view.context,"No members data",Toast.LENGTH_LONG).show()


               }
           }catch (e: JSONException)
           {
               Log.d("JSONException",e.toString())
               members_view.members_progressBar.visibility=View.GONE

           }
            catch (e: ParseException)
            {
                Log.d("ParseException",e.toString())

                members_view.members_progressBar.visibility=View.GONE

            }



        },Response.ErrorListener {

          volley_error_handler_instane.check_error(it,view)
            members_view.members_progressBar.visibility=View.GONE

        })
        {}

        requestQueue.add(stringRequest)
    }

    private fun iterate_members_data(members_data_jsonarray: JSONArray)
    {

        var members_list_recycler_view = members_view.members_list_recycler_view

        members_list_recycler_view?.layoutManager = LinearLayoutManager(members_view.context)
        (members_list_recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(true)
        members_data_arraylist.clear()
        var length=members_data_jsonarray.length()

        if (length<1)
        {

        }else
        {

            for (i in 0..length-1)
            {
               var data_at_i=members_data_jsonarray.getJSONObject(i)

               var mobile= data_at_i.getString("mobile_no")

                var data=members_data_class(
                    data_at_i.getString("firstname") + "  " + data_at_i.getString("lastname"), mobile,data_at_i.getString("account_status")
                )


                if (check_list(mobile))
                {
                    members_data_arraylist.add(data)

                }

            }

             adap = members_view.context?.let {
                Members_data_recyclerview_adapter(it, members_data_arraylist, members_view)
            }
            members_list_recycler_view?.layoutManager = LinearLayoutManager(context)
            adap?.notifyDataSetChanged()
            members_list_recycler_view?.adapter = adap
            //(members_list_recycler_view.layoutManager as LinearLayoutManager).reverseLayout=true
            (members_list_recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(false)

            members_view.members_progressBar.visibility=View.GONE


            Log.d("members_data_arraylist", members_data_arraylist.toString())

        }



    }

    private fun check_list(phonenumber: String):Boolean
    {
        var return_value=false
        val MyPreferences = "mypref"
        val sharedPreferences =
            context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        var mobile_no= sharedPreferences?.getString("phone_number","");
        if (!(mobile_no).equals(phonenumber))
        {
            return_value=true

        }
        return  return_value
    }

}