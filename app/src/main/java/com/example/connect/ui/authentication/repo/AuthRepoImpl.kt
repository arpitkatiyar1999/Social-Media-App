package com.example.connect.ui.authentication.repo

import com.example.connect.bean.UserBean
import com.example.connect.utils.Constants.KEY_USER
import com.example.connect.utils.SealedResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepo {
    override suspend fun signInUser(email: String, password: String): SealedResult<AuthResult> {
        return try {
            val response = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            SealedResult.success(response)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }

    override suspend fun forgotPassword(email: String): SealedResult<Void?> {
        return try {
            val response = firebaseAuth.sendPasswordResetEmail(email).await()
            SealedResult.success(response)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }

    override suspend fun createAccount(email: String, password: String): SealedResult<AuthResult> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            SealedResult.success(result)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }

    override suspend fun createUser(user: UserBean, uid: String): SealedResult<Void?> {
        return try {
            val response = firestore.collection(KEY_USER).document(uid).set(user).await()
            SealedResult.success(response)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }

    override suspend fun getCurrentUser(uid: String): SealedResult<Boolean> {
        return try {
            val result = firestore.collection(KEY_USER).document(uid).get().await()
            if (!result.exists()) {
                SealedResult.success(false)
            } else SealedResult.success(true)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }
}
