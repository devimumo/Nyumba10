package com.example.nyumba10.Worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import com.example.nyumba10.Dashboard.GroupChat.GroupChat
import com.example.nyumba10.Dashboard.GroupChat.retreive_chats_from_room_database
import com.example.nyumba10.Dashboard.GroupChat.time_return_value
import com.example.nyumba10.R
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.example.nyumba10.roompackages.entities.Association_chat_entity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

private var get_message_instanse= retreive_chats_from_room_database()

class set_message_payload_to_entity {


          fun set_message_payload_to_entity(context: Context, message_payload: String?,title: String,subtitle: String)
    {


        var message_payload_jsonobject=JSONObject(message_payload!!)
        var message=message_payload_jsonobject.getString("mesu")
        var time_send=message_payload_jsonobject.getString("time_send")

        var username=message_payload_jsonobject.getString("username")
        var sender_id_no=message_payload_jsonobject.getString("sender_id_no")
        var instance_id=message_payload_jsonobject.getString("instance_id")
        var type=message_payload_jsonobject.getString("type")
        var title_value=message_payload_jsonobject.getString("title")
        var subtitle_value=message_payload_jsonobject.getString("subtitle")


        val MyPreferences = "mypref"
        val sharedPreferences =context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

        var instance_id_value=sharedPreferences.getString("instance_id_value","")

        Log.d("instance_id_value6",title_value+"--"+subtitle_value+"-----"+instance_id+"----"+instance_id_value)


        if (instance_id!=instance_id_value ) {


            handle_notification(title_value,message,context,type,time_send)

            var association_entity = Association_chat_entity()
            association_entity.message = message
            association_entity.time_created = time_send
            association_entity.username = username
            association_entity.from_id_no = sender_id_no


            CoroutineScope(Dispatchers.IO).launch {


            var insert_message_payload_dbinstanse = association_chat_db_instances()


           var insert_value= insert_message_payload_dbinstanse.insert_association_message_payload(
                context,
                association_entity
            )

            if (insert_value>=1)
            {



    withContext(Dispatchers.Main) {
    val group_chat = GroupChat()

    val time_value_ = time_value(time_send).toString()

    val chats_data_class_value = association_chat_messages_dataclass(
        message, time_value_, username, sender_id_no
    )


    group_chat.set_to_recycler_from_local_phone(chats_data_class_value, context)
}

                }





            }
           // get_message_instanse.retreive_chats_from_room_database(context)
        }

    }


    private fun handle_notification(
        title: String,
        subtitle: String,
        context: Context,
        type: String,
        time_send: String
    )
    {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, GroupChat::class.java).apply {
           // flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
Log.d("notifications",title+"---"+subtitle)
        val builder = NotificationCompat.Builder(context, "Chat_notifications")
            .setSmallIcon(R.drawable.nyumba)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setGroup(type)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)

Log.d("timeeee",time_send)
    //    var notification_id: Int?=time_send.toInt()

        val noti = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        noti.notify(0, builder.build())

try {

}catch (notification_error: NumberFormatException)
{


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
            time_return_value ="yesterday"

        }
        else if(days>1)
        {
            val days_format = SimpleDateFormat("MM:dd:yyyy")

            time_return_value =days_format.format(sendv)

        }
        else
        {
            val time_format = SimpleDateFormat("HH:mm a")
            time_return_value =time_format.format(sendv)

        }
        return time_return_value
    }
    private fun get_firebase_instance_id(): String
    {
        var token_value =""


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
              //  return@OnCompleteListener
            }
            else
            {
                val token = task.result
                token_value=token.toString()
            }

            // Get new FCM registration token


            // Log and toast
            //  val msg = getString(com.example.nyumba10.R.string.msg_token_fmt, token)
            Log.d("firebase_instance_idss",token_value.toString())
            //   Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

return  token_value

    }

}