package com.example.handsonchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.ActivityChatLogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChatLogActivity : AppCompatActivity() {

    private val TAG = "ChatLogActivity"

    private lateinit var binding: ActivityChatLogBinding

    private var recyclerView: RecyclerView? = null

    var toUser: User? = null

    val chatLogs = mutableListOf<ChatLogItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        binding = ActivityChatLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toUser = intent.getParcelableExtra(USER_KEY)
        supportActionBar?.title = toUser?.username

        recyclerView = binding.recyclerviewChatlog

        listenForMessage()

        binding.sendButtonChatlog.setOnClickListener {
            performSendMessage()
        }
    }

    private fun refreshRecyclerView(list: List<ChatLogItem>) {
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ChatLogAdapter(
                list,
                object : AdapterUtil.ListListener<ChatLogItem> {
                    override fun onClickItem(tappedView: View, messageItem: ChatLogItem) {}
                }
            )
        }
    }

    private fun performSendMessage() {
        val user = intent.getParcelableExtra<User>(USER_KEY)
        val text = binding.edittextChatlog.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid

        if (fromId == null || toId == null || text == "") return

        val fromRef =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage =
            ChatMessage(fromRef.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        fromRef.setValue(chatMessage).addOnSuccessListener {
            Log.d(TAG, "Saved our chat message: ${fromRef.key}")
        }
        toRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${toRef.key}")
            }
    }

    private fun listenForMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                val myId = FirebaseAuth.getInstance().uid
                val user =
                    (if (chatMessage?.fromId == myId) MessageActivity.currentUser else toUser)
                        ?: return

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    chatLogs.add(
                        ChatLogItem(
                            user.username,
                            chatMessage.text,
                            user.profileImageUrl,
                            chatMessage.fromId == myId
                        )
                    )
                }
                refreshRecyclerView(chatLogs)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }
}
