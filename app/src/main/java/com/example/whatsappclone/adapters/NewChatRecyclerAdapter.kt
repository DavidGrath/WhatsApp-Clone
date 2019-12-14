package com.example.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.activities.NewChatActivity
import kotlinx.android.synthetic.main.activity_new_chat.view.*

class NewChatRecyclerAdapter(var tempPeople : ArrayList<NewChatActivity.PersonTwo>) : RecyclerView.Adapter<NewChatRecyclerAdapter.NewChatViewHolder>() {

    override fun onBindViewHolder(newChatViewHolder : NewChatViewHolder, position : Int) {
        var personTwo = tempPeople.get(position)
        newChatViewHolder.name.text = personTwo.name
        newChatViewHolder.about.text = personTwo.number
    }

    override fun onCreateViewHolder(viewGroup : ViewGroup, position : Int): NewChatViewHolder {
        var view = LayoutInflater.from(viewGroup.context).inflate(R.layout.new_chat_layout, null, false)
        return NewChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tempPeople.size
    }

    class NewChatViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.new_contact_name)
        var about = view.findViewById<TextView>(R.id.new_contact_about)
    }
}