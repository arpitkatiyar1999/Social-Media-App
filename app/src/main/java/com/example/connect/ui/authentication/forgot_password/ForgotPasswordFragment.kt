package com.example.connect.ui.authentication.forgot_password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.connect.R
import com.example.connect.base.BaseFragment
import com.example.connect.databinding.FragmentForgotPasswordBinding
import com.example.connect.utils.FieldsValidator
import com.example.connect.utils.State
import com.example.connect.utils.animateView
import com.example.connect.utils.hideKeyboard
import com.example.connect.utils.makeGone
import com.example.connect.utils.makeVisible
import com.example.connect.utils.showSnackbar
import com.example.connect.utils.vibrateDevice
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<ForgotPasswordViewModel>() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    override fun setViewModel(): ForgotPasswordViewModel {
        return ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.forgotPasswordLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            submitIcd.root.alpha = 0.5f
                            submitIcd.root.isEnabled = false
                            submitIcd.btnPb.makeVisible()
                            submitIcd.btnTv.text = getString(R.string.please_wait)
                        }

                        is State.Success -> {
                            submitIcd.root.alpha = 1f
                            submitIcd.root.isEnabled = true
                            submitIcd.btnPb.makeGone()
                            submitIcd.btnTv.text = getString(R.string.submit)
                            root.showSnackbar(getString(R.string.a_reset_password_link_has_been_sent_to_registered_email_address))
                            findNavController().popBackStack()
                        }

                        is State.Error -> {
                            submitIcd.root.alpha = 1f
                            submitIcd.root.isEnabled = true
                            submitIcd.btnPb.makeGone()
                            submitIcd.btnTv.text = getString(R.string.submit)
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
            emailEdit.addTextChangedListener(textWatcher)
            submitIcd.root.setOnClickListener {
                handleSubmitBtn()
            }
        }
    }

    private fun handleSubmitBtn() {
        with(binding) {
            val email = emailEdit.text
            if (checkAllDataEnteredCorrectly(email)) {
                root.hideKeyboard()
                viewModel.forgotPassword(email.toString())
            }
        }
    }

    private fun checkAllDataEnteredCorrectly(email: Editable?): Boolean {
        with(binding) {
            if (email.isNullOrBlank()) {
                root.showSnackbar(getString(R.string.please_enter_email_address))
                emailEdit.requestFocus()
                val scrollTo = emailEdit.bottom
                forgotPasswordSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(emailTil)
                return false
            }
            if (!FieldsValidator.validateEmail(email.toString())) {
                root.showSnackbar(getString(R.string.please_enter_a_valid_email_address))
                emailEdit.requestFocus()
                val scrollTo = emailEdit.bottom
                forgotPasswordSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(emailTil)
                return false
            }
            return true
        }
    }

    private fun initUi() {
        with(binding) {
            submitIcd.btnTv.text = getString(R.string.submit)
            setStatusOfSubmitBtn()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            setStatusOfSubmitBtn()
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

    private fun setStatusOfSubmitBtn() {
        if (binding.emailEdit.text.toString().isNotBlank()) {
            binding.submitIcd.root.alpha = 1f
        } else {
            binding.submitIcd.root.alpha = 0.5f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
