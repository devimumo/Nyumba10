package com.example.nyumba10_person_to_person.roompackages.db_instanse

import android.content.Context
import androidx.room.Room
import com.example.nyumba10.roompackages.appdatabase.Person_to_person_chat_db
import com.example.nyumba10.roompackages.entities.Person_to_person_chat_entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class person_to_person_chat_db_instanse {



      fun insert_message_payload(context: Context,message_payload: Person_to_person_chat_entity): Long
    {

            val db = Room.databaseBuilder(
                context,
                Person_to_person_chat_db::class.java,
                "nyumba10_person_to_person"
            ).build()



             var insert_status=   db.person_to_person_chat_dao().insertAll(message_payload)


//Log.d("long_value",long_value.toString())
return insert_status
    }

    fun delete_data_on_table(context: Context)
    {
        val db = Room.databaseBuilder( context,Person_to_person_chat_db::class.java, "nyumba10_person_to_person").build()

        db.person_to_person_chat_dao().delete()
    }

    fun retreive_person_to_person_chats_from_room_database(context: Context,unique_id: String): List<Person_to_person_chat_entity>
    {

            val db = Room.databaseBuilder( context, Person_to_person_chat_db::class.java, "nyumba10_person_to_person").build()

            var messages= db.person_to_person_chat_dao().getAll(unique_id)


return messages
    }
}