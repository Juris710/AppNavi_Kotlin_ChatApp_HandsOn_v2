package com.example.handsonchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val USER_KEY = "USER_KEY"

suspend fun DatabaseReference.awaitGet(): DataSnapshot {
    return withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            get().addOnSuccessListener {
                continuation.resume(it)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }
}

class MessageActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    private val TAG = "Message Activity"

    private lateinit var binding: ActivityMessageBinding

    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "メッセージ"

        recyclerView = binding.recyclerViewMessage

        val dividerItemDecoration =
            DividerItemDecoration(this, LinearLayoutManager(this).orientation)
        recyclerView?.addItemDecoration(dividerItemDecoration)

        fetchCurrentUser()

        listenForLatestMessage()
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d(TAG, "current User ${currentUser?.username}")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenForLatestMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        lifecycleScope.launch {
            val usersSnapshot = ref.awaitGet()
            val latestMessageItems = usersSnapshot.children.mapNotNull { userSnapshot ->
                val user = userSnapshot.getValue(User::class.java) ?: return@mapNotNull null
                if (user.uid == currentUser?.uid) {
                    return@mapNotNull null
                }
                val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/${user.uid}")
                val latestMessageSnapshot = latestMessageRef.awaitGet()
                val latestMessage = latestMessageSnapshot.getValue(ChatMessage::class.java)
                val message = latestMessage?.text ?: ""
                MessageItem(user, message)
            }
            refreshRecyclerView(latestMessageItems)
        }
    }

    private fun refreshRecyclerView(messageItems: List<MessageItem>) {
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = MessageAdapter(
                messageItems,
                object : AdapterUtil.ListListener<MessageItem> {
                    override fun onClickItem(tappedView: View, messageItem: MessageItem) {
                        val intent = Intent(tappedView.context, ChatLogActivity::class.java)
                        intent.putExtra(USER_KEY, messageItem.user)
                        startActivity(intent)
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