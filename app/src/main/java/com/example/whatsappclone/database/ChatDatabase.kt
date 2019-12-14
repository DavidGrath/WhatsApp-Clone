package com.example.whatsappclone.database

import android.content.Context
import android.database.Cursor
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.whatsappclone.Constants
import com.example.whatsappclone.Constants.Companion.SIXTH_DEC_2019
import kotlinx.coroutines.CoroutineScope

@Database(entities = [ChatObject::class, Person::class], version = SIXTH_DEC_2019)
abstract class ChatDatabase : RoomDatabase(){
    abstract fun getPersonDao() : PersonDao
    abstract fun getChatObjectDao() : ChatObjectDao

    companion object {


        @Volatile
        private var INSTANCE : ChatDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `chatPartner` TEXT NOT NULL DEFAULT \"\"")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `sent` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `delivered` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `read` INTEGER NOT NULL DEFAULT 1")

                var cursor: Cursor = database.query("SELECT id, sender,chatPartner FROM ChatObject")

                while (cursor.moveToNext()) {
                    var id: Int = cursor.getInt(cursor.getColumnIndex("id"))
                    var sender: String = cursor.getString(cursor.getColumnIndex("sender"))
                    if (sender.contains(Constants.ME_SENDER, ignoreCase = true)) {
                        database.execSQL("UPDATE ChatObject SET chatPartner = recipient WHERE id = $id")
                    } else {
                        database.execSQL("UPDATE ChatObject SET chatPartner = sender WHERE id = $id")
                    }
                }
                cursor.close()

            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Person ADD COLUMN `blocked` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Person ADD COLUMN `jid` TEXT NOT NULL DEFAULT \"\"")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `stanzaId` TEXT")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `starred` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE Person SET `jid` = `name` WHERE 1")
            }
        }

        fun getDatabase(context : Context) : ChatDatabase {
            val temp = INSTANCE
            if(temp != null) {
                return temp

            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}