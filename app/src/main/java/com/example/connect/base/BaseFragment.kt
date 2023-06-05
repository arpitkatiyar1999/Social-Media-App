package com.example.connect.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.connect.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

abstract class BaseFragment<T : BaseViewModel> : Fragment() {
    @Inject
    open lateinit var fireBaseAuth: FirebaseAuth

    open lateinit var viewModel: T
    abstract fun setViewModel(): T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = setViewModel()
        viewModel.currentUser =
            if (requireActivity() is MainActivity) (requireActivity() as MainActivity).currentUser else null
    }
}