package com.example.handsonchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth

class MessageActivity : AppCompatActivity() {

    private val TAG = "Message Activity"

    private lateinit var binding: ActivityMessageBinding

    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView = binding.recyclerViewMessage

        val messageItems = mutableListOf<MessageItem>()
        messageItems.add(MessageItem("username", "Hello world", ""))
        messageItems.add(MessageItem("username", "Hello world", ""))
        messageItems.add(MessageItem("username", "Hello world", ""))
        messageItems.add(MessageItem("username", "Hello world", ""))

        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = MessageAdapter(
                messageItems,
                object : MessageAdapter.ListListener {
                    override fun onClickItem(tappedView: View, messageItem: MessageItem) {
                    }
                }
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sign_out) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}