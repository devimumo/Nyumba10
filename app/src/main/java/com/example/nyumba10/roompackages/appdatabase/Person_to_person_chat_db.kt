package com.example.nyumba10.roompackages.appdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nyumba10.roompackages.Dao.person_to_person_chat_dao
import com.example.nyumba10.roompackages.entities.Person_to_person_chat_entity


@Database(version=1, entities = [Person_to_person_chat_entity::class] )
abstract class Person_to_person_chat_db :RoomDatabase(){
    abstract fun person_to_person_chat_dao (): person_to_person_chat_dao


}