package com.example.nyumba10.roompackages.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity(tableName = "person_to_person_chat_table")
data class Person_to_person_chat_entity (

    @PrimaryKey(autoGenerate = true)
    var chats_id:Int=0 ,

    @ColumnInfo(name= "chats_unique_id")
    var chats_unique_id: String="",

    @ColumnInfo(name= "message")
    var message: String="",
    //time channel was created column
    @ColumnInfo(name= "time_created")
    var time_created: String="",
    //current user phone number

       @ColumnInfo(name= "username")
    var username: String="",
    //
    @ColumnInfo(name= "from")
    var from_id_no: String=""



){

}