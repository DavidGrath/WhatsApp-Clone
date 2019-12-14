package com.example.whatsappclone.adapters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.Constants.Companion.ME_SENDER
import com.example.whatsappclone.Constants.Companion.RECIEVED_VIEW_TYPE
import com.example.whatsappclone.Constants.Companion.SENT_VIEW_TYPE
import com.example.whatsappclone.Constants.Companion.XMPP_DOMAIN
import com.example.whatsappclone.R
import com.example.whatsappclone.database.ChatObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ConversationRecyclerAdapter(var chatList: List<ChatObject>, var context: Context) : RecyclerView.Adapter<ConversationRecyclerAdapter.ConversationViewHolder>(){




    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        var chatObject = chatList.get(position)
        holder.content.text = chatObject.content
        var formatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        holder.time.text = formatter.format(chatObject.time)
        if(chatObject.sender.equals("$ME_SENDER@$XMPP_DOMAIN")) {
//            if(!chatObject.sent) {
//                holder.tick.setImageDrawable(context.getDrawable(R.drawable.ic_access_time_black_24dp))
//            } else if(!chatObject.delivered) {
//                holder.tick.setImageDrawable(context.getDrawable(R.drawable.ic_done_black_24dp))
//                holder.tick.drawable.setTint(Color.parseColor("#7F7F7F"))
//            } else if(!chatObject.read) {
//                holder.tick.setImageDrawable(context.getDrawable(R.drawable.ic_done_all_black_24dp))
//            } else {
//                holder.tick.drawable.setTint(R.color.checkmark_blue)
//            }
        }
//        var params = LinearLayout.LayoutParams(holder.item.layoutParams)
        when(holder.itemViewType) {
            SENT_VIEW_TYPE-> {
//                holder.item.foregroundGravity = Gravity.END
//                params.gravity = Gravity.END
            }
            RECIEVED_VIEW_TYPE -> {
//                holder.item.foregroundGravity = Gravity.START
//                params.gravity = Gravity.START
            }
        }
//        holder.item.layoutParams = params

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        var view = LayoutInflater.from(context).inflate(if (viewType == SENT_VIEW_TYPE) R.layout.chat_bubble_personal
            else R.layout.chat_bubble_others, parent, false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        var chatObject = chatList.get(position)
        //TODO Fix this so it properly finds the recipient and/or sender
        return if (chatObject.recipient.contains(ME_SENDER, true)) RECIEVED_VIEW_TYPE else SENT_VIEW_TYPE
    }

    fun setChats(chatList: List<ChatObject>) {
        this.chatList = chatList
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(val item : View) : RecyclerView.ViewHolder(item) {

        var content : TextView
//        var tick : ImageView
        var time : TextView

        init {
            content = if(item.findViewById<TextView>(R.id.chat_message_personal) == null) item.findViewById(R.id.chat_message) else item.findViewById<TextView>(R.id.chat_message_personal)
//            tick = item.findViewById<ImageView>(R.id.sent_tick)
            time = if(item.findViewById<TextView>(R.id.chat_incoming_time) == null) item.findViewById(R.id.chat_others_time) else item.findViewById<TextView>(R.id.chat_incoming_time)
        }
    }
}