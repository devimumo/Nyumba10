package com.example.nyumba10.roompackages.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nyumba10.roompackages.entities.Person_to_person_chat_entity


@Dao
interface person_to_person_chat_dao {
    @Query("SELECT * FROM person_to_person_chat_table WHERE chats_unique_id LIKE :unique_id" )
    fun getAll(unique_id: String): List<Person_to_person_chat_entity>


    @Insert
    fun insertAll( message_payload: Person_to_person_chat_entity):Long

    @Query("Delete FROM person_to_person_chat_table")
    fun  delete()


}