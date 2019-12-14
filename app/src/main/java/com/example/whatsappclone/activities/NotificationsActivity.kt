package com.example.whatsappclone.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import com.example.whatsappclone.R
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : AppCompatActivity() , View.OnClickListener{

    val NOTIFICATION_ID = 20
    val CHANNEL_ID = "new_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        send_notif.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it) {
                send_notif -> {
                    var defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    var builder = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentText("Lorem Ipsum")
                        .setContentTitle("Dave from the Nether")
                        .setSound(defaultUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.ic_photo_camera_white_24dp)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_send_white_24dp))
                    val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                }
                else -> {

                }
            }
        }
    }
}
