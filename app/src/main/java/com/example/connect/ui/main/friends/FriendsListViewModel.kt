package com.example.connect.ui.main.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connect.base.BaseViewModel
import com.example.connect.bean.UserBean
import com.example.connect.ui.main.repo.MainRepo
import com.example.connect.utils.Event
import com.example.connect.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(private val mainRepo: MainRepo) : BaseViewModel() {
    private val _friendListLiveData = MutableLiveData<Event<State<Nothing?>>>()
    val friendListLiveData: LiveData<Event<State<Nothing?>>> get() = _friendListLiveData
    val friendList = arrayListOf<UserBean>()
    fun getFriendList() {

    }
}