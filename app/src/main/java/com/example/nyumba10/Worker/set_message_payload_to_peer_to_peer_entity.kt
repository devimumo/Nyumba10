package com.example.nyumba10.Worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nyumba10.Dashboard.GroupChat.GroupChat
import com.example.nyumba10.Dashboard.GroupChat.time_return_value
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.Peer_to_peer_chat
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.retreive_chats_from_room_database_for_peer_to_peer_chats
import com.example.nyumba10.R
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import com.example.nyumba10.roompackages.entities.Person_to_person_chat_entity
import com.example.nyumba10_person_to_person.roompackages.db_instanse.person_to_person_chat_db_instanse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

private var get_message_instanse= retreive_chats_from_room_database_for_peer_to_peer_chats()

class set_message_payload_to_peer_to_peer_entity {


        fun set_message_payload_to_entity(context: Context, message_payload: String?,title: String,subtitle: String)
    {


        var message_payload_jsonobject=JSONObject(message_payload)
        var message=message_payload_jsonobject.getString("mesu")
        var time_send=message_payload_jsonobject.getString("time_send")

        var username=message_payload_jsonobject.getString("username")
        var sender_id_no=message_payload_jsonobject.getString("sender_id_no")
        var unique_id=message_payload_jsonobject.getString("unique_id")
        var instance_id=message_payload_jsonobject.getString("instance_id")

        val MyPreferences = "mypref"
        val sharedPreferences =context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

        var instance_id_value=sharedPreferences.getString("instance_id_value","")

        Log.d("instance_id_value",instance_id+"----"+instance_id_value)


        CoroutineScope(Dispatchers.IO).launch {


        if (instance_id!=instance_id_value ) {

handle_notification(username,message,context)
            var peer_to_peer = Person_to_person_chat_entity()
            peer_to_peer.message = message
            peer_to_peer.time_created = time_send
            peer_to_peer.username = username
            peer_to_peer.from_id_no = sender_id_no
            peer_to_peer.chats_unique_id = unique_id


            var insert_message_payload_dbinstanse = person_to_person_chat_db_instanse()
            insert_message_payload_dbinstanse.insert_message_payload(context, peer_to_peer)

              withContext(Dispatchers.Main)
{
    //get_message_instanse.retreive_chats_from_room_database(context, unique_id)
    val peer_to_peer_chat = Peer_to_peer_chat()

    val time_value_ = time_value(time_send).toString()

    val chats_data_class_value = association_chat_messages_dataclass(
        message, time_value_, username, sender_id_no)

peer_to_peer_chat.set_to_recycler_from_local_phone(context,chats_data_class_value)
}
        }}
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

    private fun handle_notification(title: String, subtitle: String, context: Context)
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
            .setAutoCancel(true)

        val noti = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        noti.notify(0, builder.build())

    }


}