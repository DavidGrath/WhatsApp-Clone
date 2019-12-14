package com.example.whatsappclone.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.activities.NotificationsActivity
import com.example.whatsappclone.activities.*
import com.example.whatsappclone.adapters.ChatsRecyclerAdapter
import com.example.whatsappclone.all_others.MainBackgroundService
import com.example.whatsappclone.viewmodels.MainActivityViewModel

    lateinit var viewModel : MainActivityViewModel

class ChatsFragment : Fragment() {

    var started = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        var chats_recycler_View : RecyclerView = view.findViewById(R.id.chats_recycler_view)

        viewModel = ViewModelProviders.of(this, ViewModelProvider.
            AndroidViewModelFactory((context as Activity).application)).get(MainActivityViewModel::class.java)
        viewModel.getRecentChats().observe(this, Observer { chatList ->
            chatList.let {
//                for (i in it){
//
//                }

                chats_recycler_View.adapter = ChatsRecyclerAdapter(chatList, viewModel.vCardManager)
            }
        })
        chats_recycler_View.layoutManager = LinearLayoutManager(context)
        chats_recycler_View.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))


        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.chats_menu, menu)
        var serviceItem = menu.findItem(R.id.start_service_menu_item)
        serviceItem.title = if(started) "Stop Service" else "Start Service"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.chatsmenu_settings -> {
                val newIntent = Intent(context, SettingsActivity::class.java)
                startActivity(newIntent)
                return true
            }
            R.id.notifs_activity_menu_item -> {
                val newIntent = Intent(context, NotificationsActivity::class.java)
                startActivity(newIntent)
                return true
            }
            R.id.new_chat_menu_item -> {
                val newIntent = Intent(context, NewChatActivity::class.java)
                startActivity(newIntent)
                return true
            }
            R.id.start_service_menu_item -> {
                val newIntent = Intent(context, MainBackgroundService::class.java)
                if(!started) {
                    context?.startService(newIntent)
                } else {
                    context?.stopService(newIntent)
                }
                started = !started
                item.title = if(started) "Stop Service" else "Start Service"
                return true
            }
        }
        return false
    }
}