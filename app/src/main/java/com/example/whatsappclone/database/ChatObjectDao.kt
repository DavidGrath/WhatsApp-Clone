package com.example.whatsappclone.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class ChatObjectDao {
    /*
    * It's assumed that the wildcards are passed in the method call
    * */
    @Query("SELECT * FROM ChatObject WHERE chatPartner LIKE :contact ORDER BY time")
    abstract fun getAllContactChats(contact : String) : LiveData<List<ChatObject>>

    @Query("SELECT * FROM ChatObject")
    abstract fun getAllChats() : LiveData<List<ChatObject>>

    @Query("SELECT * FROM ChatObject GROUP BY chatPartner ORDER BY time DESC")
    abstract fun getRecentChats() : LiveData<List<ChatObject>>

    @Query("SELECT * FROM ChatObject WHERE starred = 1")
    abstract fun getStarredMessages() : LiveData<List<ChatObject>>

    @Insert
    abstract suspend fun insertChatObject(chatObject: ChatObject)

    @Insert
    abstract suspend fun insertChatObjects(chatObjects: Array<ChatObject>)

    @Query("UPDATE ChatObject SET sent = :sentBool WHERE stanzaId = :messageId")
    abstract suspend fun setMessageSent(sentBool: Boolean, messageId : String?)

    @Query("UPDATE ChatObject SET delivered = :deliveredBool WHERE stanzaId = :messageId")
    abstract suspend fun setMessageDelivered(deliveredBool: Boolean, messageId : String?)

    @Query("UPDATE ChatObject SET read = :readBool WHERE stanzaId = :messageId")
    abstract suspend fun setMessageRead(readBool: Boolean, messageId : String?)
}