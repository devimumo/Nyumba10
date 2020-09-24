package com.example .nyumba10.Dashboard.Peer_to_peer_chat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.GroupChat.adapter.association_group_chat_adapter
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.adapter.peer_to_peer_chat_recycler_adapter
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.retreive_chats_from_room_database_for_peer_to_peer_chats
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.Worker.set_message_payload_to_peer_to_peer_entity
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import kotlinx.android.synthetic.main.activity_peer_to_peer_chat.*
import kotlinx.android.synthetic.main.activity_peer_to_peer_chat.view.*
import kotlinx.android.synthetic.main.group_chat.*
import kotlinx.android.synthetic.main.group_chat.chat_message
import kotlinx.android.synthetic.main.group_chat.view.*
import kotlinx.android.synthetic.main.group_chat.view.chats_list_recycler_view
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private lateinit var unique_id: String
private var rootView: View? = null
private  lateinit var text_no_data: TextView
private lateinit var receiver_phone_no: String
private lateinit var receiver_username: String
var set_message_payload_instanse_for_peer_to_peer_chats= set_message_payload_to_peer_to_peer_entity()
private val chats_payload_arraylist = ArrayList<association_chat_messages_dataclass>()

private var get_message_instanse= retreive_chats_from_room_database_for_peer_to_peer_chats()
private var time_return_value: String=""
private  lateinit var recycler_view: RecyclerView
private lateinit var unique_id_value_from_members: String

class Peer_to_peer_chat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peer_to_peer_chat)


        var intent=intent
receiver_phone_no= intent.getStringExtra("receiver_phone_no").toString()
        receiver_username= intent.getStringExtra("receiver_username").toString()
        unique_id_value_from_members= intent.getStringExtra("unique_id").toString()
        //  actionBar?.title = receiver_phone_no
        val actionBar = supportActionBar
        actionBar!!.title = receiver_username
        rootView = window.decorView.rootView

        Log.d("receiver_phone_no", receiver_phone_no)

        get_message_instanse.retreive_chats_from_room_database(rootView!!.context,unique_id_value_from_members)
    }


    suspend fun set_to_recycler(context: Context, messagesJson: String) {

         recycler_view = rootView?.chats_list_recycler_view!!
        recycler_view?.layoutManager = LinearLayoutManager(context)
        (recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(true)

        chats_payload_arraylist.clear()

        var messagesJson_array= JSONArray(messagesJson)

        if (messagesJson_array.length()!=0) {
            for (i in 0..messagesJson_array.length() - 1) {


                var message_at_position_jsonobect = messagesJson_array.getJSONObject(i)

                var time_send = message_at_position_jsonobect.getString("time_created")
                var time_value = time_value(time_send)
                Log.d("time_send", time_value)
                var chats_data = association_chat_messages_dataclass(
                    message_at_position_jsonobect.getString("message"),
                    time_value,
                    message_at_position_jsonobect.getString("username"),
                    message_at_position_jsonobect.getString("from_id_no")
                )


                var adap = peer_to_peer_chat_recycler_adapter(chats_payload_arraylist, context)

                chats_payload_arraylist.add(chats_data)
                recycler_view?.layoutManager = LinearLayoutManager(context)
                adap.notifyDataSetChanged()
                recycler_view?.adapter = adap
                (recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(true)

            }

        }
        else
        {

text_no_data.visibility=View.VISIBLE
          //
        }

    }

    private fun time_value(timeSend: String): String
    {

        var time_value= SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

        val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        var sendv=simpleDateFormat.parse(timeSend)
        var timev=simpleDateFormat.parse(time_value)

        var differe=timev.time-sendv.time
        var days=differe/(1000*60*60*24)
        var hour=(differe-(1000*60*60*24*days))/(1000*60*60)
        var mins=(differe - (1000*60*60*24*days) - (1000*60*60*hour)) / (1000*60);
        if (days>=1)
        {
            time_return_value="yesterday"

        }
        else if(days>1)
        {
            val days_format = SimpleDateFormat("MM:dd:yyyy")

            time_return_value=days_format.format(sendv)

        }
        else
        {
            val time_format = SimpleDateFormat("HH:mm a")
            time_return_value=time_format.format(sendv)

        }
        return time_return_value
    }

    fun onclick_peer_peer(view: View) {

        when(view.id){

            R.id.send->{

                send_message_(view,chat_message.text.toString())
            }
        }
    }


    private fun send_message_(view: View,message: String)
    {
        val MyPreferences = "mypref"
        val sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val id_no = sharedPreferences.getString("id_no", "")
        val firstname = sharedPreferences.getString("firstname", "")
       val sender_mobile_no=sharedPreferences.getString("phone_number","")
        var time_value= SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val association_id=sharedPreferences.getString("association_id","")

        Log.d("receiver_phone_no", sender_mobile_no!!)

        if (sender_mobile_no != null) {
            if (receiver_phone_no>sender_mobile_no)
            {
unique_id= receiver_phone_no.toString()+sender_mobile_no.toString()
            }
            else
            {
                unique_id=sender_mobile_no.toString()+ receiver_phone_no.toString()
            }
        }
        var remote_json = JSONObject()
        try {
            remote_json.put("mesu", message)
            remote_json.put("time_send", time_value)
            remote_json.put("username", firstname)
            remote_json.put("sender_id_no", id_no)
            remote_json.put("phone_number",sender_mobile_no)
            remote_json.put("receiver_phone_no", receiver_phone_no)
            remote_json.put("unique_id", unique_id)

            remote_json.put("association_id",association_id)


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val url ="https://daudi.azurewebsites.net/nyumbakumi/fcm/fcm.php"
        rootView?.context?.let {
            set_message_payload_instanse_for_peer_to_peer_chats.set_message_payload_to_entity(
                it,remote_json.toString())
        }


        var stringRequest: StringRequest =object : StringRequest(
            Method.POST,url, Response.Listener { response ->

Log.d("response",response)
            try {
                var response_jsonobject= JSONObject(response)
                var status=response_jsonobject.getString("response")
                if (status.equals("successful"))
                {

                    chat_message.text.clear()

                }
                else{

                }
            } catch (e: JSONException) {

                Log.d("jsonJSONException",e.toString())

                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
                Log.d("ParseException",e.toString())

            }




        },
            Response.ErrorListener {
                var instanse= Volley_ErrorListener_Handler()
                instanse.check_error(it,view)

            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params.put("message_payload_json",remote_json.toString())

                return params
            }

        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}