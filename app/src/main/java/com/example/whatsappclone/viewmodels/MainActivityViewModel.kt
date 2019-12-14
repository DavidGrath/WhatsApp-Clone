package com.example.whatsappclone.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.whatsappclone.Constants
import com.example.whatsappclone.Constants.Companion.SEARCH_SERVICE
import com.example.whatsappclone.database.ChatDatabase
import com.example.whatsappclone.database.ChatObject
import com.example.whatsappclone.database.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.search.UserSearchManager
import org.jxmpp.jid.impl.JidCreate
import java.net.InetAddress
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.Jid


// TODO Merge two viewmodels
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var chatDatabase = ChatDatabase.getDatabase(application)
    var chatObjectDao = chatDatabase.getChatObjectDao()
    var personDao = chatDatabase.getPersonDao()
    var chatRepository = ChatRepository(chatObjectDao, personDao)
    var config = XMPPTCPConnectionConfiguration.builder()
        .setUsernameAndPassword(Constants.ME_SENDER, Constants.PASSWORD)
        .setXmppDomain(Constants.XMPP_DOMAIN)
        .setHostAddress(runBlocking {
            withContext(Dispatchers.Default) {
                InetAddress.getByName(Constants.XMPP_DOMAIN)
            }
        })
        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
        .setPort(5222)
        .build()

    val connection = XMPPTCPConnection(config)
    val userSearchManager = UserSearchManager(connection)
    val vCardManager = VCardManager.getInstanceFor(connection)

    init {
        connection.connect()
        connection.login()
    }

    fun getRecentChats(): LiveData<List<ChatObject>> = chatRepository.getRecentChats()

    suspend fun checkIfUserExists(search : String) : Jid? {
        val searchForm = userSearchManager.getSearchForm(JidCreate.domainBareFrom(SEARCH_SERVICE))
        val answerForm = searchForm.createAnswerForm()
        answerForm.setAnswer("Username", true)
        answerForm.setAnswer("search", search)

        val data = userSearchManager.getSearchResults(answerForm, JidCreate.domainBareFrom(SEARCH_SERVICE))
        if((data.rows.size > 0) && (data.rows[0].getValues("jid").size > 0)) {
            return JidCreate.from(data.rows[0].getValues("jid")[0])
        } else {
            return null
        }
    }

}