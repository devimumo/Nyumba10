package com.example.nyumba10.Worker

import android.content.Context
import com.example.nyumba10.Dashboard.GroupChat.retreive_chats_from_room_database
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.example.nyumba10.roompackages.entities.association_chat_entity
import org.json.JSONObject
private var get_message_instanse= retreive_chats_from_room_database()

class set_message_payload_to_entity {


        fun set_message_payload_to_entity(context: Context, message_payload: String?)
    {


        var message_payload_jsonobject=JSONObject(message_payload)
        var message=message_payload_jsonobject.getString("mesu")
        var time_send=message_payload_jsonobject.getString("time_send")

        var username=message_payload_jsonobject.getString("username")
        var sender_id_no=message_payload_jsonobject.getString("sender_id_no")



        var association_entity=association_chat_entity()
        association_entity.message=message
        association_entity.time_created=time_send
        association_entity.username=username
        association_entity.from_id_no=sender_id_no


        var insert_message_payload_dbinstanse=association_chat_db_instances()
        insert_message_payload_dbinstanse.insert_association_message_payload(context,association_entity)
get_message_instanse.retreive_chats_from_room_database(context)
    }

}