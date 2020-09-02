package com.example.nyumba10.Dashboard.Peer_to_peer_chat

import android.content.Context
import android.util.Log
import android.view.View
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.example.nyumba10_person_to_person.roompackages.db_instanse.person_to_person_chat_db_instanse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class retreive_chats_from_room_database_for_peer_to_peer_chats {

    fun retreive_chats_from_room_database(context: Context,unique_id: String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            var get_messages= person_to_person_chat_db_instanse()
            var messages=get_messages.retreive_person_to_person_chats_from_room_database(context,unique_id)

            var messages_to_json= Gson()
            var messages_json=messages_to_json.toJson(messages).toString()
            Log.d("mesu",messages_json.toString())
            withContext(Dispatchers.Main) {
                 var instanse=Peer_to_peer_chat()

                instanse.set_to_recycler(context, messages_json)
            }
        }
    }

}