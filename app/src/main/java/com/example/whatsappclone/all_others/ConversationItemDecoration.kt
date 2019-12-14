package com.example.whatsappclone.all_others

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.Constants.Companion.RECIEVED_VIEW_TYPE
import com.example.whatsappclone.Constants.Companion.SENT_VIEW_TYPE

class ConversationItemDecoration() : RecyclerView.ItemDecoration() {
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
//        var left = 8
//        var right = parent.width - 8
//        var top = 4
//        var bottom = 4
        for (i in 0..parent.childCount) {
            var child = parent.getChildAt(i)
            var params = LinearLayout.LayoutParams(child.layoutParams)
            when(parent.adapter?.getItemViewType(i)) {
                SENT_VIEW_TYPE-> {
                    params.gravity = Gravity.END
                }
                RECIEVED_VIEW_TYPE -> {
                    params.gravity = Gravity.START
                }

            }

        }


    }
}