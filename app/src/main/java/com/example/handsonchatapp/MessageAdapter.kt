package com.example.handsonchatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.MessageRowBinding
import com.squareup.picasso.Picasso

class MessageAdapter(
    private val messageItems: List<MessageItem>,
    private val listener: AdapterUtil.ListListener<MessageItem>
) : RecyclerView.Adapter<MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemBinding =
            MessageRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageItems[position], listener)
    }

    override fun getItemCount(): Int = messageItems.size
}

class MessageViewHolder(private val itemBinding: MessageRowBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(item: MessageItem, listener: AdapterUtil.ListListener<MessageItem>) {
        itemBinding.usernameTextviewMessage.text = item.username
        itemBinding.latestmessageTextviewMessage.text = item.message
        val userImage = itemBinding.userimageImageviewMessage
        Picasso.get().load(item.profileImageUrl).into(userImage)
        itemBinding.root.setOnClickListener {
            listener.onClickItem(it, item)
        }
    }
}

class MessageItem(val username: String, val message: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}