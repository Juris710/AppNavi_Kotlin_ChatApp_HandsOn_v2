package com.example.handsonchatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.ChatFromRowBinding
import com.example.handsonchatapp.databinding.ChatToRowBinding
import com.squareup.picasso.Picasso

class ChatLogAdapter(
    private val list: List<ChatLogItem>,
    private val listener: AdapterUtil.ListListener<ChatLogItem>
) : RecyclerView.Adapter<ChatLogViewHolder>() {

    override fun getItemViewType(position: Int): Int = if (list[position].isFrom) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogViewHolder {
        return if (viewType == 0) {
            val binding =
                ChatFromRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ChatFromRowViewHolder(binding)
        } else {
            val binding =
                ChatToRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ChatToRowViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ChatLogViewHolder, position: Int) {
        holder.chatLog.text = list[position].message
        val url = list[position].profileImageUrl
        if (url != "") {
            Picasso.get().load(url).into(holder.profileImage)
        }
        holder.itemView.setOnClickListener {
            listener.onClickItem(it, list[position])
        }
    }

    override fun getItemCount(): Int = list.size
}

abstract class ChatLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract val chatLog: TextView
    abstract val profileImage: ImageView
}

class ChatFromRowViewHolder(private val binding: ChatFromRowBinding) :
    ChatLogViewHolder(binding.root) {
    override val chatLog: TextView
        get() = binding.textviewChatLog
    override val profileImage: ImageView
        get() = binding.imageViewChatLog
}

class ChatToRowViewHolder(private val binding: ChatToRowBinding) : ChatLogViewHolder(binding.root) {
    override val chatLog: TextView
        get() = binding.textviewChatLog
    override val profileImage: ImageView
        get() = binding.imageViewChatLog
}

class ChatLogItem(
    val username: String,
    val message: String,
    val profileImageUrl: String,
    val isFrom: Boolean
) {
    constructor() : this("", "", "", false)
}