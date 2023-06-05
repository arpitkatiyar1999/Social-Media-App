package com.example.connect.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.R
import com.example.connect.bean.UserBean
import com.example.connect.databinding.ItemFriendRequestsBinding
import com.example.connect.listeners.FriendRequestResponseClickListener

class FriendRequestAdapter(
    private val friendRequestList: ArrayList<UserBean>,
    private val friendRequestResponseClickListener: FriendRequestResponseClickListener
) :
    RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFriendRequestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = friendRequestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friendRequestList[position])
    }

    inner class ViewHolder(private val binding: ItemFriendRequestsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.acceptBtn.setOnClickListener {
                friendRequestResponseClickListener.onFriendRequestAccepted(
                    friendRequestList[adapterPosition],
                    adapterPosition
                )
            }
            binding.cancelBtn.setOnClickListener {
                friendRequestResponseClickListener.onFriendRequestDeclined(
                    friendRequestList[adapterPosition],
                    adapterPosition
                )
            }
        }

        fun bind(userBean: UserBean) {
            with(binding) {
                friendNameTv.text = userBean.name
                Glide.with(friendIv).load(userBean.imgUrl).placeholder(R.drawable.ic_default_user)
                    .into(friendIv)

            }
        }
    }
}
