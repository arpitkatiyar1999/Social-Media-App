package com.example.connect.base

import androidx.lifecycle.ViewModel
import com.example.connect.bean.UserBean
import com.example.connect.utils.AppSharedPref
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {
    @Inject
    open lateinit var sharedPref: AppSharedPref
    open var currentUser: UserBean? = null
}