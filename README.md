# アプリナビ Kotlin HandsOn

## 5.1 メッセージ画面に最新のメッセージを表示する

今回は以下の画面のようにメッセージ画面にユーザーごとに最新のメッセージを表示させます。

## 最新のメッセージをデータベースに保存する。

- 前のセクションでチャット画面にて送信ボタンを押すとメッセージを`user-messages`の下のパスに保存するよう実装しました。
- 今回はさらに送信ボタンが押されると別のパスにも保存するように実装します。
- わざわざ同じ内容を２つ保存する理由は保存の方法が違うからです。
- 前のセクションで追加した、データベースを参照する処理は以下のとおりです。

```kotlin
  val fromRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
```

- `.push()`とついているため、`setValue()`を実行する度に新たなデータが追加されていきます。
- 今回は追加されるのではなく上書きで保存するように実装します。
- データを追加するように保存すると取得する際、追加されたデータの数だけ通信量が増えます。
- そのため、上書きでも保存することで通信量を少なく、一定に保つ事ができます。
- `ChatLogActivity`を開き、以下の処理を追加しましょう。

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
         }
         toRef.setValue(chatMessage)
             .addOnSuccessListener {
                 Log.d(TAG, "Saved our chat message: ${toRef.key}")
             }
+        
+        val latestFromRef =
+            FirebaseDatabase.getInstance().getReference("latest-messages/$fromId/$toId")
+        latestFromRef.setValue(chatMessage)
+        val latestToRef =
+            FirebaseDatabase.getInstance().getReference("latest-messages/$toId/$fromId")
+        latestToRef.setValue(chatMessage)
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

- 追加できましたらチャット画面から適当にメッセージを送信してみましょう
- 以下のようにデータベースに追加されていましたらOKです。

![session5 1-add-latest-messages](https://user-images.githubusercontent.com/57338033/157463056-55b96420-0f5a-44a6-a645-565b80fe709a.png)

## メッセージ画面に最新のメッセージを表示する

- 先程保存したデータベースを利用してメッセージ画面に最新のメッセージを表示させます。
- `build.gradle(Module)`を開き、`dependencies`の中に以下の行を追加します。
- [coroutines](https://github.com/Kotlin/kotlinx.coroutines)・[lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle?hl=ja)バージョンは2022年3月時点のものです。

```gradle
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.1'
```

- `MessageActivity`に以下の処理を追加します。

```diff
package com.example.handsonchatapp

  import android.view.Menu
  import android.view.MenuItem
  import android.view.View
+ import androidx.lifecycle.lifecycleScope
  import androidx.recyclerview.widget.LinearLayoutManager
  import androidx.recyclerview.widget.RecyclerView
  import com.example.handsonchatapp.databinding.ActivityMessageBinding
  import com.google.firebase.auth.FirebaseAuth
- import com.google.firebase.database.DataSnapshot
- import com.google.firebase.database.DatabaseError
- import com.google.firebase.database.FirebaseDatabase
- import com.google.firebase.database.ValueEventListener
+ import com.google.firebase.database.*
+ import kotlinx.coroutines.Dispatchers
+ import kotlinx.coroutines.withContext
+ import kotlin.coroutines.resume
+ import kotlin.coroutines.resumeWithException
+ import kotlin.coroutines.suspendCoroutine

  val USER_KEY = "USER_KEY"
+
+ suspend fun DatabaseReference.awaitGet(): DataSnapshot {
+     return withContext(Dispatchers.IO) {
+         suspendCoroutine { continuation ->
+             get().addOnSuccessListener {
+                 continuation.resume(it)
+             }.addOnFailureListener {
+                 continuation.resumeWithException(it)
+             }
+         }
+     }
+ }

  class MessageActivity : AppCompatActivity() {
    companion object {
          var currentUser: User? = null
      }

      private val TAG = "Message Activity"

      private lateinit var binding : ActivityMessageBinding

      var recyclerView: RecyclerView? = null

      override fun onCreate(savedInstanceState: Bundle?) {
        ...
      }

      private fun fetchCurrentUser() {
          ...
      }

      private fun listenForLatestMessage() {
          val fromId = FirebaseAuth.getInstance().uid
          val ref = FirebaseDatabase.getInstance().getReference("/users")

+         lifecycleScope.launch {
+             val usersSnapshot = ref.awaitGet()
+             val latestMessageItems = usersSnapshot.children.mapNotNull { userSnapshot ->
+                 val user = userSnapshot.getValue(User::class.java) ?: return@mapNotNull null
+                 if (user.uid == currentUser?.uid) {
+                     return@mapNotNull null
+                 }
+                 val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/${user.uid}")
+                 val latestMessageSnapshot = latestMessageRef.awaitGet()
+                 val latestMessage = latestMessageSnapshot.getValue(ChatMessage::class.java)
+                 val message = latestMessage?.text ?: ""
+                 MessageItem(user, message)
+             }
+             refreshRecyclerView(latestMessageItems)
+         }
-         ref.addListenerForSingleValueEvent(object : ValueEventListener {
-             override fun onDataChange(snapshot: DataSnapshot) {
-                 val messageItems = snapshot.children.mapNotNull { userSnapshot ->
-                     val user = userSnapshot.getValue(User::class.java) ?: return@mapNotNull null
-
-                     if (user.uid == currentUser?.uid) {
-                         return@mapNotNull  null
-                     }
-
-                     MessageItem(user, "")
-                 }
-                 refreshRecyclerView(messageItems)
-           }
-
-           override fun onCancelled(error: DatabaseError) {}
-       })
      }

      private fun refreshRecyclerView(messageItems : List<MessageItem>) {
          ...
      }

      override fun onOptionsItemSelected(item: MenuItem): Boolean {
          ...
      }

      override fun onCreateOptionsMenu(menu: Menu?): Boolean {
          ...
      }
  }
```

- 追加できましたら実行してみましょう。
- 以下のようにメッセージ画面でユーザーアイコン・ユーザー名・最新のメッセージが表示されていましたらOKです。(メッセージがないチャットは表示されません。)

![session5 1-result](https://user-images.githubusercontent.com/57338033/157465500-5e052b2f-b257-471f-b50e-14993606ac76.png)

- 今回はここまでです。
- 次で最後です。最後に手直しをします。

## Diff

[前回との差分](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/compare/session4.3...session5.1)

## Next

[session5.2 手直し](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/tree/session5.2)
