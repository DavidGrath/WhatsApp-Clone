package com.example.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R

//TODO Most likely to refactor all three into one RecyclerView Adapter
class StatusRecyclerAdapter() : RecyclerView.Adapter<StatusRecyclerAdapter.StatusViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_status_layout_common, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class StatusViewHolder(var item : View) : RecyclerView.ViewHolder(item) {

    }
}