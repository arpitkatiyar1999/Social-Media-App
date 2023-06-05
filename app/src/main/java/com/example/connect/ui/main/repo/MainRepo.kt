package com.example.connect.ui.main.repo

import com.example.connect.bean.UserBean
import com.example.connect.utils.SealedResult

interface MainRepo {
    suspend fun getAllUsers(): SealedResult<ArrayList<UserBean>>

    suspend fun sendFriendRequest(
        userBean: UserBean,
        currentUserBean: UserBean
    ): SealedResult<Nothing?>

    suspend fun getUserFromId(userId: String): SealedResult<UserBean?>

    suspend fun getFriendList(userId: String): SealedResult<UserBean?>

//    suspend fun acceptRequest(currentUserBean: UserBean,userBean: UserBean): SealedResult<Nothing?>
}