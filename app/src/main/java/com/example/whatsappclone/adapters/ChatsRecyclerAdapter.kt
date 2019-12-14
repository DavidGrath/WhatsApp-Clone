package com.example.whatsappclone.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.whatsappclone.Constants.Companion.CONTACT_NAME
import com.example.whatsappclone.R
import com.example.whatsappclone.activities.ChatActivity
import com.example.whatsappclone.database.ChatObject
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import java.text.DateFormat
import java.util.*


//TODO Most likely to refactor all three into one RecyclerView Adapter
class ChatsRecyclerAdapter(var chatObjects: List<ChatObject>,var vCardManager: VCardManager) : RecyclerView.Adapter<ChatsRecyclerAdapter.ChatsViewHolder>(){

    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_chats_layout, parent, false)
        context = parent.context
        return ChatsViewHolder(view)
    }



    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        var chatObject = chatObjects.get(position)
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        if(chatObject.time < calendar.timeInMillis - 86400) {
            holder.time.text = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
                .format(chatObject.time)
        } else if(chatObject.time < calendar.timeInMillis) {
            holder.time.text = "Yesterday"
        }else {
            holder.time.text = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                .format(chatObject.time)
        }
        holder.recentChat.text = chatObject.content
        var partnerJid = JidCreate.bareFrom(chatObject.chatPartner)
        holder.personName.text = partnerJid.localpartOrNull.asUnescapedString()

        var unreadCount = getUnreadCount(position)
        if(unreadCount > 0) {
            holder.unreadCount.visibility = View.VISIBLE
            holder.unreadCount.text = unreadCount.toString()
        } else {
            holder.unreadCount.visibility = View.GONE
        }
        holder.item.setOnClickListener { v ->
            var intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CONTACT_NAME, JidCreate.entityBareFrom(chatObject.chatPartner).asUnescapedString())
            context.startActivity(intent)
        }

        val sharedOptions = RequestOptions().placeholder(R.drawable.ic_person_outline_black_24dp).circleCrop()
        Glide.with(context).load(vCardManager.loadVCard(JidCreate.entityBareFrom(chatObject.chatPartner)).avatar)
            .apply(sharedOptions)
            .into(holder.profilePic)
    }

    fun getUnreadCount(position: Int) : Int{
        return 0
    }

    override fun getItemCount(): Int {
        return chatObjects.size
    }

    inner class ChatsViewHolder(var item : View) : RecyclerView.ViewHolder(item) {
        var time = item.findViewById<TextView>(R.id.chat_recent_time)
        var recentChat = item.findViewById<TextView>(R.id.chat_most_recent)
        var personName = item.findViewById<TextView>(R.id.chat_name)
        var unreadCount = item.findViewById<TextView>(R.id.unread_count)
        var profilePic = item.findViewById<ImageView>(R.id.chat_dp)
    }
}