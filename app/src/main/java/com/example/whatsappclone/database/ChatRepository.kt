package com.example.whatsappclone.database

import androidx.lifecycle.LiveData

class ChatRepository(private val chatObjectDao: ChatObjectDao, private val personDao: PersonDao) {
    fun getAllContactChats(contact : String) : LiveData<List<ChatObject>> = chatObjectDao.getAllContactChats(contact)
    fun getAllChats() : LiveData<List<ChatObject>> = chatObjectDao.getAllChats()
    fun getRecentChats() : LiveData<List<ChatObject>> = chatObjectDao.getRecentChats()
    fun getStarredMessages() : LiveData<List<ChatObject>> = chatObjectDao.getStarredMessages()
    suspend fun insertChatObject(chatObject: ChatObject) = chatObjectDao.insertChatObject(chatObject)
    suspend fun insertChatObjects(chatObjects : Array<ChatObject>) = chatObjectDao.insertChatObjects(chatObjects)
    suspend fun setMessageSent(sentBool : Boolean, messageId : String?) = chatObjectDao.setMessageSent(sentBool, messageId)
    suspend fun setMessageDelivered(deliveredBool : Boolean, messageId : String?) = chatObjectDao.setMessageDelivered(deliveredBool, messageId)
    suspend fun setMessageRead(readBool : Boolean, messageId : String?) = chatObjectDao.setMessageRead(readBool, messageId)

    suspend fun addPerson(person: Person) = personDao.addPerson(person)
    suspend fun addPeople(people : Array<Person>) = personDao.addPeople(people)
    fun setPersonBlocked(blocked : Boolean, jid : String) = personDao.setPersonBlocked(blocked, jid)
}