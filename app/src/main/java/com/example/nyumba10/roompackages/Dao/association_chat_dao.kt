package com.example.nyumba10.roompackages.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nyumba10.roompackages.entities.association_chat_entity


@Dao
interface association_chat_dao {
    @Query("SELECT * FROM association_chat_table")
    fun getAll(): List<association_chat_entity>


    @Insert
    fun insertAll(vararg message_payload: association_chat_entity)

    @Query("Delete FROM association_chat_table")
    fun  delete()


}