package com.example.nyumba10.roompackages.db_instanse

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.nyumba10.roompackages.Dao.association_chat_dao
import com.example.nyumba10.roompackages.appdatabase.association_chat_db
import com.example.nyumba10.roompackages.entities.association_chat_entity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class association_chat_db_instances {



      fun insert_association_message_payload(context: Context,message_payload: association_chat_entity)
    {

        val db = Room.databaseBuilder( context,
            association_chat_db::class.java, "nyumba10"
        ).build()

        db.association_chat_dao().insertAll(message_payload)
//Log.d("long_value",long_value.toString())
    }

    fun delete_sata_on_table(context: Context)
    {
        val db = Room.databaseBuilder( context,
            association_chat_db::class.java, "nyumba10"
        ).build()

        db.association_chat_dao().delete()
    }

    fun retreive_chats_from_room_database(context: Context): List<association_chat_entity>
    {

            val db = Room.databaseBuilder( context,
                association_chat_db::class.java, "nyumba10"
            ).build()

            var messages= db.association_chat_dao().getAll()


return messages
    }
}