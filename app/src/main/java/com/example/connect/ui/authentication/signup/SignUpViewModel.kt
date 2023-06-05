package com.example.connect.ui.authentication.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.connect.base.BaseViewModel
import com.example.connect.ui.authentication.repo.AuthRepo
import com.example.connect.utils.Event
import com.example.connect.utils.SealedResult
import com.example.connect.utils.State
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepo: AuthRepo) : BaseViewModel() {
    private val _createAccountLiveData = MutableLiveData<Event<State<AuthResult>>>()
    val createAccountLiveData: LiveData<Event<State<AuthResult>>> get() = _createAccountLiveData

    fun createAccount(email: String, password: String) {
        _createAccountLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = authRepo.createAccount(email, password)) {
                is SealedResult.Success -> {
                    _createAccountLiveData.postValue(Event(State.success(response.data)))
                }

                is SealedResult.Error -> {
                    _createAccountLiveData.postValue(
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