package com.example.connect.ui.authentication.repo

import com.example.connect.bean.UserBean
import com.example.connect.utils.SealedResult
import com.google.firebase.auth.AuthResult

interface AuthRepo {
    suspend fun signInUser(email: String, password: String): SealedResult<AuthResult>
    suspend fun forgotPassword(email: String): SealedResult<Void?>
    suspend fun createAccount(email: String, password: String): SealedResult<AuthResult>
    suspend fun createUser(user: UserBean, uid: String): SealedResult<Void?>
    suspend fun getCurrentUser(uid: String): SealedResult<Boolean>
}