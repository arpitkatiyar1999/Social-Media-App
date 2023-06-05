package com.example.connect.ui.authentication.forgot_password

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
class ForgotPasswordViewModel @Inject constructor(private val authRepo: AuthRepo) :
    BaseViewModel() {
    private val _forgotPasswordLiveData = MutableLiveData<Event<State<Void?>>>()
    val forgotPasswordLiveData: LiveData<Event<State<Void?>>> get() = _forgotPasswordLiveData

    fun forgotPassword(email: String) {
        _forgotPasswordLiveData.postValue(Event(State.loading()))
        viewModelScope.launch {
            when (val response = authRepo.forgotPassword(email)) {
                is SealedResult.Success -> {
                    _forgotPasswordLiveData.postValue(Event(State.success(null)))
                }

                is SealedResult.Error -> {
                    _forgotPasswordLiveData.postValue(
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