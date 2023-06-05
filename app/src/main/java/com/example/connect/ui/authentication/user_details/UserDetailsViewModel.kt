package com.example.connect.ui.authentication.user_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.connect.base.BaseViewModel
import com.example.connect.bean.UserBean
import com.example.connect.ui.authentication.repo.AuthRepo
import com.example.connect.utils.Event
import com.example.connect.utils.SealedResult
import com.example.connect.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserDetailsViewModel @Inject constructor(private val authRepo: AuthRepo) : BaseViewModel() {
    private val _createUserLiveData = MutableLiveData<Event<State<Void?>>>()
    val createUserLiveData: LiveData<Event<State<Void?>>> get() = _createUserLiveData

    fun createUser(user: UserBean, uid: String) {
        _createUserLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = authRepo.createUser(user, uid)) {
                is SealedResult.Success -> {
                    _createUserLiveData.postValue(Event(State.success(response.data)))
                }

                is SealedResult.Error -> {
                    _createUserLiveData.postValue(
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