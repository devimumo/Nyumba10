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
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.adapter.peer_to_peer_chat_recycler_adapter
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.example.nyumba10.roompackages.entities.Association_chat_entity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.group_chat.*
import kotlinx.android.synthetic.main.group_chat.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private var rootView: View? = null

private  var  id_no: String=""
private  var  firstname: String=""
private  var  time_value: String=""
private  var  association_id: String=""
private  var  sender_mobile_no: String=""

val chats_payload_arraylist = ArrayList<association_chat_messages_dataclass>()
var get_message_instanse=retreive_chats_from_room_database()
var time_return_value: String=""
private  var  token_value: String=""

class GroupChat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_chat)
         rootView = window.decorView.rootView


        val actionBar = supportActionBar
        actionBar!!.title = "Association Group Chat"


        val MyPreferences = "mypref"
        val sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        id_no = sharedPreferences.getString("id_no", "").toString()
        firstname = sharedPreferences.getString("firstname", "").toString()
        sender_mobile_no =sharedPreferences.getString("phone_number","").toString()
        association_id =sharedPreferences.getString("association_id","").toString()
        time_value = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

get_message_instanse.retreive_chats_from_room_database(applicationContext)

    }

    fun set_to_recycler(context: Context, messagesJson: String) {

        var recycler_view = rootView?.chats_list_recycler_view

        recycler_view?.layoutManager = LinearLayoutManager(context)
        (recycler_view!!.layoutManager as LinearLayoutManager).setStackFromEnd(true)

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

                var message_from_edit_text=chat_message.text.toString()
                var chats_data = association_chat_messages_dataclass(
                    message_from_edit_text,
                    time_value(time_value),
                    firstname,
                    id_no
                )

                set_to_recycler_from_local_phone(chats_data,this)


                CoroutineScope(Dispatchers.IO).launch {
                    if (insert_to_db()>=1)
                    {

                        withContext(Dispatchers.Main) {
                            chat_message.setText("")

                        }
                    }

                }


                send_message_to_group_chat(view,chat_message.text.toString())
            }
        }
    }

    private fun insert_to_db(): Long
    {

        var message_from_edit_text=chat_message.text.toString()
        var association_entity = Association_chat_entity()
        association_entity.message = message_from_edit_text
        association_entity.time_created = time_value
        association_entity.username = firstname
        association_entity.from_id_no = id_no


        var insert_message_payload_dbinstanse = association_chat_db_instances()


        var insert_value= insert_message_payload_dbinstanse.insert_association_message_payload(
            this,
            association_entity
        )
    return insert_value

    }

    fun set_to_recycler_from_local_phone(chats_data:association_chat_messages_dataclass,context: Context )
    {

        var recycler_view = rootView?.chats_list_recycler_view

        Log.d("chats_payload_arraylist", chats_payload_arraylist.toString())
        var adap = peer_to_peer_chat_recycler_adapter(chats_payload_arraylist, context, rootView!!)

        chats_payload_arraylist.add(chats_data)
        recycler_view!!.layoutManager = LinearLayoutManager(this)
        adap.notifyDataSetChanged()
        recycler_view.adapter = adap
        (recycler_view.layoutManager as LinearLayoutManager).setStackFromEnd(true)
    }

    private fun get_firebase_instance_id(): String
    {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            token_value =token.toString()

            // Log and toast
            //  val msg = getString(com.example.nyumba10.R.string.msg_token_fmt, token)
            Log.d("firebase_instance_ids",token.toString())
            //   Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })


        return token_value

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

        Log.d("asscoiiid",association_id)

        var time_value=SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

        var instance_id_value=sharedPreferences.getString("instance_id_value","instance_id_value")

        var remote_json = JSONObject()
        try {

            remote_json.put("app_name","com.example.nyumba10")
            remote_json.put("type_of_conversation","group_chat")
            remote_json.put("mesu", message)
            remote_json.put("time_send", time_value)
            remote_json.put("username", firstname)
            remote_json.put("sender_id_no", id_no)
            remote_json.put("phone_number",sender_mobile_no)
            remote_json.put("association_id",association_id)
            remote_json.put("unique_id", id_no)
            remote_json.put("instance_id", instance_id_value)

            Log.d("instance_id_value4",instance_id_value)

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
                chat_message.text.clear()
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