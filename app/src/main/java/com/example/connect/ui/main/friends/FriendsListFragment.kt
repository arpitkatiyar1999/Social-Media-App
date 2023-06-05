package com.example.connect.ui.main.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.connect.R
import com.example.connect.base.BaseFragment
import com.example.connect.databinding.FragmentFriendsListBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FriendsListFragment : BaseFragment<FriendsListViewModel>() {
    private var _binding: FragmentFriendsListBinding? = null
    private val binding get() = _binding!!
    override fun setViewModel(): FriendsListViewModel {
        return ViewModelProvider(this)[FriendsListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {

    }

    private fun initUi() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}