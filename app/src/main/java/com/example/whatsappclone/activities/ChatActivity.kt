package com.example.whatsappclone.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.Constants.Companion.CONTACT_NAME
import com.example.whatsappclone.R
import com.example.whatsappclone.adapters.ConversationRecyclerAdapter
import com.example.whatsappclone.viewmodels.MainViewModel
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.ios.IosEmojiProvider
import kotlinx.android.synthetic.main.activity_chat_screen.*
import org.jivesoftware.smack.android.AndroidSmackInitializer
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.PresenceEventListener
import org.jivesoftware.smackx.chatstates.ChatState
import org.jivesoftware.smackx.chatstates.ChatStateListener
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.FullJid
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import java.util.*

class ChatActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var viewModel : MainViewModel
    lateinit var preferences : SharedPreferences
    var animated = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        setSupportActionBar(toolbar2)
        send_button.setOnClickListener(this)

        EmojiManager.install(IosEmojiProvider())
        val popup = EmojiPopup.Builder.fromRootView(chat_activity_layout).build(main_text_input)
        toggle_emoji.setOnClickListener{
            if(popup.isShowing) {
                toggle_emoji.setImageDrawable(getDrawable(R.drawable.ic_tag_faces_gray_24dp))
            } else {
                toggle_emoji.setImageDrawable(getDrawable(R.drawable.ic_keyboard_black_24dp))
            }
            popup.toggle()
        }

        var contactName = intent.getStringExtra(CONTACT_NAME)
        supportActionBar?.title = contactName


        AndroidSmackInitializer.initialize(this)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        viewModel.setChat(JidCreate.from(contactName))
        viewModel.chatList.observe(this, Observer { chatList ->
            chatList.let {
                main_chat.adapter = ConversationRecyclerAdapter( it, this)
            }
        })
        supportActionBar?.subtitle = getStatusMethod() ?: getRelativeTimeSpan()

        preferences = getPreferences(Context.MODE_PRIVATE)
//        setBackground()

        main_chat.layoutManager = LinearLayoutManager(this)
        main_chat.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        main_chat.addItemDecoration(ConversationItemDecoration())
        viewModel.chatManager.addIncomingListener(IncomingChatMessageListener() { from, message, chat ->
            viewModel.updateChat(message)
            runOnUiThread {
                main_chat.adapter?.notifyDataSetChanged()
            }
        })
        viewModel.chatManager.addOutgoingListener(OutgoingChatMessageListener() {from, message, chat ->
            viewModel.updateChat(message)
            runOnUiThread {
                main_chat.adapter?.notifyDataSetChanged()
            }
        })
        viewModel.chatStateManager.addChatStateListener(object : ChatStateListener{
            override fun stateChanged(chat: Chat?, state: ChatState?, message: Message?) {
                runOnUiThread {
                        supportActionBar?.subtitle = if(viewModel.roster.getPresence(chat?.xmppAddressOfChatPartner).type == Presence.Type.available) {
                            when (state) {
                            ChatState.active -> {
                                "online"
                            }
                            ChatState.composing -> {
                                "typing..."
                            }
                            ChatState.gone -> {
                                null
                            }
                            ChatState.inactive -> {
                                "online"
                            }
                            ChatState.paused -> {
                                "online"
                            }
                            else -> {
                                getStatusMethod()
                            }
                        }
                        } else {
                            null
                    }
                    supportActionBar?.subtitle = supportActionBar?.subtitle ?: getRelativeTimeSpan()
                }
            }
        })
        main_text_input.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.chatStateManager.setCurrentState(ChatState.composing, viewModel.chat)
            }
            override fun afterTextChanged(editable : Editable?) {

                val interpolator = LinearInterpolator()

                var duration = 120L
                var shrink_image_animatorX = ObjectAnimator.ofFloat(send_image, "scaleX", 1f, 0.8f)
                var shrink_image_animatorY = ObjectAnimator.ofFloat(send_image, "scaleY", 1f, 0.8f)
                var expand_image_animatorX = ObjectAnimator.ofFloat(send_image, "scaleX", 0.8f, 1f)
                var expand_image_animatorY = ObjectAnimator.ofFloat(send_image, "scaleY", 0.8f, 1f)

                shrink_image_animatorX.interpolator = interpolator
                shrink_image_animatorY.interpolator = interpolator
                expand_image_animatorX.interpolator = interpolator
                expand_image_animatorY.interpolator = interpolator

                shrink_image_animatorX.duration = duration/2
                shrink_image_animatorY.duration = duration/2
                expand_image_animatorX.duration = duration/2
                expand_image_animatorY.duration = duration/2

                var text_input_empty = AnimatorSet()


                if (editable!!.isEmpty()) {
                    var show_camera_animator = ObjectAnimator.ofFloat(take_picture, "translationX" , take_picture.x, 0f)
                    var move_attach_animator_backward = ObjectAnimator.ofFloat(attach_file, "translationX" , attach_file.x, 0f)

                    show_camera_animator.interpolator = interpolator
                    move_attach_animator_backward.interpolator = interpolator

                    show_camera_animator.duration = duration
                    move_attach_animator_backward.duration = duration

                    text_input_empty.play(shrink_image_animatorX).with(show_camera_animator).with(move_attach_animator_backward).with(shrink_image_animatorY)//.with(move_attach_forward_animator)
                        .before(expand_image_animatorX).with(expand_image_animatorY)

                    shrink_image_animatorY.doOnEnd {
                        send_image.setImageDrawable(getDrawable(R.drawable.ic_mic_white_24dp))
                    }
                    text_input_empty.start()
                    text_input_empty.doOnEnd {
                        take_picture.isEnabled = true
                    }
                    animated = !animated
                }
                else if(!animated){

                    val CAMERA_MOVEMENT_DISTANCE = send_button.x - take_picture.x
                    val ATTACH_MOVEMENT_DISTANCE = take_picture.x - attach_file.x
                    var hide_camera_animator = ObjectAnimator.ofFloat(take_picture, "translationX", 0f, CAMERA_MOVEMENT_DISTANCE)
                    var move_attach_animator_forward = ObjectAnimator.ofFloat(attach_file, "translationX", 0f, ATTACH_MOVEMENT_DISTANCE)

                    hide_camera_animator.interpolator = interpolator
                    move_attach_animator_forward.interpolator = interpolator

                    hide_camera_animator.duration = duration
                    move_attach_animator_forward.duration = duration

                    text_input_empty.play(shrink_image_animatorX).with(hide_camera_animator).with(move_attach_animator_forward).with(shrink_image_animatorY)//.with(move_attach_forward_animator)
                        .before(expand_image_animatorX).with(expand_image_animatorY)
                    shrink_image_animatorY.doOnEnd {
                        send_image.setImageDrawable(getDrawable(R.drawable.ic_send_white_24dp))
                    }
                    text_input_empty.start()
                    text_input_empty.doOnEnd {
                        take_picture.isEnabled = false
                    }
                    animated = !animated
                }
                viewModel.chatStateManager.setCurrentState(ChatState.active, viewModel.chat)
            }
        })
        viewModel.roster.addPresenceEventListener(object : PresenceEventListener {
            override fun presenceAvailable(address: FullJid?, availablePresence: Presence?) {
                runOnUiThread {
                    toolbar2.subtitle = "online"
                }
                Log.d("Presence Available", "${address?.asUnescapedString()} ${availablePresence?.mode.toString()}")
            }

            override fun presenceUnavailable(address: FullJid?, presence: Presence?) {
                runOnUiThread {
                    toolbar2?.subtitle = getRelativeTimeSpan()
                }
                Log.d("Presence Unavailable", "${address?.asUnescapedString()} ${presence?.mode.toString()}")
            }

            override fun presenceSubscribed(address: BareJid?, subscribedPresence: Presence?) {

                Log.d("Presence Subscribed", "${address?.asUnescapedString()} ${subscribedPresence?.mode.toString()}")
            }

            override fun presenceUnsubscribed(address: BareJid?, unsubscribedPresence: Presence?) {
                runOnUiThread {
                    toolbar2.subtitle = null
                }
                Log.d("Presence Unsubbed", "${address?.asUnescapedString()} ${unsubscribedPresence?.mode.toString()}")
            }

            override fun presenceError(address: Jid?, errorPresence: Presence?) {
                Log.d("Presence Error", "${address?.asUnescapedString()} ${errorPresence?.mode.toString()}")
            }
        })
    }

    fun getStatusMethod() : String? {
        var chat = viewModel.chat
        var presence = viewModel.roster.getPresence(chat?.xmppAddressOfChatPartner)
        return if(presence.type == Presence.Type.available) {
            "online"
        } else if(presence.type == Presence.Type.unsubscribed) {
            null
        } else {
            getRelativeTimeSpan()
        }
    }

    fun getRelativeTimeSpan() : String {
        var timeElapsed = viewModel.lastActivityManager.getLastActivity(JidCreate.from(intent.getStringExtra(CONTACT_NAME))).idleTime
        var calendar = Calendar.getInstance()
        val relativeTime = calendar.time.time - (timeElapsed * 1000)
        return "last seen " + DateUtils.getRelativeTimeSpanString(this, relativeTime, true).toString()
    }

    fun setBackground() {
        runOnUiThread {
            chat_activity_layout.setBackgroundColor(R.color.chat_background)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.view_contact_menu_item -> {
                Toast.makeText(this, "Some Rando Menu Thing...", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.chat_block -> {
                viewModel.blockingCommandManager.blockContacts(mutableListOf(viewModel.jid))
                return true
            }
            else -> {
                return false
            }
        }
    }

    override fun onClick(view : View?) {
        view?.let {
            when(it) {
                send_button -> {
                    var chatText = main_text_input.text.toString()
                    viewModel.chat.send(chatText)
                    main_text_input.text.clear()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val unavailablePresence = Presence(Presence.Type.unavailable)
        viewModel.connection.sendStanza(unavailablePresence)
//        viewModel.connection.disconnect()
    }

}
