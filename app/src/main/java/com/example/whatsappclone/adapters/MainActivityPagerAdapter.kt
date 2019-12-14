package com.example.whatsappclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainActivityPagerAdapter(var fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager){

    lateinit var fragmentList : List<Fragment>


    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return null
            }
            1 -> {
                return "CHATS"
            }
            2 -> {
                return "STATUS"
            }
            3 -> {
                return "CALLS"
            }

        }
        return null
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}