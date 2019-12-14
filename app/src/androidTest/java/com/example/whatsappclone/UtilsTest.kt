package com.example.whatsappclone

import android.content.Context
import android.text.format.DateUtils
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    @Mock
    lateinit var context: Context

    @Test
    fun contextTest() {
        var elapsed = DateUtils.getRelativeTimeSpanString(context, 157431093737) as String
        println(elapsed)


    }
}