package com.example.handsonchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.ActivityChatLogBinding

class ChatLogActivity : AppCompatActivity() {

    private val TAG = "ChatLogActivity"

    private lateinit var binding: ActivityChatLogBinding

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        binding = ActivityChatLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView = binding.recyclerviewChatlog

        val chatLogs = mutableListOf<ChatLogItem>()
        chatLogs.add(ChatLogItem("username", "Hello world", "", true))
        chatLogs.add(ChatLogItem("username", "Hello world", "", false))
        chatLogs.add(ChatLogItem("username", "Hello world", "", true))
        chatLogs.add(ChatLogItem("username", "Hello world", "", false))

        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ChatLogAdapter(
                chatLogs,
                object : AdapterUtil.ListListener<ChatLogItem> {
                    override fun onClickItem(tappedView: View, messageItem: ChatLogItem) {}
                }
            )
        }
    }
}
