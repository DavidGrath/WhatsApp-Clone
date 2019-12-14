package com.example.whatsappclone.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jxmpp.jid.Jid

@Entity
data class Person(@PrimaryKey(autoGenerate = true) var id : Int,
                  var name : String, var number : String,var blocked : Boolean, var jid: String) {
}