package com.example.whatsappclone.viewmodels

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.Constants
import com.example.whatsappclone.Constants.Companion.LOCALHOST
import com.example.whatsappclone.Constants.Companion.ME_SENDER
import com.example.whatsappclone.Constants.Companion.XMPP_DOMAIN
import com.example.whatsappclone.database.ChatDatabase
import com.example.whatsappclone.database.ChatObject
import com.example.whatsappclone.database.ChatRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.blocking.BlockingCommandManager
import org.jivesoftware.smackx.blocking.JidsBlockedListener
import org.jivesoftware.smackx.chatstates.ChatState
import org.jivesoftware.smackx.chatstates.ChatStateListener
import org.jivesoftware.smackx.chatstates.ChatStateManager
import org.jivesoftware.smackx.iqlast.LastActivityManager
import org.jivesoftware.smackx.offline.OfflineMessageManager
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener
import org.jxmpp.jid.Jid
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application){

    lateinit var jid : Jid
    lateinit var connection : AbstractXMPPConnection
    lateinit var chatManager : ChatManager
    lateinit var chat : Chat
    lateinit var roster: Roster
    lateinit var chatList : LiveData<List<ChatObject>>
    lateinit var chatStateManager : ChatStateManager
    lateinit var lastActivityManager : LastActivityManager
    lateinit var receiptManager : DeliveryReceiptManager
    lateinit var offlineMessageManager: OfflineMessageManager
    lateinit var blockingCommandManager : BlockingCommandManager
    var chatRepository : ChatRepository
    var chatDatabase = ChatDatabase.getDatabase(application)
    var chatObjectDao = chatDatabase.getChatObjectDao()
    var personDao = chatDatabase.getPersonDao()


    init {
        chatRepository = ChatRepository(chatObjectDao, personDao)
    }
    fun setChat(jid: Jid) {
        this.jid = jid
        var config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(ME_SENDER, Constants.PASSWORD)
            .setXmppDomain(XMPP_DOMAIN)
            .setHostAddress( runBlocking {
                GlobalScope.async {
                    InetAddress.getByName(XMPP_DOMAIN)
                }.await()
            })
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setPort(5222)
            .build()

        connection = XMPPTCPConnection(config)
        connection.connect()
        connection.login()
        chatManager = ChatManager.getInstanceFor(connection)
                                                // SQL Wildcard
        chatList = getAllContactChats(jid.localpartOrNull.asUnescapedString() + "%")
        chatStateManager = ChatStateManager.getInstance(connection)
        lastActivityManager = LastActivityManager.getInstanceFor(connection)
        offlineMessageManager = OfflineMessageManager(connection)
        var messages = offlineMessageManager.getMessages()
        if(messages.size > 0) {


//            var chatArray = Array(messages.size) { i ->
//                ChatObject(null, Date().time, messages[i].body, messages[i].from.asBareJid().asUnescapedString(),
//                    messages[i].to.asBareJid().asUnescapedString(), messages[i].from.asBareJid().asUnescapedString(),
//                    false, false, false)
//            }
//            insertChatObjects(chatArray)
//            offlineMessageManager.deleteMessages()
        }
        roster = Roster.getInstanceFor(connection)
//        roster.createEntry(jid.asBareJid(), chat.xmppAddressOfChatPartner.localpart.asUnescapedString(), null)
        chat = chatManager.chatWith(jid.asEntityBareJidIfPossible())
        receiptManager = DeliveryReceiptManager.getInstanceFor(connection)

        receiptManager.autoAddDeliveryReceiptRequests()
        receiptManager.addReceiptReceivedListener(object : ReceiptReceivedListener {
            override fun onReceiptReceived(
                fromJid: Jid?,
                toJid: Jid?,
                receiptId: String?,
                receipt: Stanza?
            ) {
                setMessageDelivered(true, receiptId)
            }
        })
        blockingCommandManager = BlockingCommandManager.getInstanceFor(connection)
        blockingCommandManager.addJidsBlockedListener(object : JidsBlockedListener{
            override fun onJidsBlocked(blockedJids: MutableList<Jid>?) {
                setPersonBlocked(true, blockedJids!![0].asUnescapedString())
            }
        })
        blockingCommandManager.addJidsUnblockedListener {
            setPersonBlocked(false, it[0].asUnescapedString())
        }
    }

    suspend fun getInetAddress() = GlobalScope.async { InetAddress.getByName(XMPP_DOMAIN) }.await()

    fun setPersonBlocked(blocked: Boolean, jid : String) = chatRepository.setPersonBlocked(blocked, jid)
    fun updateChat(message: Message) {
        val ME_DOMAIN = "$ME_SENDER@$XMPP_DOMAIN"
        insertChatObject(
            ChatObject(null, Date().time, message.body.toString(), chat.xmppAddressOfChatPartner.asUnescapedString(),
                ME_DOMAIN, chat.xmppAddressOfChatPartner.asUnescapedString(), false, false, false, message.stanzaId, false)
        )
    }

    fun insertChatObject(chatObject: ChatObject) = viewModelScope.launch {
        chatRepository.insertChatObject(chatObject)
    }

    fun insertChatObjects(chatObjects : Array<ChatObject>) = viewModelScope.launch {
        chatRepository.insertChatObjects(chatObjects)
    }

    fun getAllContactChats(contactName : String) = chatRepository.getAllContactChats(contactName)
    fun getAllChats() = chatRepository.getAllChats()
    fun setMessageDelivered(deliveredBool : Boolean, messageId : String?) =
        viewModelScope.launch {

            chatRepository.setMessageDelivered(deliveredBool, messageId)
        }
}