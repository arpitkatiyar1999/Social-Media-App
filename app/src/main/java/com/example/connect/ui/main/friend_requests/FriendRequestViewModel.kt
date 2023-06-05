package com.example.connect.ui.main.friend_requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.connect.base.BaseViewModel
import com.example.connect.bean.UserBean
import com.example.connect.ui.main.repo.MainRepo
import com.example.connect.utils.Event
import com.example.connect.utils.SealedResult
import com.example.connect.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(private val mainRepo: MainRepo) : BaseViewModel() {

    private var _friendListLiveData = MutableLiveData<Event<State<Nothing?>>>()
    val friendListLiveData: LiveData<Event<State<Nothing?>>> get() = _friendListLiveData
    val friendList = arrayListOf<UserBean>()

    private val _acceptFriendRequestLiveData = MutableLiveData<Event<State<Nothing?>>>()
    val acceptFriendRequestLiveData: LiveData<Event<State<Nothing?>>> get() = _acceptFriendRequestLiveData


    fun getFriendList() {
        if (currentUser?.uid == null)
            return
        _friendListLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = mainRepo.getFriendList(currentUser!!.uid)) {
                is SealedResult.Success -> {
                    val friendMap = response.data?.friendsRequestsReceived
                    friendList.clear()
                    friendMap?.forEach {
                        if (it.value) {
                            when (val resp = mainRepo.getUserFromId(it.key)) {
                                is SealedResult.Success -> {
                                    resp.data?.let { it1 -> friendList.add(it1) }
                                }

                                is SealedResult.Error -> {
                                    //no need to handle it
                                }
                            }

                        }
                    }
                    _friendListLiveData.postValue(Event(State.success(null)))
                }

                is SealedResult.Error -> {
                    _friendListLiveData.postValue(
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

    fun acceptFriendRequest(userBean: UserBean, position: Int) {
        _acceptFriendRequestLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {

        }
    }
}