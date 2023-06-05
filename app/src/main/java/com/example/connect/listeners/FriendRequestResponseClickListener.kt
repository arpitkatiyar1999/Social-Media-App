package com.example.connect.listeners

import com.example.connect.bean.UserBean

interface FriendRequestResponseClickListener {
    fun onFriendRequestAccepted(userBean: UserBean, position: Int)
    fun onFriendRequestDeclined(userBean: UserBean, position: Int)
}