package com.example.nyumba10.Dashboard.MyAssociation.recycler

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.Peer_to_peer_chat
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import kotlinx.android.synthetic.main.fragment_members.view.*
import kotlinx.android.synthetic.main.members_data_recyclerview_layout.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.util.*


private var this_view: View?=null
private  var designation: String="MEMBER"

class Members_data_recyclerview_adapter(
    var context: Context,
    var members_data_arraylist: ArrayList<members_data_class>,
    var view: View
) :
    RecyclerView.Adapter<Members_data_recyclerview_adapter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {

var view=LayoutInflater.from(parent.context).inflate(
    R.layout.members_data_recyclerview_layout,
    parent,
    false
)

        return  Viewholder(view)
    }

    override fun getItemCount(): Int {


return members_data_arraylist.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        var data=members_data_arraylist.get(position)
        var holder_=holder.itemView
        val MyPreferences = "mypref"
        val sharedPreferences =context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        var mobile_no= "254"+sharedPreferences?.getString("phone_number", "");
        designation= sharedPreferences?.getString("designation", "MEMBER").toString()
        var account_status=data.status


        if ((position % 2) == 0) {
            holder_.outside_layer.setBackgroundColor(Color.parseColor("#A6FFA6"))
        }

        if (designation.equals("ADMIN"))
        {

            if (account_status.equals("inactive"))
            {
                holder_.activate_account.visibility=View.VISIBLE
                holder_.more_options.visibility=View.GONE

            }
            else{
                holder_.more_options.visibility=View.VISIBLE

            }


            holder_.activate_account.setOnClickListener {

                set_active_alert(data.username,data.phone_number,members_data_arraylist,position,view,holder_)

            }
            holder_.more_options.setOnClickListener {


                set_inactive_alert(data.username,data.phone_number,members_data_arraylist,position,view,holder_)

            }

            holder_.member_names.text=data.username
            holder_.mobile_no.text="+254"+data.phone_number
        }
        else
        {
            if (account_status.equals("active"))
            {
                holder_.member_names.text=data.username
                holder_.mobile_no.text="+254"+data.phone_number
            }
            holder_.visibility=View.GONE
        }
       // val i: Int = mobile_no.toInt()
       // val s: Int = data.phone_number.toInt()


        var unique_id_for_intent=get_peer_to_peer_chat_unique_id(mobile_no, data.phone_number)


        holder_.text_member.setOnClickListener {
           var intent_to_person_to_person=Intent(context, Peer_to_peer_chat::class.java)
            intent_to_person_to_person.putExtra("receiver_phone_no", data.phone_number)
            intent_to_person_to_person.putExtra("receiver_username", data.username)
            intent_to_person_to_person.putExtra("unique_id", unique_id_for_intent)


            context.startActivity(intent_to_person_to_person)
        }



    }

    private fun set_active_alert(
        username: String,
        phoneNumber: String,
        membersDataArraylist: ArrayList<members_data_class>,
        position: Int,
        view: View,
        holder_: View
    ) {



        // Use the Builder class for convenient dialog construction
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure Set account for "+username +" to active? ")
            .setPositiveButton("YES",
                DialogInterface.OnClickListener {
                        dialog, id ->
                    handle_more_options(phoneNumber, members_data_arraylist, position,"active",holder_)
                })
            .setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()




    }


    class Viewholder(itemview: View): RecyclerView.ViewHolder(itemview)
    {}


    private fun handle_more_options(
        mobile_no: String,
        members_data_arraylist: ArrayList<members_data_class>,
        position: Int,state: String,handler: View
    )
    {

        view.members_progressBar.visibility=View.VISIBLE
        view.members_list_recycler_view.isClickable=false


        val requestQueue = Volley.newRequestQueue(context)
        val url="https://daudi.azurewebsites.net/nyumbakumi/login/update_member_status.php?mobile_no="+mobile_no+"&state="+state;
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            url,
            Response.Listener { response ->

                try {

                    var response_json_object = JSONObject(response)
                    var response = response_json_object.getString("response")


                    if (response.equals("successful")) {


                        view.members_progressBar.visibility=View.GONE
                        view.members_list_recycler_view.isClickable=true
                        members_data_arraylist.removeAt(position)

                    } else {
                        view.members_progressBar.visibility=View.GONE
                        view.members_list_recycler_view.isClickable=true

                    }

                } catch (e: JSONException) {
                    Log.d("JSONException", e.toString())
                    view.members_progressBar.visibility=View.GONE
                    view.members_list_recycler_view.isClickable=true

                } catch (e: ParseException) {
                    Log.d("ParseException", e.toString())
                    view.members_progressBar.visibility=View.GONE
                    view.members_list_recycler_view.isClickable=true

                }


            },
            Response.ErrorListener {

                view.members_progressBar.visibility=View.GONE
                view.members_list_recycler_view.isClickable=true

                val err = Volley_ErrorListener_Handler()

                 err.check_error(it,view)
            })
        {
            /*
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                                   params["mobile_no"] = mobile_no.toString()
                return params
            }*/
        }
        requestQueue.add(stringRequest)
    }



    private fun set_inactive_alert(
        name: String,
        phone: String,
        members_data_arraylist: ArrayList<members_data_class>,
        position: Int,
        view: View,
        holder_: View
    ) {

                // Use the Builder class for convenient dialog construction
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setMessage("Are you sure Set account for "+name +" to inactive? ")
                    .setPositiveButton("YES",
                        DialogInterface.OnClickListener {
                                dialog, id ->
                            handle_more_options(phone, members_data_arraylist, position,"inactive",holder_)
                        })
                    .setNegativeButton("cancel",
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
                // Create the AlertDialog object and return it
                 builder.create()
        builder.show()


    }
    private fun get_peer_to_peer_chat_unique_id(
        sender_mobile_no: String,
        receiver_mobile_no: String
    ): String
    {
        var unique_id_return_value=""


        if (receiver_mobile_no > sender_mobile_no)
        {
            unique_id_return_value = receiver_mobile_no.toString()+sender_mobile_no.toString()
        }
        else
        {
            unique_id_return_value =sender_mobile_no.toString()+ receiver_mobile_no.toString()
        }
        return  unique_id_return_value
    }
}