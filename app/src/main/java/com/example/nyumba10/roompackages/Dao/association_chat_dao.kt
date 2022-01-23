package com.example.nyumba10.roompackages.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nyumba10.roompackages.entities.Association_chat_entity


@Dao
interface association_chat_dao {
    @Query("SELECT * FROM association_chat_table")
    fun getAll(): List<Association_chat_entity>


    @Insert
    fun insertAll( message_payload: Association_chat_entity): Long

    @Query("Delete FROM association_chat_table")
    fun  delete()


}