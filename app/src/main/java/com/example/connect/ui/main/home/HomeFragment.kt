package com.example.connect.ui.main.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.connect.base.BaseFragment
import com.example.connect.databinding.FragmentHomeBinding
import com.example.connect.ui.main.MainActivity
import com.example.connect.utils.State
import com.example.connect.utils.makeGone
import com.example.connect.utils.makeVisible
import com.example.connect.utils.navigateUsingAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel>() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var dialog: AlertDialog? = null

    override fun setViewModel(): HomeViewModel {
        return ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            searchIv.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchUserFragment()
                navigateUsingAction(action)
            }
        }
    }

    private fun initObservers() {
        viewModel.currentUserLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            searchIv.makeGone()
                        }

                        is State.Success -> {
                            searchIv.makeVisible()
                            (requireActivity() as MainActivity).currentUser = it.data
                            viewModel.currentUser = it.data
                            Log.e("abc", "initObservers: ${viewModel.currentUser}")
                        }

                        is State.Error -> {

                        }
                    }

                }
            }
        }
    }


    private fun initUi() {
        fireBaseAuth.currentUser?.uid?.let { viewModel.getCurrentUser(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}