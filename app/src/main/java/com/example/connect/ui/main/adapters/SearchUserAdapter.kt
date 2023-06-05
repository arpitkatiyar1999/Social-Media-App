package com.example.connect.ui.main.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.R
import com.example.connect.bean.UserBean
import com.example.connect.databinding.ItemSearchUserBinding
import com.example.connect.listeners.OnAddFriendClickListener

class SearchUserAdapter(
    private val userList: ArrayList<UserBean>,
    private val currentUserBean: UserBean,
    private val onAddFriendClickListener: OnAddFriendClickListener
) :
    RecyclerView.Adapter<SearchUserAdapter.ViewHolder>(), Filterable {

    private var userListFull = ArrayList<UserBean>(userList)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = userList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    inner class ViewHolder(private val binding: ItemSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ctaIv.setOnClickListener {
                onAddFriendClickListener.onAddFriendClicked(
                    userList[adapterPosition],
                    adapterPosition
                )
            }
        }

        fun bind(userBean: UserBean) {
            with(binding) {
                friendNameTv.text = userBean.name
                Glide.with(friendIv).load(userBean.imgUrl).placeholder(R.drawable.ic_default_user)
                    .into(friendIv)
                if (userBean.uid in currentUserBean.friendsRequestsSent.keys) {
                    ctaIv.setImageResource(R.drawable.ic_in_progress)
                    ctaIv.isEnabled = false
                    if (userBean.uid in currentUserBean.friends.keys) {
                        ctaIv.setImageResource(R.drawable.ic_check)
                        ctaIv.isEnabled = false
                    }
                }
            }
        }
    }

    override fun getFilter() = userFilter

    private val userFilter = object : Filter() {
        override fun performFiltering(query: CharSequence?): FilterResults {
            Log.e("abc", "performFiltering: $userListFull", )
            val filteredList = arrayListOf<UserBean>()
            if (query.isNullOrBlank()) {
                Log.e("abc", "performFiltering: empty ${query}")
                filteredList.addAll(userListFull)
                Log.e("abc", "performFiltering: ", )
            } else {
                Log.e("abc", "performFiltering:  data ${query}")
                val finalQuery = query.toString().lowercase().trim()
                userListFull.forEach {
                    if (it.name.lowercase().contains(finalQuery)) {
                        filteredList.add(it)
                    }
                }
            }
            val filterResult = FilterResults()
            filterResult.values = filteredList
            return filterResult
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
            userList.clear()
            val resultData = filterResults?.values as ArrayList<UserBean>
            userList.addAll(resultData)
            notifyDataSetChanged()
        }

    }
}