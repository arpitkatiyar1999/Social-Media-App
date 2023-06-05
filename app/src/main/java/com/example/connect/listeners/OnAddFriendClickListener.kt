package com.example.connect.listeners

import com.example.connect.bean.UserBean

interface OnAddFriendClickListener {
    fun onAddFriendClicked(userBean: UserBean,position:Int)
}