package com.example.connect.ui.authentication.signin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.connect.R
import com.example.connect.base.BaseFragment
import com.example.connect.databinding.FragmentSigninBinding
import com.example.connect.ui.main.MainActivity
import com.example.connect.utils.FieldsValidator
import com.example.connect.utils.State
import com.example.connect.utils.animateView
import com.example.connect.utils.focusScreen
import com.example.connect.utils.makeGone
import com.example.connect.utils.makeVisible
import com.example.connect.utils.showSnackbar
import com.example.connect.utils.toast
import com.example.connect.utils.vibrateDevice
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment : BaseFragment<SignInViewModel>() {
    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    override fun setViewModel(): SignInViewModel {
        return ViewModelProvider(this)[SignInViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.focusScreen()
        initUi()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        observeSignInUser()
    }

    private fun observeSignInUser() {
        observeSigninUser()
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModel.checkCurrentUserLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            //handled in observeSigninUser()
                        }

                        is State.Success -> {
                            signInBtnIcd.root.alpha = 1f
                            signInBtnIcd.root.isEnabled = true
                            signInBtnIcd.btnPb.makeGone()
                            signInBtnIcd.btnTv.text = getString(R.string.sign_in)
                            if (it.data) {
                                viewModel.sharedPref.isUserInfoAdded = true
                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                requireActivity().finish()
                            } else {
                                val action =
                                    SignInFragmentDirections.actionSignInFragmentToUserDetailsFragment()
                                findNavController().navigate(action)
                            }
                            toast(requireContext(), getString(R.string.signed_in_successfully))
                        }

                        is State.Error -> {
                            signInBtnIcd.root.alpha = 1f
                            signInBtnIcd.root.isEnabled = true
                            signInBtnIcd.btnPb.makeGone()
                            signInBtnIcd.btnTv.text = getString(R.string.sign_in)
                            root.showSnackbar(
                                it.errorMessage ?: getString(R.string.something_went_wrong)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeSigninUser() {
        viewModel.signInUserLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            signInBtnIcd.root.alpha = 0.5f
                            signInBtnIcd.root.isEnabled = false
                            signInBtnIcd.btnPb.makeVisible()
                            signInBtnIcd.btnTv.text = getString(R.string.please_wait)
                        }

                        is State.Success -> {
                            fireBaseAuth.currentUser?.let {
                                viewModel.checkCurrentUser(it.uid)
                            }
                        }

                        is State.Error -> {
                            signInBtnIcd.root.alpha = 1f
                            signInBtnIcd.root.isEnabled = true
                            signInBtnIcd.btnPb.makeGone()
                            signInBtnIcd.btnTv.text = getString(R.string.sign_in)
                            root.showSnackbar(
                                it.errorMessage ?: getString(R.string.something_went_wrong)
                            )

                        }
                    }
                }
            }

        }
    }


    private fun initListeners() {
        with(binding) {
            emailEt.addTextChangedListener(textWatcher)
            passwordEt.addTextChangedListener(textWatcher)
            forgotPasswordTv.setOnClickListener {
                val action = SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment()
                findNavController().navigate(action)
            }
            signupTv.setOnClickListener {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                findNavController().navigate(action)
            }
            signInBtnIcd.root.setOnClickListener {
                handleSignInBtn()
            }
        }
    }

    private fun handleSignInBtn() {
        with(binding) {
            val email = emailEt.text
            val password = passwordEt.text
            if (checkAllFieldsEnteredCorrectly(email, password)) {
                viewModel.signInUser(email.toString(), password.toString())
            }
        }
    }

    private fun checkAllFieldsEnteredCorrectly(email: Editable?, password: Editable?): Boolean {
        with(binding) {
            if (email.isNullOrBlank()) {
                root.showSnackbar(getString(R.string.please_enter_email_address))
                emailEt.requestFocus()
                val scrollTo = emailEt.bottom
                signInSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(emailTil)
                return false
            }
            if (!FieldsValidator.validateEmail(email.toString())) {
                root.showSnackbar(getString(R.string.please_enter_a_valid_email_address))
                emailEt.requestFocus()
                val scrollTo = emailEt.bottom
                signInSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(emailTil)
                return false
            }
            if (password.isNullOrBlank()) {
                root.showSnackbar(getString(R.string.please_enter_the_password))
                passwordEt.requestFocus()
                val scrollTo = passwordEt.bottom
                signInSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(passwordTil)
                return false
            }
            if (password.length < 6) {
                root.showSnackbar(getString(R.string.password_must_be_atleast_6_digits))
                passwordEt.requestFocus()
                val scrollTo = passwordEt.bottom
                signInSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(passwordTil)
                return false
            }
        }
        return true
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            handleSignInBtnStatus()
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    private fun initUi() {
        with(binding) {
            signInBtnIcd.btnTv.text = getString(R.string.sign_in)
            handleSignInBtnStatus()
        }
    }

    private fun handleSignInBtnStatus() {
        with(binding) {
            val isEmailAdded = emailEt.text.toString().isNotBlank()
            val isPasswordAdded = passwordEt.text.toString().isNotBlank()
            if (isEmailAdded && isPasswordAdded) {
                signInBtnIcd.root.alpha = 1.0f

            } else {
                signInBtnIcd.root.alpha = 0.5f
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}