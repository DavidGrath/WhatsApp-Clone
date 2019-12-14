package com.example.whatsappclone.activities

import android.Manifest
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.whatsappclone.Constants.Companion.CONTACT_NAME
import com.example.whatsappclone.R
import com.example.whatsappclone.adapters.MainActivityPagerAdapter
import com.example.whatsappclone.fragments.CallsFragment
import com.example.whatsappclone.fragments.CameraFragment
import com.example.whatsappclone.fragments.ChatsFragment
import com.example.whatsappclone.fragments.StatusFragment
import com.example.whatsappclone.viewmodels.MainActivityViewModel
import com.google.android.material.tabs.TabItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.android.AndroidSmackInitializer
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

class MainActivity : AppCompatActivity() , View.OnClickListener{

    lateinit var viewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        fab.setOnClickListener(this)
        var permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE)
        if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) and
            (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(permissions, 1001)
        } else {

        }


        viewModel = ViewModelProviders.of(this)[MainActivityViewModel::class.java]

        val cameraFragment = CameraFragment()
        val chatsFragment = ChatsFragment()
        val statusFragment = StatusFragment()
        val callsFragment = CallsFragment()

        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(cameraFragment)
        fragmentList.add(chatsFragment)
        fragmentList.add(statusFragment)
        fragmentList.add(callsFragment)

        val mainActivityPagerAdapter = MainActivityPagerAdapter(supportFragmentManager)
        mainActivityPagerAdapter.fragmentList = fragmentList
        mainactivity_viewpager.adapter = mainActivityPagerAdapter
        mainactivity_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when(position) {

                    1 -> {
                        status_fab.hide()
                        fab.setImageResource(R.drawable.ic_chat_white_24dp)
                    }
                    2 -> {
                        status_fab.show()
                        fab.setImageResource(R.drawable.ic_photo_camera_white_24dp)
                    }
                    3 -> {
                        status_fab.hide()
                        fab.setImageResource(R.drawable.ic_call_white_24dp)
                    }
                    else -> {
                        status_fab.hide()
                    }
                }
            }
        })
        mainactivity_tablayout.setupWithViewPager(mainactivity_viewpager)
        mainactivity_viewpager.currentItem = 1
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.ic_photo_camera_white_24dp)
        imageView.alpha = 0.5.toFloat()
        mainactivity_tablayout.getTabAt(0)?.customView = imageView
    }

    override fun onClick(view : View?) {
        view?.let {
            when(it) {
                fab -> {
                    when(mainactivity_viewpager.currentItem) {
                        0 -> {

                        }
                        1 -> {
                            var builder = AlertDialog.Builder(this)
                            var v = this.layoutInflater.inflate(R.layout.dialog_start_chat, null)
                            builder.setView(v)
                            builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog : DialogInterface?, which: Int) {
                                    dialog?.cancel()
                                }
                            })
                            builder.setPositiveButton("Start Chat", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog : DialogInterface?, which: Int) {

                                }
                            })
                            var dialog = builder.create()
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.show()
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                                val editText = v.findViewById<EditText>(R.id.chat_input)
                                val name = (editText.text.toString())
                                val exists = runBlocking {
                                    viewModel.checkIfUserExists(name)
                                }
                                if(exists != null) {
                                    var anIntent =
                                        Intent(this@MainActivity, ChatActivity::class.java)
                                    anIntent.putExtra(CONTACT_NAME, exists.asUnescapedString())
                                    startActivity(anIntent)
                                    dialog.dismiss()
                                } else {
                                    editText.setError("User doesn't exist!")
                                }
                            }
                        }
                        2 -> {

                        }
                        3 -> {

                        }
                        else -> {

                        }
                    }
                }
                else -> {

                }
            }
        }
    }
}
