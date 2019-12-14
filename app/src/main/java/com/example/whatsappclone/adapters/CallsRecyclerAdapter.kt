package com.example.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R

//TODO Most likely to refactor all three into one RecyclerView Adapter
class CallsRecyclerAdapter  : RecyclerView.Adapter<CallsRecyclerAdapter.CallsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_calls_layout, parent, false)
        return CallsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallsViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class CallsViewHolder(var item : View) : RecyclerView.ViewHolder(item) {

    }
}