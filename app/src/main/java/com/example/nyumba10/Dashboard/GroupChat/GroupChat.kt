package com.example.nyumba10.Dashboard.GroupChat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Dashboard.GroupChat.adapter.association_group_chat_adapter
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import kotlinx.android.synthetic.main.group_chat.*
import kotlinx.android.synthetic.main.group_chat.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private var rootView: View? = null

val chats_payload_arraylist = ArrayList<association_chat_messages_dataclass>()
var get_message_instanse=retreive_chats_from_room_database()
var time_return_value: String=""
class GroupChat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_chat)
         rootView = window.decorView.rootView


        val actionBar = supportActionBar
        actionBar!!.title = "Association Group Chat"

get_message_instanse.retreive_chats_from_room_database(applicationContext)

    }

    fun set_to_recycler(context: Context, messagesJson: String) {

        var recycler_view = rootView?.chats_list_recycler_view

        recycler_view?.layoutManager = LinearLayoutManager(context)
        (recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(true)

        chats_payload_arraylist.clear()

        var messagesJson_array=JSONArray(messagesJson)

        for (i in 0..messagesJson_array.length()-1)
        {


            var message_at_position_jsonobect=messagesJson_array.getJSONObject(i)

            var time_send=message_at_position_jsonobect.getString("time_created")
            var time_value=time_value(time_send)
            Log.d("time_send",time_value)
            var chats_data=  association_chat_messages_dataclass(
                message_at_position_jsonobect.getString("message"),
                time_value,
              message_at_position_jsonobect.getString("username"),
              message_at_position_jsonobect.getString("from_id_no")
            )


            var adap =  association_group_chat_adapter(chats_payload_arraylist, context)

            chats_payload_arraylist.add(chats_data)
        recycler_view?.layoutManager = LinearLayoutManager(context)
            adap.notifyDataSetChanged()
            recycler_view?.adapter = adap
            (recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(true)

        }






    }


    private fun time_value(timeSend: String): String
    {

        var time_value=SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

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

    fun onclick(view: View) {

        when(view.id){


            R.id.send->{


                send_message_to_group_chat(view,chat_message.text.toString())
            }
        }
    }


    private fun send_message_to_group_chat(view: View,message: String)
    {
        val MyPreferences = "mypref"
        val sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val id_no = sharedPreferences.getString("id_no", "")
        val firstname = sharedPreferences.getString("firstname", "")
        val sender_mobile_no=sharedPreferences.getString("phone_number","")
        val association_id=sharedPreferences.getString("association_id","")


        var time_value=SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())


        var remote_json = JSONObject()
        try {
            remote_json.put("mesu", message)
            remote_json.put("time_send", time_value)
            remote_json.put("username", firstname)
            remote_json.put("sender_id_no", id_no)
            remote_json.put("phone_number",sender_mobile_no)
            remote_json.put("association_id",association_id)
            remote_json.put("unique_id", id_no)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val url ="https://daudi.azurewebsites.net/nyumbakumi/fcm/fcm.php"

        var stringRequest: StringRequest=object : StringRequest(Method.POST,url,Response.Listener {response ->


            try {

                Log.d("Parse",response)

                var response_jsonobject=JSONObject(response)
                var status=response_jsonobject.getString("response")
                if (status.equals("successful"))
                {

                }
                else{

                }            } catch (e: JSONException) {

                Log.d("jsonJSONException",e.toString())

                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
                Log.d("ParseException",e.toString())

            }




        },
        Response.ErrorListener {
            var instanse=Volley_ErrorListener_Handler()
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