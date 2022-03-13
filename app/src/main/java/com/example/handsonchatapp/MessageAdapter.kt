package com.example.handsonchatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.MessageRowBinding

class MessageAdapter(
    private val messageItems: List<MessageItem>,
    private val listener: ListListener
) : RecyclerView.Adapter<MessageViewHolder>() {

    interface ListListener {
        fun onClickItem(tappedView: View, messageItem: MessageItem)
    }

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
    fun bind(item: MessageItem, listener: MessageAdapter.ListListener) {
        itemBinding.usernameTextviewMessage.text = item.username
        itemBinding.latestmessageTextviewMessage.text = item.message
        val userImage = itemBinding.userimageImageviewMessage
        itemBinding.root.setOnClickListener {
            listener.onClickItem(it, item)
        }
    }
}

class MessageItem(val username: String, val message: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}