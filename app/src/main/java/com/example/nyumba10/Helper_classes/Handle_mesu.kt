package com.example.nyumba10.Helper_classes

import android.util.Log
import com.example.nyumba10.DashboardMapUpdate.get_marker_to_update_details_from_onmessagereceived
import com.example.nyumba10.Worker.set_message_payload_to_entity
import com.example.nyumba10.Worker.set_message_payload_to_peer_to_peer_entity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


private var set_message_payload_instanse=set_message_payload_to_entity()
var set_message_payload_instanse_for_peer_to_peer_chats=set_message_payload_to_peer_to_peer_entity()

val get_marker_to_update_details_from_onmessagereceived_instanse=get_marker_to_update_details_from_onmessagereceived()

class Handle_mesu: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val mes = remoteMessage.data["message"];
        Log.d("extra_information",mes!!)
        val context=applicationContext

var mes_jsonobject=JSONObject(mes)
        var type=mes_jsonobject.getString("type")



        when(type){

            "group"->{
                set_message_payload_instanse.set_message_payload_to_entity(context,mes)
            }
            "map_marker"->{

                CoroutineScope(Dispatchers.IO).launch {

                    get_marker_to_update_details_from_onmessagereceived_instanse.get_marker_to_update_details_from_onmessagereceived(
                        mes
                    )
                }
            }
            "peer_to_peer"->{

                set_message_payload_instanse_for_peer_to_peer_chats.set_message_payload_to_entity(context,mes)

            }
            else->{



            }
        }




    }



}