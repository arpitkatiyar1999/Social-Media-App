package com.example.connect.ui.authentication.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.connect.base.BaseViewModel
import com.example.connect.ui.authentication.repo.AuthRepo
import com.example.connect.utils.Event
import com.example.connect.utils.SealedResult
import com.example.connect.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepo: AuthRepo) : BaseViewModel() {
    private val _signInUserLiveData = MutableLiveData<Event<State<Nothing?>>>()
    val signInUserLiveData: LiveData<Event<State<Nothing?>>> get() = _signInUserLiveData

    private val _checkCurrentUserLiveData = MutableLiveData<Event<State<Boolean>>>()
    val checkCurrentUserLiveData: LiveData<Event<State<Boolean>>> get() = _checkCurrentUserLiveData
    fun signInUser(email: String, password: String) {
        _signInUserLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = authRepo.signInUser(email, password)) {
                is SealedResult.Success -> {
                    _signInUserLiveData.postValue(Event(State.success(null)))
                }

                is SealedResult.Error -> {
                    _signInUserLiveData.postValue(
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

    fun checkCurrentUser(uid: String) {
        _checkCurrentUserLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = authRepo.getCurrentUser(uid)) {
                is SealedResult.Success -> {
                    _checkCurrentUserLiveData.postValue(Event(State.success(response.data)))
                }

                is SealedResult.Error -> {
                    _checkCurrentUserLiveData.postValue(
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