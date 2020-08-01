package com.example.nyumba10.Dashboard.GroupChat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nyumba10.Dashboard.GroupChat.adapter.association_group_chat_adapter
import com.example.nyumba10.R
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.google.gson.Gson
import kotlinx.android.synthetic.main.group_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
val chats_payload_arraylist = ArrayList<association_chat_messages_dataclass>()

var time_return_value: String=""
class GroupChat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_chat)


get_messages(this)

    }


 private fun get_messages(context: Context)
{
    CoroutineScope(Dispatchers.IO).launch {
        var get_mesus=association_chat_db_instances()
        var messages=get_mesus.retreive_chats_from_room_database(context)

        var messages_to_json= Gson()
        var messages_json=messages_to_json.toJson(messages).toString()
        Log.d("mesu",messages_json.toString())
        withContext(Dispatchers.Main) {

            set_to_recycler(context, messages_json)
        }
    }
    }

    private fun set_to_recycler(context: Context, messagesJson: String) {
              val recycler_view = chats_list_recycler_view

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
        recycler_view.layoutManager = LinearLayoutManager(context)
            adap.notifyDataSetChanged()
            recycler_view.adapter = adap
            (recycler_view.layoutManager as LinearLayoutManager).setStackFromEnd(true)

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
            val time_format = SimpleDateFormat("HH:mm:a")
            time_return_value=time_format.format(sendv)

        }
        return time_return_value
    }

}