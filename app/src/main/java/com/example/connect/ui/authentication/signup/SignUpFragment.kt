package com.example.connect.ui.authentication.signup

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
import com.example.connect.databinding.FragmentSignUpBinding
import com.example.connect.utils.FieldsValidator
import com.example.connect.utils.State
import com.example.connect.utils.animateView
import com.example.connect.utils.focusScreen
import com.example.connect.utils.makeGone
import com.example.connect.utils.makeVisible
import com.example.connect.utils.showSnackbar
import com.example.connect.utils.vibrateDevice
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<SignUpViewModel>() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    override fun setViewModel(): SignUpViewModel {
        return ViewModelProvider(this)[SignUpViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
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
        with(binding) {
            viewModel.createAccountLiveData.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    when (it) {
                        is State.Loading -> {
                            continueIcd.root.alpha = 0.5f
                            continueIcd.root.isEnabled = false
                            continueIcd.btnPb.makeVisible()
                            continueIcd.btnTv.text = getString(R.string.creating_account)
                        }

                        is State.Success -> {
                            continueIcd.root.alpha = 1f
                            continueIcd.root.isEnabled = true
                            continueIcd.btnPb.makeGone()
                            continueIcd.btnTv.text = getString(R.string.sign_in)
                            root.showSnackbar(getString(R.string.account_created_successfully))
                            fireBaseAuth.signOut()
                            findNavController().popBackStack()
                        }

                        is State.Error -> {
                            vibrateDevice(requireContext(), time = 500)
                            continueIcd.root.alpha = 1f
                            continueIcd.root.isEnabled = true
                            continueIcd.btnPb.makeGone()
                            continueIcd.btnTv.text = getString(R.string.sign_in)
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
            confirmPasswordEt.addTextChangedListener(textWatcher)
            signInTv.setOnClickListener {
                findNavController().popBackStack()
            }
            continueIcd.root.setOnClickListener {
                handleContinueBtn()

            }
        }
    }

    private fun handleContinueBtn() {
        with(binding) {
            val email = emailEt.text
            val password = passwordEt.text
            val confirmPassword = confirmPasswordEt.text
            if (checkAllDataEnteredCorrectly(email, password, confirmPassword)) {
                viewModel.createAccount(email.toString(), password.toString())
            }
        }
    }

    private fun checkAllDataEnteredCorrectly(
        email: Editable?,
        password: Editable?,
        confirmPassword: Editable?
    ): Boolean {
        with(binding) {
            if (email.isNullOrBlank()) {
                root.showSnackbar(getString(R.string.please_enter_email_address))
                emailEt.requestFocus()
                val scrollTo = emailEt.bottom
                signupSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(emailTil)
                return false
            }
            if (!FieldsValidator.validateEmail(email.toString())) {
                root.showSnackbar(getString(R.string.please_enter_a_valid_email_address))
                emailEt.requestFocus()
                val scrollTo = emailEt.bottom
                signupSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(emailTil)
                return false
            }
            if (password.isNullOrBlank()) {
                root.showSnackbar(getString(R.string.please_enter_the_password))
                passwordEt.requestFocus()
                val scrollTo = passwordEt.bottom
                signupSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(passwordTil)
                return false
            }
            if (password.length < 6) {
                root.showSnackbar(getString(R.string.password_must_be_atleast_6_digits))
                passwordEt.requestFocus()
                val scrollTo = passwordEt.bottom
                signupSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(passwordTil)
                return false
            }
            if (confirmPassword.toString() != password.toString()) {
                root.showSnackbar(getString(R.string.confirm_password_and_password_are_not_same))
                confirmPasswordEt.requestFocus()
                val scrollTo = confirmPasswordEt.bottom
                signupSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(confirmPasswordTil)
                return false
            }
        }
        return true
    }

    private fun initUi() {
        with(binding) {
            setStatusOfContinueBtn()
            continueIcd.btnTv.text = getString(R.string.continue_txt)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            setStatusOfContinueBtn()
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

    private fun setStatusOfContinueBtn() {
        with(binding) {
            val email = emailEt.text
            val password = passwordEt.text
            val confirmPassword = confirmPasswordEt.text
            if (!email.isNullOrBlank() && !password.isNullOrBlank() && password.length >= 6 && !confirmPassword.isNullOrBlank() &&
                confirmPassword.toString() == password.toString()
            ) {
                continueIcd.root.alpha = 1f
            } else {
                continueIcd.root.alpha = 0.5f
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}