package com.example.connect.ui.main.search_user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.connect.R
import com.example.connect.base.BaseFragment
import com.example.connect.bean.UserBean
import com.example.connect.databinding.FragmentSearchUserBinding
import com.example.connect.listeners.OnAddFriendClickListener
import com.example.connect.ui.main.MainActivity
import com.example.connect.ui.main.adapters.SearchUserAdapter
import com.example.connect.utils.State
import com.example.connect.utils.checkCurrentUser
import com.example.connect.utils.makeGone
import com.example.connect.utils.makeInvisible
import com.example.connect.utils.makeVisible
import com.example.connect.utils.navigateUsingAction
import com.example.connect.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchUserFragment : BaseFragment<SearchUserViewModel>(), OnAddFriendClickListener {
    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: SearchUserAdapter
    override fun setViewModel(): SearchUserViewModel {
        return ViewModelProvider(this)[SearchUserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCurrentUser(fireBaseAuth)
        initUi()
        initObservers()
        initListeners()
        if (viewModel.allUsersList.isEmpty())
            viewModel.getAllUsers()
    }


    private fun initListeners() {
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                viewModel.getAllUsers()
                swipeRefresh.isRefreshing = false
            }
            error.retryBtn.setOnClickListener {
                viewModel.getAllUsers()
            }
            friendsTv.setOnClickListener {
                val action =
                    SearchUserFragmentDirections.actionSearchUserFragmentToFriendsListFragment()
                navigateUsingAction(action)
            }
            friendRequestsTv.setOnClickListener {
                val action =
                    SearchUserFragmentDirections.actionSearchUserFragmentToFriendRequestFragment()
                navigateUsingAction(action)
            }
            searchUserSv.setOnQueryTextListener(searchUserQuery)
        }
    }

    private val searchUserQuery = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
            userAdapter.filter.filter(query)
            return true
        }


    }

    private fun initUi() {
        with(binding) {
            userAdapter = SearchUserAdapter(
                viewModel.allUsersList,
                currentUserBean = (requireActivity() as MainActivity).currentUser!!,
                this@SearchUserFragment
            )
            searchUserRv.adapter = userAdapter
        }
    }

    private fun initObservers() {
        observeAllUsersList()
        observeSendRequest()
    }

    private fun observeSendRequest() {
        viewModel.sendFriendRequestLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            it.data?.let {
                                val view =
                                    searchUserRv.layoutManager?.findViewByPosition(it.toInt())
                                val imageView = view?.findViewById<AppCompatImageView>(R.id.cta_iv)
                                val progressBar =
                                    view?.findViewById<ProgressBar>(R.id.progress_pb)
                                imageView?.isEnabled = false
                                imageView?.makeInvisible()
                                progressBar?.makeVisible()
                            }
                        }

                        is State.Success -> {
                            val position = it.data?.substringBefore("#")?.toInt()
                            it.data?.substringAfter("#")?.let { it1 ->
                                (requireActivity() as MainActivity).currentUser?.friendsRequestsSent?.put(
                                    it1, true
                                )
                            }
                            position?.let { pos ->
                                val view = searchUserRv.layoutManager?.findViewByPosition(pos)
                                val imageView = view?.findViewById<AppCompatImageView>(R.id.cta_iv)
                                val progressBar =
                                    view?.findViewById<ProgressBar>(R.id.progress_pb)
                                imageView?.isEnabled = false
                                imageView?.makeGone()
                                progressBar?.makeGone()
                                root.showSnackbar(getString(R.string.friend_request_sent_successfully))
                            }
                        }

                        is State.Error -> {
                            it.data?.let { pos ->
                                val view =
                                    searchUserRv.layoutManager?.findViewByPosition(pos.toInt())
                                val imageView = view?.findViewById<AppCompatImageView>(R.id.cta_iv)
                                val progressBar =
                                    view?.findViewById<ProgressBar>(R.id.progress_pb)
                                imageView?.isEnabled = true
                                imageView?.makeVisible()
                                progressBar?.makeGone()
                                root.showSnackbar(
                                    it.errorMessage ?: getString(R.string.something_went_wrong)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeAllUsersList() {
        viewModel.getAllUsersLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                with(binding) {
                    when (it) {
                        is State.Loading -> {
                            shimmer.root.makeVisible()
                            group.makeGone()
                            shimmer.root.startShimmer()
                            error.root.makeGone()

                        }

                        is State.Success -> {
                            shimmer.root.stopShimmer()
                            shimmer.root.makeGone()
                            error.root.makeGone()
                            if (viewModel.allUsersList.isEmpty()) {
                                group.makeGone()
                            } else {
                                group.makeVisible()
                                userAdapter.notifyDataSetChanged()
                            }
                        }

                        is State.Error -> {
                            error.root.makeVisible()
                            error.errorDescriptionTv.text = it.errorMessage
                            shimmer.root.makeGone()
                            shimmer.root.stopShimmer()
                            group.makeGone()
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAddFriendClicked(userBean: UserBean, position: Int) {
        viewModel.sendFriendRequest(userBean, position)
    }
}