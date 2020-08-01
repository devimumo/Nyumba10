package com.example.nyumba10.roompackages.appdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nyumba10.roompackages.Dao.association_chat_dao
import com.example.nyumba10.roompackages.entities.association_chat_entity


@Database(version=1, entities =  arrayOf(association_chat_entity::class) )
abstract class association_chat_db :RoomDatabase(){
    abstract fun association_chat_dao (): association_chat_dao


}