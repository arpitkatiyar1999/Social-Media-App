package com.example.connect.ui.main.repo

import com.example.connect.bean.UserBean
import com.example.connect.utils.Constants
import com.example.connect.utils.SealedResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepoImpl @Inject constructor(private val firestore: FirebaseFirestore) : MainRepo {
    override suspend fun getAllUsers(): SealedResult<ArrayList<UserBean>> {
        return try {
            val response = firestore.collection(Constants.KEY_USER).get().await()
            val list = arrayListOf<UserBean>()
            response.documents.forEach {
                it.toObject(UserBean::class.java)?.let { user ->
                    list.add(user)
                }
            }
            SealedResult.success(list)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }


    override suspend fun sendFriendRequest(
        userBean: UserBean,
        currentUserBean: UserBean
    ): SealedResult<Nothing?> {
        return try {
            firestore.collection(Constants.KEY_USER).document(currentUserBean.uid).update(
                "${Constants.KEY_FRIEND_REQUEST_SENT}.${userBean.uid}",
                true
            ).await()
            firestore.collection(Constants.KEY_USER).document(userBean.uid).update(
                "${Constants.KEY_FRIEND_REQUEST_RECEIVED}.${currentUserBean.uid}",
                true
            ).await()
            SealedResult.success(null)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }

    override suspend fun getUserFromId(userId: String): SealedResult<UserBean?> {
        return try {
            val response = firestore.collection(Constants.KEY_USER).document(userId).get().await()
            val currentUser = response.toObject(UserBean::class.java)
            SealedResult.success(currentUser)
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }

    override suspend fun getFriendList(userId: String): SealedResult<UserBean?> {
        return try {
            val response = firestore.collection(Constants.KEY_USER).document(userId).get().await()
            SealedResult.success(response.toObject(UserBean::class.java))
        } catch (exception: Exception) {
            SealedResult.error(exception)
        }
    }

//    override suspend fun acceptRequest(
//        currentUserBean: UserBean,
//        userBean: UserBean
//    ): SealedResult<Nothing?> {
//        return try {
//            firestore.collection()
//        } catch (exception: Exception) {
//            SealedResult.error(exception)
//        }
//    }

}