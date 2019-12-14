package com.example.whatsappclone.all_others

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.whatsappclone.Constants
import com.example.whatsappclone.Constants.Companion.CONTACT_NAME
import com.example.whatsappclone.Constants.Companion.INTENT_SERVICE_MAIN
import com.example.whatsappclone.Constants.Companion.NOTIFICATION_REQUEST_CODE
import com.example.whatsappclone.R
import com.example.whatsappclone.activities.ChatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import java.net.InetAddress

class MainBackgroundService() : Service() {

    val CHANNEL_ID = "background_service"
    lateinit var connection : XMPPTCPConnection

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(INTENT_SERVICE_MAIN, "Service Started!")
        GlobalScope.launch {
            val handler = Handler(Looper.getMainLooper())
            var config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(Constants.ME_SENDER, Constants.PASSWORD)
                .setXmppDomain(Constants.XMPP_DOMAIN)
                .setHostAddress(InetAddress.getByName(Constants.XMPP_DOMAIN))
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setPort(5222)
                .build()
            connection = XMPPTCPConnection(config)
            connection.connect()
            connection.login()
            val chatManager = ChatManager.getInstanceFor(connection)
            handler.post {
                chatManager.addIncomingListener(IncomingChatMessageListener() { from, message, chat ->
//                    val NOTIF_REPLY = "notification_reply"
//                    val remoteInput = RemoteInput.Builder(NOTIF_REPLY)
//                        .setLabel("Type a reply")
//                        .build()
//                    val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_send_white_24dp, "Reply", null)
//                        .addRemoteInput(remoteInput)
//                        .build()
//                    val markaAsReadAction = NotificationCompat.Action.Builder()
                    val anIntent = Intent(applicationContext, ChatActivity::class.java)
                    anIntent.putExtra(CONTACT_NAME, from.localpartOrNull.asUnescapedString() + "@" + from.domain.toString())
                    val pendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_REQUEST_CODE, anIntent, PendingIntent.FLAG_CANCEL_CURRENT)
                    val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSound(RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_NOTIFICATION))
                        .setContentTitle(message.from.localpartOrNull.asUnescapedString())
                        .setContentText(message.body)
                        .setSmallIcon(R.drawable.ic_search_black_24dp)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_mic_white_24dp))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    val notifManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notifManager.notify("A Tag", 1, builder.build())
                })
            }
        }
        return START_STICKY
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        connection.sendStanza(Presence(Presence.Type.unavailable, null, 1, Presence.Mode.away))
        connection.disconnect()
        Log.d(INTENT_SERVICE_MAIN, "I've been destroyed!!! :(")
    }

}