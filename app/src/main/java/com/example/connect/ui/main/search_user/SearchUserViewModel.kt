package com.example.connect.ui.main.search_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.connect.base.BaseViewModel
import com.example.connect.bean.UserBean
import com.example.connect.ui.main.repo.MainRepo
import com.example.connect.utils.Event
import com.example.connect.utils.SealedResult
import com.example.connect.utils.State
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val mainRepo: MainRepo,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {
    private val _getAllUsersLiveData = MutableLiveData<Event<State<Void?>>>()
    val getAllUsersLiveData: LiveData<Event<State<Void?>>> get() = _getAllUsersLiveData

    private val _sendFriendRequestLiveData = MutableLiveData<Event<State<String?>>>()
    val sendFriendRequestLiveData: LiveData<Event<State<String?>>> get() = _sendFriendRequestLiveData
    val allUsersList = arrayListOf<UserBean>()

    fun getAllUsers() {
        _getAllUsersLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = mainRepo.getAllUsers()) {
                is SealedResult.Success -> {
                    allUsersList.clear()
                    allUsersList.addAll(response.data)
                    allUsersList.removeIf {
                        it.uid == currentUser?.uid
                    }
                    _getAllUsersLiveData.postValue(Event(State.success(null)))
                }

                is SealedResult.Error -> {
                    _getAllUsersLiveData.postValue(
                        Event(
                            State.error(
                                response.exception.localizedMessage ?: "",
                                response.exception
                            )
                        )
                    )
                }
            }
        }
    }

    fun sendFriendRequest(userBean: UserBean, position: Int) {
        if (currentUser == null)
            return
        _sendFriendRequestLiveData.postValue(Event(State.loading(data = position.toString())))
        viewModelScope.launch {
            when (val response = mainRepo.sendFriendRequest(userBean, currentUser!!)) {
                is SealedResult.Success -> {
                    _sendFriendRequestLiveData.postValue(Event(State.success("$position#${userBean.uid}")))
                }

                is SealedResult.Error -> {
                    _sendFriendRequestLiveData.postValue(
                        Event(
                            State.error(
                                response.exception.localizedMessage ?: "",
                                response.exception,
                                data = position.toString()
                            )
                        )
                    )

                }
            }
        }
    }
}