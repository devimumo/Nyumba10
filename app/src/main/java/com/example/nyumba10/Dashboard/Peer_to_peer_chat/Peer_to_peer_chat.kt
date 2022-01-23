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
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.adapter.peer_to_peer_chat_recycler_adapter
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.Worker.set_message_payload_to_peer_to_peer_entity
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.example.nyumba10.roompackages.entities.Association_chat_entity
import com.example.nyumba10.roompackages.entities.Person_to_person_chat_entity
import com.example.nyumba10_person_to_person.roompackages.db_instanse.person_to_person_chat_db_instanse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_peer_to_peer_chat.*
import kotlinx.android.synthetic.main.fragment_members.*
import kotlinx.android.synthetic.main.group_chat.chat_message
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
private  var  id_no: String=""
private  var  firstname: String=""
private  var  time_value: String=""
private  var  association_id: String=""
private  var  sender_mobile_no: String=""
private  var  token_value: String=""


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


        val MyPreferences = "mypref"
        val sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

         id_no = sharedPreferences.getString("id_no", "").toString()
         firstname = sharedPreferences.getString("firstname", "").toString()
         sender_mobile_no=sharedPreferences.getString("phone_number","").toString()
         association_id=sharedPreferences.getString("association_id","").toString()
         time_value= SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())



        Log.d("receiver_phone_no", receiver_phone_no)

        get_message_instanse.retreive_chats_from_room_database(rootView!!.context,unique_id_value_from_members)
    }


    suspend fun set_to_recycler(context: Context, messagesJson: String) {

         recycler_view = peer_to_peer_chats_list_recycler_view!!
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


                var adap = peer_to_peer_chat_recycler_adapter(chats_payload_arraylist, context,
                    rootView!!)

                chats_payload_arraylist.add(chats_data)
                recycler_view?.layoutManager = LinearLayoutManager(context)
                adap.notifyDataSetChanged()
                recycler_view?.adapter = adap
                (recycler_view?.layoutManager as LinearLayoutManager).setStackFromEnd(true)

            }

        }
        else
        {

//text_no_data.visibility=View.VISIBLE
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

                var message_from_edit_text=chat_message.text.toString()
                var chats_data = association_chat_messages_dataclass(
                    message_from_edit_text,
                    time_value(time_value),
                    firstname ,
                    id_no)

                rootView?.let { set_to_recycler_from_local_phone(it.context,chats_data) }

                CoroutineScope(Dispatchers.IO).launch {
                    if (insert_to_db()>=1)
                    {

                        withContext(Dispatchers.Main) {
                            chat_message.setText("")

                        }
                    }

                }


                send_message_(view,message_from_edit_text, association_id)
            }
        }
    }


    private fun send_message_(view: View, message: String, association_id: String)
    {

        val MyPreferences = "mypref"
        val sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

        var instance_id_value=sharedPreferences.getString("instance_id_value","")


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
            remote_json.put("instance_id", instance_id_value)

            remote_json.put("association_id",
                com.example.nyumba10.Dashboard.Peer_to_peer_chat.association_id
            )


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val url ="https://daudi.azurewebsites.net/nyumbakumi/fcm/fcm.php"
        rootView?.context?.let {
            set_message_payload_instanse_for_peer_to_peer_chats.set_message_payload_to_entity(
                it,remote_json.toString(),""!!,""!!)
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

    private fun insert_to_db(): Long
    {


        var message_from_edit_text=chat_message.text.toString()
        var peer_to_peer = Person_to_person_chat_entity()
        peer_to_peer.message = message_from_edit_text
        peer_to_peer.time_created = time_value
        peer_to_peer.username = firstname
        peer_to_peer.from_id_no = id_no
        peer_to_peer.chats_unique_id = sender_mobile_no+ receiver_phone_no


        var insert_message_payload_dbinstanse = person_to_person_chat_db_instanse()
        var insert_value=
            insert_message_payload_dbinstanse.insert_message_payload(rootView!!.context, peer_to_peer)


       // var insert_message_payload_dbinstanse = association_chat_db_instances()



        return  insert_value
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
             token_value=token.toString()

            // Log and toast
            //  val msg = getString(com.example.nyumba10.R.string.msg_token_fmt, token)
            Log.d("firebase_instance_ids",token.toString())
            //   Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })


return token_value

    }

     fun set_to_recycler_from_local_phone(context: Context,chats_data:association_chat_messages_dataclass )
    {

        var recycler_views= peer_to_peer_chats_list_recycler_view
        var adap = rootView?.let {
            peer_to_peer_chat_recycler_adapter(chats_payload_arraylist, context,
                rootView!!)
        }

        chats_payload_arraylist.add(chats_data)
        recycler_views?.layoutManager = LinearLayoutManager(context)
        adap?.notifyDataSetChanged()
        recycler_views?.adapter = adap
        (recycler_views?.layoutManager as LinearLayoutManager).setStackFromEnd(true)
    }
}