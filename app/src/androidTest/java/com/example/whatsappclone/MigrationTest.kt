package com.example.whatsappclone

import android.database.Cursor
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.whatsappclone.Constants.Companion.ME_SENDER
import com.example.whatsappclone.database.ChatDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*


@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "chat_db_test"

    @get:Rule
    val helper : MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ChatDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `chatPartner` TEXT NOT NULL DEFAULT \"\"")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `sent` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `delivered` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `read` INTEGER NOT NULL DEFAULT 0")

                var cursor : Cursor = database.query("SELECT id, sender,chatPartner FROM ChatObject")

                while(cursor.moveToNext()) {
                    var id : Int = cursor.getInt(cursor.getColumnIndex("id"))
                    var sender : String = cursor.getString(cursor.getColumnIndex("sender"))
                    if(sender.contains(ME_SENDER, ignoreCase = true)) {
                        database.execSQL("UPDATE ChatObject SET chatPartner = recipient WHERE id = $id")
                    } else {
                        database.execSQL("UPDATE ChatObject SET chatPartner = sender WHERE id = $id")
                    }
                    var partner = cursor.getString(cursor.getColumnIndex("chatPartner"))
                    println("Id: $id\nSender:  $sender")
                }

                cursor.close()
                var cursor2 = database.query("SELECT `chatPartner` FROM ChatObject")
                while (cursor2.moveToNext()) {
                    var partner = cursor2.getString(cursor2.getColumnIndex("chatPartner"))
                    System.out.println("Partner(2): " + partner)
                }
            }

        }



        var db = helper.createDatabase(TEST_DB, 1).apply {
            var time = Date().time
            execSQL("INSERT INTO ChatObject (time, content, sender, recipient) VALUES ($time, 'Heya!', 'Admin', 'David')")
            time = Date().time
            execSQL("INSERT INTO ChatObject (time, content, sender, recipient) VALUES ($time, 'Oh, you exist', 'David', 'Admin')")
            time = Date().time
            execSQL("INSERT INTO ChatObject (time, content, sender, recipient) VALUES ($time, 'Am I a joke to you?', 'Admin', 'David')")
            close()
        }


        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)


    }
    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Person ADD COLUMN `blocked` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Person ADD COLUMN `jid` TEXT NOT NULL DEFAULT \"\"")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `stanzaId` TEXT")
                database.execSQL("ALTER TABLE ChatObject ADD COLUMN `starred` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE Person SET `jid` = `name` WHERE 1")
            }
        }
        var db = helper.createDatabase(TEST_DB, 2).apply {
            var time = Date().time
            execSQL("INSERT INTO ChatObject (time, content, sender, recipient, chatPartner, sent, delivered, read) VALUES ($time, 'Heya!', 'Admin', 'David', 'David', 0, 0, 0)")
            time = Date().time
            execSQL("INSERT INTO ChatObject (time, content, sender, recipient, chatPartner, sent, delivered, read) VALUES ($time, 'Oh, you exist', 'David', 'Admin', 'David', 0, 0, 0)")
            time = Date().time
            execSQL("INSERT INTO ChatObject (time, content, sender, recipient, chatPartner, sent, delivered, read) VALUES ($time, 'Am I a joke to you?', 'Admin', 'David', 'David', 0, 0, 0)")
            execSQL("INSERT INTO Person (name, number) VALUES ('David', '1234')")
            execSQL("INSERT INTO Person (name, number) VALUES ('Duke', '2345')")
            close()
        }


        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)
    }

}