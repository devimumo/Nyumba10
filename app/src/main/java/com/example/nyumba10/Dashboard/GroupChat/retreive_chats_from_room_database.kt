package com.example.nyumba10.Dashboard.GroupChat

import android.content.Context
import android.util.Log
import android.view.View
import com.example.nyumba10.roompackages.db_instanse.association_chat_db_instances
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class retreive_chats_from_room_database {

    fun retreive_chats_from_room_database(context: Context)
    {

        CoroutineScope(Dispatchers.IO).launch {
            var get_messages= association_chat_db_instances()
            var messages=get_messages.retreive_chats_from_room_database(context)

            var messages_to_json= Gson()
            var messages_json=messages_to_json.toJson(messages).toString()
            Log.d("mesu",messages_json.toString())
            withContext(Dispatchers.Main) {
                 var instanse=GroupChat()

                instanse.set_to_recycler(context, messages_json)
            }
        }
    }

}