# アプリナビ Kotlin HandsOn

## 5.2 手直し

今回は以下の画面のようにメッセージ画面にユーザーごとに最新のメッセージを表示させます。

## メッセージ送信時に入力をクリア・下までスクロール

- 送信ボタンを押すとメッセージは送信されますが入力がクリアされず、次にメッセージを入力する前に消さないといけません。
- またメッセージが少ないとあまり気になりませんが、送信しても最新のメッセージまでスクロールされません。
- ということでこれらを実装します。`ChatLogActivity`に以下の処理を追加しましょう。

```diff
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
+            binding.edittextChatlog.text.clear()
+            binding.recyclerviewChatlog.scrollToPosition(chatLogs.count() - 1)
         }
         toRef.setValue(chatMessage)
             .addOnSuccessListener {
                 Log.d(TAG, "Saved our chat message: ${toRef.key}")
             }
 
         val latestFromRef =
             FirebaseDatabase.getInstance().getReference("latest-messages/$fromId/$toId")
         latestFromRef.setValue(chatMessage)
         val latestToRef =
             FirebaseDatabase.getInstance().getReference("latest-messages/$toId/$fromId")
         latestToRef.setValue(chatMessage)
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
```

- 追加できましたら実行してみましょう。メッセージをいくつか送信して送信時に入力がクリアされ、下までスクロールされればOKです。

## アイコンにフレームを追加する。

- 現状アイコンの背景が白だと後ろと同化してしまうので、あまり目立たない程度に枠を設定します。
- `res/layout/message_row.xml`を開き、以下の行を追加します。

```diff
 <?xml version="1.0" encoding="utf-8"?>
 <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
     xmlns:app="http://schemas.android.com/apk/res-auto"
     xmlns:tools="http://schemas.android.com/tools"
     android:layout_width="match_parent"
     android:layout_height="100dp">
 
     <de.hdodenhof.circleimageview.CircleImageView
         android:id="@+id/userimage_imageview_message"
         android:layout_width="64dp"
         android:layout_height="64dp"
         android:layout_marginStart="16dp"
+        app:civ_border_color="@color/color_gray"
+        app:civ_border_width="1dp"
         app:layout_constraintBottom_toBottomOf="parent"
         app:layout_constraintStart_toStartOf="parent"
         app:layout_constraintTop_toTopOf="parent"
         tools:srcCompat="@tools:sample/avatars" />
 
     <TextView
         android:id="@+id/username_textview_message"
         android:layout_width="wrap_content"
         android:layout_height="wrap_content"
         android:layout_marginStart="16dp"
         android:layout_marginBottom="8dp"
         android:text="username"
         android:textSize="16sp"
         android:textStyle="bold"
         app:layout_constraintBottom_toTopOf="@+id/latestmessage_textview_message"
         app:layout_constraintStart_toEndOf="@+id/userimage_imageview_message"
         app:layout_constraintTop_toTopOf="parent"
         app:layout_constraintVertical_chainStyle="packed" />
 
     <TextView
         android:id="@+id/latestmessage_textview_message"
         android:layout_width="0dp"
         android:layout_height="wrap_content"
         android:layout_marginStart="16dp"
         android:layout_marginEnd="8dp"
         android:text="latest message"
         android:textSize="16sp"
         app:layout_constraintBottom_toBottomOf="parent"
         app:layout_constraintEnd_toEndOf="parent"
         app:layout_constraintHorizontal_bias="0.5"
         app:layout_constraintStart_toEndOf="@+id/userimage_imageview_message"
         app:layout_constraintTop_toBottomOf="@+id/username_textview_message" />
 </androidx.constraintlayout.widget.ConstraintLayout>
```

- 同様に`res/layout/chat_from_row.xml`を以下のように編集します。

```diff
 <?xml version="1.0" encoding="utf-8"?>
 <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
     xmlns:app="http://schemas.android.com/apk/res-auto"
     xmlns:tools="http://schemas.android.com/tools"
     android:layout_width="match_parent"
     android:layout_height="wrap_content">
 
     <de.hdodenhof.circleimageview.CircleImageView
         android:id="@+id/imageView_chat_log"
         android:layout_width="50dp"
         android:layout_height="50dp"
         android:layout_marginTop="8dp"
         android:layout_marginEnd="8dp"
+        app:civ_border_color="@color/color_gray"
+        app:civ_border_width="1dp"
         app:layout_constraintEnd_toEndOf="parent"
         app:layout_constraintTop_toTopOf="parent"
         tools:srcCompat="@tools:sample/avatars" />
 
     <TextView
         android:id="@+id/textview_chat_log"
         android:layout_width="wrap_content"
         android:layout_height="wrap_content"
         android:layout_marginEnd="8dp"
         android:background="@drawable/chat_log_from_background"
         android:maxWidth="240dp"
         android:padding="16dp"
         android:text="This is my message that will wrap into multiple lines and keep on going"
         android:textColor="@color/white"
         app:layout_constraintEnd_toStartOf="@+id/imageView_chat_log"
         app:layout_constraintTop_toTopOf="@+id/imageView_chat_log" />
 </androidx.constraintlayout.widget.ConstraintLayout>
```

- `res/layout/chat_to_row.xml`を以下のように編集します。

```diff
 <?xml version="1.0" encoding="utf-8"?>
 <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
     xmlns:app="http://schemas.android.com/apk/res-auto"
     xmlns:tools="http://schemas.android.com/tools"
     android:layout_width="match_parent"
     android:layout_height="wrap_content">
 
     <de.hdodenhof.circleimageview.CircleImageView
         android:id="@+id/imageView_chat_log"
         android:layout_width="50dp"
         android:layout_height="50dp"
         android:layout_marginStart="8dp"
         android:layout_marginTop="8dp"
+        app:civ_border_color="@color/color_gray"
+        app:civ_border_width="1dp"
         app:layout_constraintStart_toStartOf="parent"
         app:layout_constraintTop_toTopOf="parent"
         tools:srcCompat="@tools:sample/avatars" />
 
     <TextView
         android:id="@+id/textview_chat_log"
         android:layout_width="wrap_content"
         android:layout_height="wrap_content"
         android:layout_marginStart="8dp"
         android:background="@drawable/chat_log_to_background"
         android:maxWidth="240dp"
         android:padding="16dp"
         android:text="This is my message that will wrap into multiple lines and keep on going"
         android:textColor="@color/black"
         app:layout_constraintStart_toEndOf="@+id/imageView_chat_log"
         app:layout_constraintTop_toTopOf="@+id/imageView_chat_log" />
 </androidx.constraintlayout.widget.ConstraintLayout>
```

- ついでにMessage画面のアイテム間にDividerを設定します。下図のように線を引きます。

![session5 2-add-frame-message-activity](https://user-images.githubusercontent.com/57338033/157472431-d336005d-a1df-48ad-a342-c22f04f964e8.png)

- `MessageActivity`に以下の処理を追加しましょう

```diff
 package com.example.handsonchatapp
 
 import android.content.Intent
 import androidx.appcompat.app.AppCompatActivity
 import android.os.Bundle
 import android.util.Log
 import android.view.Menu
 import android.view.MenuItem
 import android.view.View
 import androidx.lifecycle.lifecycleScope
+import androidx.recyclerview.widget.DividerItemDecoration
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
 
         recyclerView = binding.recyclerViewMessage
 
+        val dividerItemDecoration =
+            DividerItemDecoration(this, LinearLayoutManager(this).orientation)
+        recyclerView?.addItemDecoration(dividerItemDecoration)
+
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
```

- ここまで追加できましたら実行してみましょう。以下のようにフレームやDividerが表示されて入ればOKです。(若干見にくいですが...)

![session5 2-add-frame-message-activity](https://user-images.githubusercontent.com/57338033/157473072-0502b5ed-c151-45fa-822e-c65fde5ff8e2.png)
![session5 2-add-frame-chat-log-activity](https://user-images.githubusercontent.com/57338033/157473208-c0b9c9d6-41cf-4807-a32a-716cf8274950.png)

## アクションバーのタイトルを変える

- 最後にアクションバーのタイトルを変えましょう。
- `RegisterActivity`を開き、以下の処理を追加します。

```diff
 package com.example.handsonchatapp
 
 import android.app.Activity
 import android.content.Intent
 import android.net.Uri
 import androidx.appcompat.app.AppCompatActivity
 import android.os.Bundle
 import android.provider.MediaStore
 import android.util.Log
 import android.widget.Toast
 import com.example.handsonchatapp.databinding.ActivityRegisterBinding
 import com.google.firebase.auth.FirebaseAuth
 import com.google.firebase.database.FirebaseDatabase
 import com.google.firebase.storage.FirebaseStorage
 import java.util.*
 
 class RegisterActivity : AppCompatActivity() {
     private lateinit var binding: ActivityRegisterBinding
 
     private val TAG = "RegisterActivity"
 
     var selectPhotoUri: Uri? = null
 
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityRegisterBinding.inflate(layoutInflater)
         val view = binding.root
         setContentView(view)
+
+        supportActionBar?.title = "登録"
+
         binding.registerButtonRegister.setOnClickListener {
             performClick()
         }
         binding.haveAccountTextRegister.setOnClickListener {
             Log.d(TAG, "try to show login activity")
 
             val intent = Intent(this, LoginActivity::class.java)
             startActivity(intent)
         }
         binding.selectPhotoButtonRegister.setOnClickListener {
             Log.d(TAG, "Try to show photo selector")
 
             val intent = Intent(Intent.ACTION_PICK)
             intent.type = "image/*"
             startActivityForResult(intent, 0)
         }
     }
 
     private fun performClick() {
         val email = binding.emailEdittextRegister.text.toString()
         val password = binding.passwordEdittextRegister.text.toString()
 
         if (email.isEmpty() || password.isEmpty()) {
             Toast.makeText(this, "Please enter text in email or password", Toast.LENGTH_SHORT)
                 .show()
             return
         }
 
         Log.d(TAG, "Email is: $email")
         Log.d(TAG, "password is: $password")
 
         FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
             .addOnCompleteListener {
                 if (it.isCanceled) {
                     Log.d(TAG, "Canceled")
                 }
                 if (!it.isSuccessful) {
                     Log.d(TAG, "Failed to create user ${it.exception}")
                     return@addOnCompleteListener
                 }
 
                 Log.d(TAG, "Successfully created user with uid: ${it.result.user?.uid}")
                 uploadImageToFirebaseStorage()
             }
             .addOnFailureListener {
                 Log.d(TAG, "failed to create user message ${it.message}")
                 Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
             }
     }
 
     private fun uploadImageToFirebaseStorage() {
         if (selectPhotoUri == null) {
             Toast.makeText(this, "please select an Image", Toast.LENGTH_SHORT).show()
             return
         }
 
         val filename = UUID.randomUUID().toString()
         val ref = FirebaseStorage.getInstance().getReference("image/$filename")
 
         ref.putFile(selectPhotoUri!!)
             .addOnSuccessListener {
                 Log.d(TAG, "Successfully uploaded image:${it.metadata?.path}")
 
                 ref.downloadUrl.addOnSuccessListener {
                     Log.d(TAG, "File location :$it")
 
                     saveUserToFirebaseDatabase(it.toString())
                 }
             }
             .addOnFailureListener {}
     }
 
     private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
         val uid = FirebaseAuth.getInstance().uid ?: ""
         val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
 
         val user = User(uid, binding.usernameEdittextRegister.text.toString(), profileImageUrl)
 
         ref.setValue(user)
             .addOnSuccessListener {
                 Log.d(TAG, "saved the user to Firebase Database")
 
                 val intent = Intent(this, MessageActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                 startActivity(intent)
             }
     }
 
 
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
 
         if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
             Log.d(TAG, "Photo was selected")
 
             selectPhotoUri = data.data
 
             val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)
             binding.circleViewRegister.setImageBitmap(bitmap)
             binding.selectPhotoButtonRegister.alpha = 0f
         }
     }
 }
```

- `LoginActivity`を開き、以下のように編集します・

```diff
  package com.example.handsonchatapp

  import android.content.Intent
  import androidx.appcompat.app.AppCompatActivity
  import android.os.Bundle
  import android.util.Log
  import android.widget.Toast
  import com.example.handsonchatapp.databinding.ActivityLoginBinding
  import com.google.firebase.auth.FirebaseAuth

  class LoginActivity : AppCompatActivity() {
      private lateinit var binding: ActivityLoginBinding

      private val TAG = "LoginActivity"

      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityLoginBinding.inflate(layoutInflater)
          val view = binding.root
          setContentView(view)

+         supportActionBar?.title = "ログイン"

          binding.loginButtonLogin.setOnClickListener {
              val email = binding.emailEdittextLogin.text.toString();
              val password = binding.passwordEdittextLogin.text.toString();

              if (email.isEmpty() || password.isEmpty()) {
                  Toast.makeText(this, "Please enter text in email or password", Toast.LENGTH_SHORT)
                      .show()
                  return@setOnClickListener
              }

              Log.d(TAG, "Email is: $email")
              Log.d(TAG, "password is: $password")

              FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                  .addOnCompleteListener {
                      if (it.isCanceled) {
                          Log.d(TAG, "Canceled")
                      }
                      if (!it.isSuccessful) {
                          Toast.makeText(this, "Failed to Login", Toast.LENGTH_SHORT).show()
                          return@addOnCompleteListener
                      }

                      Log.d(TAG, "Successful Login")
                      val intent = Intent(this, MessageActivity::class.java)
                      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                      startActivity(intent)
                  }
                  .addOnFailureListener {
                      Toast.makeText(this, "Failed to Login", Toast.LENGTH_SHORT).show()
                  }
          }
          binding.backToRegisterTextLogin.setOnClickListener {
              Log.d(TAG, "try to show register activity")

              finish()
          }
      }
  }
```

- `MessageActivity`を開き、以下のように編集します。

```diff
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

+         supportActionBar?.title = "メッセージ"

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
```

- 追加できましたら実行してみましょう。アクションバーにそれぞれ指定した文字が表示されていればOKです

![session5 2-edit-actionbar-login-activity](https://user-images.githubusercontent.com/57338033/157477270-6b07fdc6-435c-4e30-a1d7-88cb3236d120.png)

![session5 2-edit-actionbar-message-activity](https://user-images.githubusercontent.com/57338033/157477278-5b06c779-9675-4273-ab73-c4942115264d.png)

![session5 2-edit-actionbar-register-activity](https://user-images.githubusercontent.com/57338033/157477280-4528422a-d2db-4a7e-a9cd-4309285cc01f.png)

## Diff

[前回との差分](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/compare/session5.1...session5.2)

## 最後に

今回はこれにて終了です。お疲れさまでした。
