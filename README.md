# アプリナビ Kotlin HandsOn

## 4.3 チャット機能の実装

今回はチャット機能を実装します。メッセージを入力して送信するとデータベースに保存され、画面にも反映されるようにします。

## MessageActivityとデータのやりとり

- 現在`ChatLogActivity`のアクションバーにはプロジェクトの名前が表示されています。
- このアクションバーもユーザー名を表示するようにします。
- そのためにMessageActivityからタップしたユーザー情報を取得します。以下は`MessageActivity`から切り抜いたコードです。

```kotlin
    private fun refreshRecyclerView(messageItems: List<MessageItem>) {
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = MessageAdapter(
                messageItems,
                object : AdapterUtil.ListListener<MessageItem> {
                    override fun onClickItem(tappedView: View, messageItem: MessageItem) {
                        val intent = Intent(tappedView.context, ChatLogActivity::class.java)
                        startActivity(intent)
                    }
                }
            )
        }
    }
```

- `onClickItem`の処理で`MessageActivity`から`ChatLogActivity`に遷移します。方針はここからユーザー情報を取得します。
- `MessageAdapter`の`MessageItem`クラスを以下のように編集します。

```diff
- class MessageItem(val username: String, val message: String, val profileImageUrl: String) {
-     constructor() : this("", "", "")
+ class MessageItem(val user: User, val message: String) {
+     constructor() : this(User("", "", ""), "")
  }
```

- `MessageItem`を編集するとエラーになるので`MessageViewHolderClass`クラスを以下のように編集します。

```diff
  class MessageViewHolder(private val itemBinding: MessageRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
      fun bind(item: MessageItem, listener: AdapterUtil.ListListener<MessageItem>) {
-         itemBinding.usernameTextviewMessage.text = item.username
+         itemBinding.usernameTextviewMessage.text = item.user.username
          itemBinding.latestmessageTextviewMessage.text = item.message
          val userImage = itemBinding.userimageImageviewMessage
-         Picasso.get().load(item.profileImageUrl).into(userImage)
+         Picasso.get().load(item.user.profileImageUrl).into(userImage)
          itemBinding.root.setOnClickListener {
              listener.onClickItem(it, item)
          }
      }
  }
```

- 続いて`MessageActivity`からユーザー情報を渡します。ユーザー情報を渡すために[putExtra](https://developer.android.com/reference/android/content/Intent#putExtra(java.lang.String,%20android.os.Bundle))を使います。
- ただし、Userクラスをそのまま渡すことができないので`User`クラスをParcelizeします。
- `build.gradle(Module)`の`id`に以下の行を追加します。

```gradle
  id 'kotlin-parcelize'
```

- `User`クラスを以下のように編集します。

```diff
  package com.example.handsonchatapp

+ import android.os.Parcelable
+ import kotlinx.android.parcel.Parcelize

+ @Parcelize
- class User(val uid: String, val username: String, val profileImageUrl: String){
+ class User(val uid: String, val username: String, val profileImageUrl: String) : Parcelable{
      constructor() : this("", "", "")
  }
```

- `MessageActivity`を開き、以下のように編集します。

```diff
  package com.example.handsonchatapp
  
  import ...略
+
+ const val USER_KEY = "USER_KEY"

  class MessageActivity : AppCompatActivity() {

      companion object {
          var currentUser: User? = null
      }

      private val TAG = "Message Activity"

      private lateinit var binding : ActivityMessageBinding

      var recyclerView: RecyclerView? = null

      override fun onCreate(savedInstanceState: Bundle?) {
          略
      }

      private fun fetchCurrentUser() {
        略
      }

      private fun listenForLatestMessage() {
          val fromId = FirebaseAuth.getInstance().uid
          val ref = FirebaseDatabase.getInstance().getReference("/users")

          ref.addListenerForSingleValueEvent(object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  val messageItems = snapshot.children.mapNotNull { userSnapshot ->
                      val user = userSnapshot.getValue(User::class.java) ?: return@mapNotNull null

                      if (user.uid == currentUser?.uid) {
                          return@mapNotNull  null
                      }

-                     MessageItem(user.username, "Hello world", user.profileImageUrl)
+                     MessageItem(user, "")
                  }
                  refreshRecyclerView(messageItems)
              }

              override fun onCancelled(error: DatabaseError) {}
          })
      }

      private fun refreshRecyclerView(messageItems : List<MessageItem>) {
          recyclerView?.apply {
              setHasFixedSize(true)
              layoutManager = LinearLayoutManager(context)
              adapter = MessageAdapter(
                  messageItems,
                  object : AdapterUtil.ListListener<MessageItem> {
                      override fun onClickItem(tappedView: View, messageItem: MessageItem) {
                          val intent = Intent(tappedView.context, ChatLogActivity::class.java)
+                         intent.putExtra(USER_KEY, messageItem.user)
                          startActivity(intent)
                      }
                  }
              )
          }
      }

      override fun onOptionsItemSelected(item: MenuItem): Boolean {
          略
      }

      override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        略
      }
  }
```

- アクションバーにユーザー名を表示します。ChatLogActivityに以下の処理を追加しましょう。

```diff
...略

class ChatLogActivity : AppCompatActivity() {

    private val TAG = "ChatLogActivity"

    private lateinit var binding : ActivityChatLogBinding

    private var recyclerView : RecyclerView? = null
+
+   var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        binding = ActivityChatLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

+       toUser = intent.getParcelableExtra(USER_KEY)
+       supportActionBar?.title = toUser?.username

        recyclerView = binding.recyclerviewChatlog
        
        略
```

- 追加できましたら実行してみましょう。以下の画面の用になっていればOKです。

![session4 3-messsage-to-chat-log](https://user-images.githubusercontent.com/50994088/158007881-8dc779d0-77bd-4f34-8be2-8a9a596eca87.png)

## 送信機能の追加

- `ChatLogActivity`を開きます。最初にダミーメッセージを削除し、`recyclerView`の処理をメソッド化します。
- 以下のように変更しましょう。

```diff
package com.example.handsonchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handsonchatapp.databinding.ActivityChatLogBinding

class ChatLogActivity : AppCompatActivity() {

    private val TAG = "ChatLogActivity"

    private lateinit var binding : ActivityChatLogBinding

    private var recyclerView : RecyclerView? = null
    
    var toUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        binding = ActivityChatLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        
        toUser = intent.getParcelableExtra<User>(USER_KEY)
        supportActionBar?.title = toUser?.username

        recyclerView = binding.recyclerviewChatlog
-
-       val chatLogs = mutableListOf<ChatLogItem>()
-       chatLogs.add(ChatLogItem("username", "Hello world", "", true))
-       chatLogs.add(ChatLogItem("username", "Hello world", "", false))
-       chatLogs.add(ChatLogItem("username", "Hello world", "", true))
-       chatLogs.add(ChatLogItem("username", "Hello world", "", false))
-
-       recyclerView?.apply {
-           setHasFixedSize(true)
-           layoutManager = LinearLayoutManager(context)
-           adapter = ChatLogAdapter(
-               chatLogs,
-               object : AdapterUtil.ListListener<ChatLogItem> {
-                   override fun onClickItem(tappedView: View, messageItem: ChatLogItem) {}
-               }
-           )
-       }
    }
    
+   private fun refreshRecyclerView(list : List<ChatLogItem>) {
+       recyclerView?.apply {
+           setHasFixedSize(true)
+           layoutManager = LinearLayoutManager(context)
+           adapter = ChatLogAdapter(
+               list,
+               object : AdapterUtil.ListListener<ChatLogItem> {
+                   override fun onClickItem(tappedView: View, messageItem: ChatLogItem) {}
+               }
+           )
+       }
+   }
}
```

- 次に`ChatMessage`を定義します。
- 左のバーで右クリック → `new` → `Kotlin Class/File`を選択し、`ChatMessage`という名前でファイルを作成します。
- 以下のように編集しましょう。

```kotlin
package com.example.handsonchatapp

class ChatMessage(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timestamp: Long
) {
    constructor() : this("", "", "", "", -1)
}
```

- 送信ボタンが押されるとメッセージがデータベースに保存されるようにします。以下の処理を`ChatLogActivity`に追加しましょう

```diff
package com.example.handsonchatapp

import ...

class ChatLogActivity : AppCompatActivity() {

    private val TAG = "ChatLogActivity"

    private lateinit var binding : ActivityChatLogBinding

    private var recyclerView : RecyclerView? = null
    
    var toUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        binding = ActivityChatLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toUser = intent.getParcelableExtra<User>(USER_KEY)
        supportActionBar?.title = toUser?.username

        recyclerView = binding.recyclerviewChatlog

+       binding.sendButtonChatlog.setOnClickListener {
+           performSendMessage()
+       }
    }

    private fun refreshRecyclerView(list : List<ChatLogItem>) {
      ...
    }
+      private fun performSendMessage() {
+          val user = intent.getParcelableExtra<User>(USER_KEY)
+          val text = binding.edittextChatlog.text.toString()
+          val fromId = FirebaseAuth.getInstance().uid
+          val toId = user?.uid
+
+          if (fromId == null || toId == null || text == "") return
+
+          val ref = FirebaseDatabase.getInstance().getReference("/message").push()
+
+          val chatMessage =
+              ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
+          ref.setValue(chatMessage)
+              .addOnSuccessListener {
+                  Log.d(TAG, "Saved our chat message: ${ref.key}")
+              }
+      }
}
```

- 追加できましたら実行してみましょう。
- 適当なメッセージを入力して送信ボタンを押します。以下のように`FirebaseのRealtime Database`に`message`が追加されていましたらOKです。(以下は３回送信してしましました)

![session4 3-saved-message](https://user-images.githubusercontent.com/57338033/157277871-abdd938e-f5e2-48dd-a288-12ac2b2d9322.png)

## データベースから取得してチャットメッセージを表示

- データベースに保存されているデータを取得してチャットメッセージを表示します。
- `ChatLogActivity`に以下の処理を追加します。

```diff
package com.example.handsonchatapp

import ...

class ChatLogActivity : AppCompatActivity() {

    private val TAG = "ChatLogActivity"

    private lateinit var binding : ActivityChatLogBinding

    private var recyclerView : RecyclerView? = null

    var toUser : User? = null

+   val chatLogs = mutableListOf<ChatLogItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        binding = ActivityChatLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toUser = intent.getParcelableExtra<User>(USER_KEY)
        supportActionBar?.title = toUser?.username

        recyclerView = binding.recyclerviewChatlog

+       listenForMessage()

        binding.sendButtonChatlog.setOnClickListener {
            performSendMessage()
        }
    }

    private fun refreshRecyclerView(list : List<ChatLogItem>) {
      ...
    }

    private fun performSendMessage() {
      ...
    }
+
+     private fun listenForMessage() {
+         val ref = FirebaseDatabase.getInstance().getReference("/message")
+
+         ref.addChildEventListener(object : ChildEventListener {
+             override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
+                 val chatMessage = snapshot.getValue(ChatMessage::class.java)
+                 val myId = FirebaseAuth.getInstance().uid
+                 val user =
+                     (if (chatMessage?.fromId == myId) MessageActivity.currentUser else toUser)
+                         ?: return
+
+                 if (chatMessage != null) {
+                     Log.d(TAG, chatMessage.text)
+                     chatLogs.add(
+                         ChatLogItem(
+                             user.username,
+                             chatMessage.text,
+                             user.profileImageUrl,
+                             chatMessage.fromId == myId
+                         )
+                     )
+                 }
+                 refreshRecyclerView(chatLogs)
+             }
+
+             override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
+
+             override fun onCancelled(error: DatabaseError) {}
+
+             override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
+
+             override fun onChildRemoved(snapshot: DataSnapshot) {}
+         })
+     }
}
```

- 編集できましたら実行してみましょう。`ChatLogActivity`を開き、先程送信したメッセージが表示されていればOKです。

![session4 3-get-chat-log-from-database](https://user-images.githubusercontent.com/57338033/157285822-19a43caa-c403-4bbb-8a41-64fa41ef30c2.png)

- データベースにメッセージを送信し、データベースからメッセージを取得して画面に表示することはできました。
- しかし、ユーザーごとにメッセージが分けられていません。最後にユーザーごとにメッセージの保存・取得を行うよう実装します。
- FirebaseのRealtime Databseを開き、メッセージを削除します。"message"にマウスカーソルを合わせると表示される×をクリックし、表示されたダイアログ右下の"削除"を押すと削除されます。

![image](https://user-images.githubusercontent.com/57338033/157286444-61fe0052-c75d-4bf1-9fe4-4b666bcfb2be.png)

- `ChatLogActivity`を開き以下のように編集します。

```diff
    ...略
    
    private fun performSendMessage() {
        val user = intent.getParcelableExtra<User>(USER_KEY)
        val text = binding.edittextChatlog.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
 
        if (fromId == null || toId == null || text == "") return

-       val ref = FirebaseDatabase.getInstance().getReference("/message").push()
+       val fromRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
+       val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

-       val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
+       val chatMessage = ChatMessage(fromRef.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
-       ref.setValue(chatMessage)
+       fromRef.setValue(chatMessage)
            .addOnSuccessListener {
-               Log.d(TAG, "Saved our chat message: ${ref.key}")
+               Log.d(TAG, "Saved our chat message: ${fromRef.key}")
            }
+       toRef.setValue(chatMessage)
+           .addOnSuccessListener {
+               Log.d(TAG, "Saved our chat message: ${toRef.key}")
+           }
    }

    private fun listenForMessage() {
+       val fromId = FirebaseAuth.getInstance().uid
+       val toId = toUser?.uid
-       val ref = FirebaseDatabase.getInstance().getReference("/message")
+       val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                val myId = FirebaseAuth.getInstance().uid
                val user = (if (chatMessage?.fromId == myId) MessageActivity.currentUser else toUser) ?: return

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    chatLogs.add(ChatLogItem(user.username, chatMessage.text, user.profileImageUrl, chatMessage.fromId == myId))
                }
                refreshRecyclerView(chatLogs)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }
```

- 編集できましたら実行してみましょう。
- チャット画面に行き、適当にメッセージを送信してみましょう
- 下図に画面に反映されましたらOKです。

![session4 3-send-message](https://user-images.githubusercontent.com/57338033/157289013-fb6265c5-f691-4999-82ed-7427a852b5e0.png)

- データベースも確認してみましょう

![session4 3-database-user-messages](https://user-images.githubusercontent.com/57338033/157289260-6f54dc88-7ef8-4db6-832a-87b888c80723.png)

このようになっていればOKです。

## Diff

[前回との差分](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/compare/session4.2...session4.3)

## Next

[session5.1 メッセージ画面に最新のメッセージを表示する](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/tree/session5.1)
