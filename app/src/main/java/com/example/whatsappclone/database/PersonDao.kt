package com.example.whatsappclone.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class PersonDao {
    @Query("SELECT * FROM Person")
    abstract fun getAllChats() : List<Person>

    @Insert
    abstract suspend fun addPerson(person: Person)

    @Insert
    abstract suspend fun addPeople(people : Array<Person>)

    @Query("UPDATE Person SET blocked = :blocked WHERE jid = :jid")
    abstract fun setPersonBlocked(blocked : Boolean, jid : String)
}