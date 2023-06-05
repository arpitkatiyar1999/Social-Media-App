package com.example.connect.ui.main.friend_requests

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.connect.R
import com.example.connect.base.BaseFragment
import com.example.connect.bean.UserBean
import com.example.connect.databinding.FragmentFriendRequestBinding
import com.example.connect.listeners.FriendRequestResponseClickListener
import com.example.connect.ui.main.adapters.FriendRequestAdapter
import com.example.connect.utils.State
import com.example.connect.utils.makeGone
import com.example.connect.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendRequestFragment : BaseFragment<FriendRequestViewModel>(),
    FriendRequestResponseClickListener {
    private var _binding: FragmentFriendRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FriendRequestAdapter
    override fun setViewModel(): FriendRequestViewModel {
        return ViewModelProvider(this)[FriendRequestViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        initListeners()
        if (viewModel.friendList.isEmpty())
            viewModel.getFriendList()
    }

    private fun initListeners() {
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                viewModel.getFriendList()
                swipeRefresh.isRefreshing = false
            }
            error.retryBtn.setOnClickListener {
                viewModel.getFriendList()
            }
        }
    }

    private fun initObservers() {
        observeFriendList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeFriendList() {
        viewModel.friendListLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            shimmer.root.makeVisible()
                            shimmer.root.startShimmer()
                            error.root.makeGone()
                            noData.root.makeGone()
                            group.makeGone()
                        }

                        is State.Success -> {
                            shimmer.root.makeGone()
                            shimmer.root.stopShimmer()
                            error.root.makeGone()
                            if (viewModel.friendList.isNotEmpty()) {
                                noData.root.makeGone()
                                group.makeVisible()
                                if (viewModel.friendList.size == 1) {
                                    friendsRequestsTv.text = getString(
                                        R.string.int_string_placeholder,
                                        viewModel.friendList.size,
                                        getString(R.string.request)
                                    )
                                } else {
                                    friendsRequestsTv.text = getString(
                                        R.string.int_string_placeholder,
                                        viewModel.friendList.size,
                                        getString(R.string.requests)
                                    )
                                }
                                adapter.notifyDataSetChanged()
                            } else {
                                noData.root.makeVisible()
                                group.makeGone()
                                noData.noDataDescTv.text = getString(R.string.no_friend_requests)
                            }
                        }

                        is State.Error -> {
                            shimmer.root.makeGone()
                            shimmer.root.stopShimmer()
                            error.root.makeVisible()
                            noData.root.makeGone()
                            error.errorDescriptionTv.text = it.errorMessage
                            group.makeGone()
                        }
                    }
                }
            }

        }
    }

    private fun initUi() {
        with(binding) {
            adapter = FriendRequestAdapter(viewModel.friendList, this@FriendRequestFragment)
            friendsRequestsRv.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFriendRequestAccepted(userBean: UserBean, position: Int) {
        
    }

    override fun onFriendRequestDeclined(userBean: UserBean, position: Int) {

    }

}