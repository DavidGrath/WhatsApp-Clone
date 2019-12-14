package com.example.whatsappclone

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.android.AndroidSmackInitializer
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.jxmpp.jid.impl.JidCreate
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.net.InetAddress

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {



    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

    }

    @Mock
    lateinit var intent : Intent

    @get:Rule
    lateinit var context: Context

    @Test
    fun contextTest() {
        var elapsed = DateUtils.getRelativeTimeSpanString(context, 157431093737) as String
        println(elapsed)
        assertEquals("Oh yeah!", "Yesterday", elapsed)

    }

    @Test
    fun connectToOpenFire() {
        `when`(intent.getStringExtra("PHONE_NUM")).thenReturn("admin")

        AndroidSmackInitializer.initialize(context)
        var configuration = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword("david", "waterboy")
            .setXmppDomain("laptop-ofiu2brs")
            .setHostAddress(InetAddress.getByName("laptop-ofiu2brs"))
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()
        var connection = XMPPTCPConnection(configuration)
        connection.connect()
        connection.login()
        var chatManager = ChatManager.getInstanceFor(connection)
        var chat = chatManager.chatWith(JidCreate
            .entityBareFrom("${intent.getStringExtra("PHONE_NUM")}@laptop-ofiu2brs"))
        System.out.println(chat.xmppAddressOfChatPartner.localpart.asUnescapedString())
        assertTrue(connection.isConnected)
        chatManager.addIncomingListener() { from, message, chat ->
            System.out.println(from.asUnescapedString() + " : " + message.body)
        }
        connection.disconnect()
    }
}
