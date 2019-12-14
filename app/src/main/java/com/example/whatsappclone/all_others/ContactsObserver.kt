package com.example.whatsappclone.all_others

import android.database.ContentObserver
import android.os.Handler

class ContactsObserver(handler : Handler) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
    }
}