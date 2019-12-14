package com.example.whatsappclone.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatObject(@PrimaryKey(autoGenerate = true) var id : Int?, var time : Long, var content : String,
                      var sender : String, var recipient : String, var chatPartner : String,
                      var sent : Boolean, var delivered : Boolean, var read : Boolean, var stanzaId : String?, var starred : Boolean) {

}