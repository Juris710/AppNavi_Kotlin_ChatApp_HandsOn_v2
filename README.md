# アプリナビ Kotlin HandsOn

## 3.2 メッセージ画面の機能・レイアウト作成

今回は`Status Bar`にサインアウトボタンを追加し、画面にリストを表示します。

## サインアウトボタンを表示する。

- 以下のように`Status Bar`にサインアウトボタンを追加します。

![session3 2-add-menu](https://user-images.githubusercontent.com/57338033/157197353-36539e9f-77b4-4764-8f18-2c272cfc762d.png)

- これを実装するにあたってMenuを用います。
- `app/res`直下に`menu`フォルダーを作成しましょう。`app/res`の上にマウスをあわせた状態で右クリックし、`New`→`Directory`でフォルダーを追加できます。
- 作成した`menu`フォルダーに`nav_menu.xml`を作成しましょう。`menu`フォルダーにマウスを合わせた状態で右クリックし、`New`→`Menu Resource File`で追加できます。
- 追加できましたら作成したファイルを開きましょう。ViewモードをCodeに切り替え、下記のように内容を書き換えます。

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/menu_sign_out"
        android:title="Sign Out"
        app:showAsAction="ifRoom" />
</menu>
```

- 書き換えが完了しましたらViewモードをDesingに切り替えましょう。下記のように`Status bar`の右に`SIGN OUT`という文字列が表示されていればOKです。

![session3 2-add-nav-menu](https://user-images.githubusercontent.com/57338033/156949557-75f8a938-cd36-47b9-9f03-db4b3210868c.png)

- 続いて`SIGN OUT`がタップされたときの処理を追加します。
- `MessageActivity`を開き、以下の緑色のハイライトを追加しましょう。

```diff
  package com.example.handsonchatapp
+ import android.content.Intent
  import androidx.appcompat.app.AppCompatActivity
  import android.os.Bundle
+ import android.view.Menu
+ import android.view.MenuItem
+ import com.google.firebase.auth.FirebaseAuth

  class MessageActivity : AppCompatActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_message)
      }
+
+     override fun onOptionsItemSelected(item: MenuItem): Boolean {
+         if (item.itemId == R.id.menu_sign_out){
+             FirebaseAuth.getInstance().signOut()
+             val intent = Intent(this, RegisterActivity::class.java)
+             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
+             startActivity(intent)
+         }
+
+         return super.onOptionsItemSelected(item)
+     }
+
+     override fun onCreateOptionsMenu(menu: Menu?): Boolean {
+         menuInflater.inflate(R.menu.nav_menu, menu)
+         return super.onCreateOptionsMenu(menu)
+     }
  }
```

- ここまでできましたら一度実行し、`SIGN OUT`をタップしましょう。ログアウトし、登録画面に遷移しましたらOKです。

## RecyclerViewを配置する

つづいてリスト表示を実装します。以下の画面のように`MessageActivity`にUser一覧を表示します。

![session3 2-user-list-view-sample](https://user-images.githubusercontent.com/57338033/156951341-a5f1afe0-7bfc-422c-a3be-1febdfef19c7.png)

- まずは`activity_message`を編集します。
- `Palette`から`RecyclerView`をドラッグ＆ドロップし、以下のように設定します。Constraintの設定で`parent`と表記していますが、画面端に合わせることと同義です。
  - `layout_widht` : `0dp`
  - `layout_height` : `0dp`
  - Constraint start : `parent`
  - Constraint end : `parent`
  - Constraint top : `parent`
  - Constraint bottom : `parent`
  - `id` : `recyclerView_message`

![session3 2-add-recycler-view](https://user-images.githubusercontent.com/57338033/156955835-d3b480b4-2557-433f-8f18-3c0be3d34396.png)

## アイテムの実装

- つづいてリストに表示されるアイテムを実装していきます。
- 最初にレイアウトを作ります。`layout`フォルダーに`message_row`という名前でxmlファイルを作成しましょう。
- 追加できましたらParentの高さを変更します。Component Treeを見ると１つだけViewがあると思います。このViewの高さを`100dp`に設定します。

![session3 2-change-parent-height](https://user-images.githubusercontent.com/57338033/156959006-10f1a90c-cb9e-40c4-a2da-dbfd5d2577fe.png)

- ImageViewを追加し、以下のように設定します。(Avatorは何でもいいです)
- 以下のよう`imageView`の大きさを設定します。
  - `layout_width` : `64dp`
  - `layout_height` : `64dp`
- 下図のようにconstraintを設定します。

![session3 2-set-imageview-constraint](https://user-images.githubusercontent.com/57338033/156960545-9398b07f-3b0c-4f41-9b69-475585906055.png)

- `margin`を以下のようにに設定します。
  - `Start` : `16dp`
- `id`を`userimage_imageview_message`に設定します。

- 続いて`textview`２つ追加します。以降一方を`username`、もう一方を`latestmessage`と呼びます。
- 下図のように`username`のconstraintを設定します。少し見づらいですが、`username`のボトムのconstraintは`latestmessage`のトップにドラッグ＆ドロップします。

![session3 2-set-username-constraint](https://user-images.githubusercontent.com/57338033/156965245-69d3075b-5dcf-4818-a72c-0665005422f8.png)

- 下図のように`latestmessage`のconstraintを設定します。

![session3 2-set-latestmessage-constraint](https://user-images.githubusercontent.com/57338033/156969719-a85fa5a8-5bd4-4187-84ac-f24943625ee4.png)

- [チェーン](https://developer.android.com/training/constraint-layout?hl=ja#constrain-chain)を設定します。
- チェーンはリンクさせたViewを垂直または水平方向に制約をつけて配置してくれます。
- `username`と`latestmessage`を選択した状態で右クリックし、`Chains`→`Create Vertical Chain`を選択します。

![image](https://user-images.githubusercontent.com/57338033/156970752-dc5ee368-7602-438a-b273-5fd04c660ce5.png)

- その後２つ選択されている状態で再度右クリックして`Chains`→`Vertical Chain Style`→`packed`を選択しましょう。以下のような画面になっていればOKです。

![image](https://user-images.githubusercontent.com/57338033/156970981-2705c7f0-0ea6-4508-bed8-a6e9aca04576.png)

- `username`を以下の設定にします。
  - `id` : `username_textview_message`
  - `text` : `username`
  - `margin`
    - `Start` : `16dp`
    - `Bottom` : `8dp`
  - `textStyle` : `bold`
  - `text size`: `16sp`
- `latest message`を以下の設定にします。
  - `id` : `latestmessage_textview_message`
  - `text` : `latest message`
  - `layout_width` : `0dp`
  - `margin`
    - `Start` : `16dp`
    - `End` : `8dp`
  - `text size` : `16sp`
- ViewモードをCodeに変更し、`ImageView`を`de.hdodenhof.circleimageview.CircleImageView`に書き換えます。

- 以下のような画面になっていればOKです。

![session3 2-message-row-result](https://user-images.githubusercontent.com/57338033/156975469-b19a551e-569a-4154-abb2-deb4f4c950ff.png)

## Adaptorの実装

- 左のバーで右クリック → `New` → `Kotlin Class/File`を選択
- `MessageAdapter`という名前でファイルを作成
- 以下の内容を追加しましょう

```kotlin
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
```

## List表示する

- message画面でリスト表示を実装します。
- `MessageActivity`を開きます。
- 以下の内容を追加しましょう

```diff
  package com.example.handsonchatapp

  import android.content.Intent
  import androidx.appcompat.app.AppCompatActivity
  import android.os.Bundle
  import android.view.Menu
  import android.view.MenuItem
+ import android.view.View
+ import androidx.recyclerview.widget.LinearLayoutManager
+ import androidx.recyclerview.widget.RecyclerView
+ import com.example.handsonchatapp.databinding.ActivityMessageBinding
  import com.google.firebase.auth.FirebaseAuth

  class MessageActivity : AppCompatActivity() {

+     private val TAG = "Message Activity"
+
+     private lateinit var binding : ActivityMessageBinding
+
+     var recyclerView: RecyclerView? = null
+
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
-         setContentView(R.layout.activity_message)
+
+         binding = ActivityMessageBinding.inflate(layoutInflater)
+         val view = binding.root
+         setContentView(view)
+
+         recyclerView = binding.recyclerViewMessage
+
+         val messageItems = mutableListOf<MessageItem>()
+         messageItems.add(MessageItem("username", "Hello world", ""))
+         messageItems.add(MessageItem("username", "Hello world", ""))
+         messageItems.add(MessageItem("username", "Hello world", ""))
+         messageItems.add(MessageItem("username", "Hello world", ""))
+
+         recyclerView?.apply {
+             setHasFixedSize(true)
+             layoutManager = LinearLayoutManager(context)
+             adapter = MessageAdapter(
+                 messageItems,
+                 object : MessageAdapter.ListListener {
+                     override fun onClickItem(tappedView: View, messageItem: MessageItem) {
+                     }
+                 }
+             )
+         }
+
      }

      override fun onOptionsItemSelected(item: MenuItem): Boolean {
          if (item.itemId == R.id.menu_sign_out){
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

- 追加できましたら実行してみましょう。
- 下図のようにユーザー名にusername、メッセージにHello worldと表記されたアイテムが４つ表示されていればOKです。

![session3 2-user-list-view-result](https://user-images.githubusercontent.com/57338033/156992572-a441847d-0a2b-4dc9-a806-252fbf0e28e0.png)

## Diff

[前回との差分](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/compare/session3.1...session3.2)

## Next

[Session3.3 データベースへ登録・MessageActivityのリストへ反映](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/tree/session3.3)
