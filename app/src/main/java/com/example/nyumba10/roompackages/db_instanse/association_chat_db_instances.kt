package com.example.nyumba10.roompackages.db_instanse

import android.content.Context
import androidx.room.Room
import com.example.nyumba10.roompackages.appdatabase.Association_chat_db
import com.example.nyumba10.roompackages.entities.Association_chat_entity

class association_chat_db_instances {



      fun insert_association_message_payload(context: Context,message_payload: Association_chat_entity): Long {

        val db = Room.databaseBuilder( context,
            Association_chat_db::class.java, "nyumba10"
        ).build()

      var insert_status=  db.association_chat_dao().insertAll(message_payload)

      //  return insert_status
//Log.d("long_value",long_value.toString())

          return insert_status
    }

    fun delete_sata_on_table(context: Context)
    {
        val db = Room.databaseBuilder( context,
            Association_chat_db::class.java, "nyumba10"
        ).build()

        db.association_chat_dao().delete()
    }

    fun retreive_chats_from_room_database(context: Context): List<Association_chat_entity>
    {

            val db = Room.databaseBuilder( context,
                Association_chat_db::class.java, "nyumba10").build()

            var messages= db.association_chat_dao().getAll()


return messages
    }
}