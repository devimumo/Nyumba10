package com.example.nyumba10.Worker

import android.content.Context
import com.example.nyumba10.Dashboard.GroupChat.retreive_chats_from_room_database
import com.example.nyumba10.Dashboard.Peer_to_peer_chat.retreive_chats_from_room_database_for_peer_to_peer_chats
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.example.nyumba10.roompackages.entities.association_chat_entity
import com.example.nyumba10.roompackages.entities.person_to_person_chat_entity
import com.example.nyumba10_person_to_person.roompackages.db_instanse.person_to_person_chat_db_instanse
import org.json.JSONObject
private var get_message_instanse= retreive_chats_from_room_database_for_peer_to_peer_chats()

class set_message_payload_to_peer_to_peer_entity {


        fun set_message_payload_to_entity(context: Context, message_payload: String?)
    {


        var message_payload_jsonobject=JSONObject(message_payload)
        var message=message_payload_jsonobject.getString("mesu")
        var time_send=message_payload_jsonobject.getString("time_send")

        var username=message_payload_jsonobject.getString("username")
        var sender_id_no=message_payload_jsonobject.getString("sender_id_no")
        var unique_id=message_payload_jsonobject.getString("unique_id")



        var peer_to_peer=person_to_person_chat_entity()
        peer_to_peer.message=message
        peer_to_peer.time_created=time_send
        peer_to_peer.username=username
        peer_to_peer.from_id_no=sender_id_no
        peer_to_peer.chats_unique_id=unique_id


        var insert_message_payload_dbinstanse=person_to_person_chat_db_instanse()
        insert_message_payload_dbinstanse.insert_message_payload(context,peer_to_peer)
get_message_instanse.retreive_chats_from_room_database(context,unique_id)

    }

}