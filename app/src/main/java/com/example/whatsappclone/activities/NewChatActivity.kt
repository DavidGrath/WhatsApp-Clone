package com.example.whatsappclone.activities

import android.content.ContentResolver
import android.database.Cursor
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.Constants.Companion.XMPP_DOMAIN
import com.example.whatsappclone.R
import com.example.whatsappclone.adapters.NewChatRecyclerAdapter
import kotlinx.android.synthetic.main.activity_new_chat.*

class NewChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        var cursor = GetCursorAsyncTask(contentResolver).execute().get()

        Log.d("This Works?", cursor.getColumnName(1))
        var jid = "david" + "@" + XMPP_DOMAIN
//        var connection = XMPPTCPConnection(jid, Constants.PASSWORD)
        var numbers : ArrayList<PersonTwo>
        if(cursor.count >0) {
            numbers = ArrayList<PersonTwo>(0)

            while (cursor.moveToNext()) {
                val hasNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)) .toInt() != 0
                val hasNormalized = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)) != null
                if(hasNumber and hasNormalized)
                numbers.add(PersonTwo(
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))))
            }
            Toast.makeText(this, cursor.count.toString(), Toast.LENGTH_SHORT).show()

//            for (number in numbers) {
//                var contact_jid = number + "@" + XMPP_DOMAIN
//                var chatManager = ChatManager.getInstanceFor(connection)
//                var chat = chatManager.chatWith(JidCreate.entityBareFrom(contact_jid))
//                chatManager.addIncomingListener(IncomingChatMessageListener { from, message, chat ->
//
//                })
//            }
        } else {
            numbers = arrayListOf()
        }
        new_chat_recyclerview.adapter = NewChatRecyclerAdapter(ArrayList(numbers.sortedWith(compareBy{ it.name }).distinctBy { it.number }))
        new_chat_recyclerview.layoutManager = LinearLayoutManager(this)
//        var contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//        Log.d("Contact no. 1", contactName)
//        Toast.makeText(applicationContext, contactName, Toast.LENGTH_SHORT).show()
        cursor.close()
    }

    class GetCursorAsyncTask(var contentResolver: ContentResolver) : AsyncTask<Void, Void, Cursor>() {

        var cursor : Cursor? = null

        override fun doInBackground(vararg p0: Void?): Cursor? {
            return contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,//ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                null, null)
        }

        override fun onPostExecute(result: Cursor?) {
            cursor = result
        }
    }
    data class PersonTwo(var name : String,var number : String){}
}
