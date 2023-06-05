package com.example.connect.ui.authentication.user_details

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.connect.R
import com.example.connect.base.BaseFragment
import com.example.connect.bean.UserBean
import com.example.connect.databinding.FragmentUserDetailsBinding
import com.example.connect.ui.main.MainActivity
import com.example.connect.utils.Constants.genderList
import com.example.connect.utils.State
import com.example.connect.utils.animateView
import com.example.connect.utils.getMonthNameFromMonthNumber
import com.example.connect.utils.makeGone
import com.example.connect.utils.makeVisible
import com.example.connect.utils.removeAlpha
import com.example.connect.utils.setAlpha
import com.example.connect.utils.showSnackbar
import com.example.connect.utils.toast
import com.example.connect.utils.vibrateDevice
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class UserDetailsFragment : BaseFragment<UserDetailsViewModel>() {
    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var datePickerDialog: DatePickerDialog
    override fun setViewModel(): UserDetailsViewModel {
        return ViewModelProvider(this)[UserDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        observeCreateUser()
    }

    private fun observeCreateUser() {
        viewModel.createUserLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            registerIcd.root.alpha = 0.5f
                            registerIcd.root.isEnabled = false
                            registerIcd.btnPb.makeVisible()
                            registerIcd.btnTv.text = getString(R.string.please_wait)
                        }

                        is State.Success -> {
                            registerIcd.root.alpha = 1f
                            registerIcd.root.isEnabled = true
                            registerIcd.btnPb.makeGone()
                            registerIcd.btnTv.text = getString(R.string.sign_in)
                            viewModel.sharedPref.isUserInfoAdded = true
                            toast(requireContext(), getString(R.string.signed_in_successfully))
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }

                        is State.Error -> {
                            registerIcd.root.alpha = 1f
                            registerIcd.root.isEnabled = true
                            registerIcd.btnPb.makeGone()
                            registerIcd.btnTv.text = getString(R.string.sign_in)
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
            dateOfBirthActv.setOnClickListener {
                datePickerDialog.show()
            }
            fullNameEt.addTextChangedListener(textWatcher)
            dateOfBirthActv.addTextChangedListener(textWatcher)
            genderActv.addTextChangedListener(textWatcher)
            registerIcd.root.setOnClickListener {
                handleRegisterBtn()
            }
        }
    }

    private fun checkAllDataEntered(name: Editable?, dob: Editable, gender: Editable): Boolean {
        with(binding) {
            if (name.isNullOrBlank()) {
                root.showSnackbar(getString(R.string.please_enter_your_name))
                fullNameEt.requestFocus()
                val scrollTo = fullNameEt.bottom
                userDetailsSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(fullNameTil)
                return false
            }
            if (dob.isBlank()) {
                root.showSnackbar(getString(R.string.please_select_your_date_of_birth))
                dateOfBirthActv.requestFocus()
                val scrollTo = dateOfBirthActv.bottom
                userDetailsSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(dateOfBirthTil)
                return false
            }
            if (gender.isBlank()) {
                root.showSnackbar(getString(R.string.please_select_your_gender))
                genderActv.requestFocus()
                val scrollTo = genderActv.bottom
                userDetailsSv.smoothScrollTo(0, scrollTo)
                vibrateDevice(requireContext())
                animateView(genderTil)
                return false
            }
        }
        return true
    }

    private fun handleRegisterBtn() {
        with(binding) {
            val name = fullNameEt.text
            val dob = dateOfBirthActv.text
            val gender = genderActv.text
            if (checkAllDataEntered(name, dob, gender)) {
                fireBaseAuth.currentUser?.let {
                    val user = UserBean(it.uid, name.toString(), gender.toString(), dob.toString())
                    viewModel.createUser(user, it.uid)
                }
            }
        }
    }

    private fun initUi() {
        with(binding) {
            registerIcd.btnTv.text = getString(R.string.register)
            setUpDobCalender()
            setUpGenderDropDown()
            setStatusOfRegisterBtn()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            setStatusOfRegisterBtn()
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

    private fun setUpDobCalender() {
        val calender = Calendar.getInstance()
        val yrs = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        if ((!::datePickerDialog.isInitialized)) {
            datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.MyDatePickerStyle,
                datePickerResult,
                yrs,
                month,
                day
            )
            datePickerDialog.datePicker.maxDate = Date().time
        }
    }

    private val datePickerResult =
        DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
            val date = "$dayOfMonth ${getMonthNameFromMonthNumber(monthOfYear)} $year"
            datePicker.updateDate(year, monthOfYear, dayOfMonth)
            binding.dateOfBirthActv.setText(date)
        }

    private fun setUpGenderDropDown() {
        val genderAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                genderList
            )
        binding.genderActv.setAdapter(genderAdapter)
    }

    private fun setStatusOfRegisterBtn() {
        with(binding) {
            val name = fullNameEt.text
            val dob = dateOfBirthActv.text
            val gender = genderActv.text
            if (!name.isNullOrBlank() && !dob.isNullOrBlank() && !gender.isNullOrBlank()) {
                registerIcd.root.removeAlpha()
            } else {
                registerIcd.root.setAlpha()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}