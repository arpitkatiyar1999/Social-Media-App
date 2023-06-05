package com.example.connect.ui.main.home

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
class HomeViewModel @Inject constructor(private val mainRepo: MainRepo) : BaseViewModel() {

    private val _currentUserLiveData = MutableLiveData<Event<State<UserBean?>>>()
    val currentUserLiveData: LiveData<Event<State<UserBean?>>> get() = _currentUserLiveData

    fun getCurrentUser(uid: String) {
        _currentUserLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = mainRepo.getUserFromId(uid)) {
                is SealedResult.Success -> {
                    _currentUserLiveData.postValue(Event(State.success(response.data)))
                }

                is SealedResult.Error -> {
                    _currentUserLiveData.postValue(
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
}