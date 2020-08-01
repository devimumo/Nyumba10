package com.example.nyumba10.Helper_classes

import android.util.Log
import com.example.nyumba10.Worker.set_message_payload_to_entity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject


class Handle_mesu: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val mes = remoteMessage.data["message"];
        Log.d("extra_information",mes!!)
        val context=applicationContext

var mes_jsonobject=JSONObject(mes)
        var type=mes_jsonobject.getString("type")

       if (type.equals("group"))
       {
           var set_message_payload_instanse=set_message_payload_to_entity()
           set_message_payload_instanse.set_message_payload_to_entity(context,mes)
       }




    }



}