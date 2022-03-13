# アプリナビ Kotlin HandsOn

## 4.1 チャット画面の作成・画面遷移

session4からチャット画面・機能を作成していきます。

## ChatLogActivityを追加する。

- 右のメニューバーで`右クリック` → `New` → `Activity` → `Empty Activity`を選択し、`ChatLogActivity`という名前で作成

![session4 1-add-chat-log-activity](https://user-images.githubusercontent.com/57338033/157045995-e4332aa9-15f8-4217-a94b-5234cf3cfb90.png)

- `ChatLogActivity.kt`と`activity_chat_log.xml`が生成されて入れればOKです。

![image](https://user-images.githubusercontent.com/57338033/157046211-a40ff272-26b6-45a2-b112-50c59c01a595.png)

## MessageActivityと行き来する

- `MessageActivity`を開き、以下を追加します。

```diff
...略
      private fun refreshRecyclerView(messageItems: List<MessageItem>) {
          recyclerView?.apply {
              setHasFixedSize(true)
              layoutManager = LinearLayoutManager(context)
              adapter = MessageAdapter(
                  messageItems,
                  object : MessageAdapter.ListListener {
                      override fun onClickItem(tappedView: View, messageItem: MessageItem) {
+                         val intent = Intent(tappedView.context, ChatLogActivity::class.java)
+                         startActivity(intent)
                      }
                  }
              )
          }
      }
...略
```

- `AndroidManifest.xml`を開き、以下の内容を追加します。`ChatLogActivity`のactivity属性は`ChatLogActivity`を生成した際に自動で入力されています。

```diff
 <?xml version="1.0" encoding="utf-8"?>
 <manifest xmlns:android="http://schemas.android.com/apk/res/android"
     package="com.example.handsonchatapp">
 
     <application
         android:allowBackup="true"
         android:icon="@mipmap/ic_launcher"
         android:label="@string/app_name"
         android:roundIcon="@mipmap/ic_launcher_round"
         android:supportsRtl="true"
         android:theme="@style/Theme.HandsOnChatApp">
         <activity
             android:name=".ChatLogActivity"
-            android:exported="false" />
+            android:exported="false">
+            <meta-data
+                android:name="android.support.PARENT_ACTIVITY"
+                android:value=".MessageActivity" />
+        </activity>
         <activity
             android:name=".MessageActivity"
             android:exported="false" />
         <activity
             android:name=".LoginActivity"
             android:exported="false">
             <meta-data
                 android:name="android.support.PARENT_ACTIVITY"
                 android:value=".RegisterActivity" />
         </activity>
         <activity
             android:name=".RegisterActivity"
             android:exported="true">
             <intent-filter>
                 <action android:name="android.intent.action.MAIN" />
 
                 <category android:name="android.intent.category.LAUNCHER" />
             </intent-filter>
         </activity>
     </application>
 
 </manifest>
```

- ここまで入力できましたら実行してみましょう。
- `MessageActivity`でアイテムをタップすると`ChatLogActivity`に遷移し、`ChatLogActivity`の`Status bar`に表示されている戻るボタンを押すと`MessageActivity`に戻ってくることが確認できればOKです。

## レイアウトの作成

- `ChatLogActivity`のレイアウトを作成します。最初にレイアウトで使うxmlファイルを作成します。
- `res/drawable`の上にマウスを合わせて`右クリック`し、`New` → `Drawable Resource File`を選択し、`edittext_chatlog_frame`という名前でxmlファイルを作成します。
- 作成したxmlファイルに以下の内容を追加します。

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <stroke android:width="2dp" android:color="@android:color/darker_gray" />
            <corners android:radius="10dp" />
            <solid android:color="@color/white" />
        </shape>
    </item>
</selector>
```

- 続いて`ChatLogActivity`のレイアウトを作成します。
- `res/layout/activity_chat_log.xml`を開きます。
- `Palette`から`Button`を画面に配置し、以下のように設定します。
  - `id` : `send_button_chatlog`
  - `layout_height` : `50dp`
  - `margin`
    - `left` : `8dp`
    - `bottom` : `8dp`
  - `text` : `送信`
  - constraint
    - bottom : 画面下端
    - right : 画面右端
- `Palette`から`PlainText`を画面に配置し、以下のように設定します。
  - `id` : `edittext_chatlog`
  - `layout_width` : `0dp`
  - `layout_height` : `50dp`
  - `margin`
    - `right` : `8dp`
    - `left` : `8dp`
    - `bottom` : `8dp`
  - constraint
    - left : 画面左端
    - right : `send_button_chatlog`の左
    - bottom : 画面下端
  - `padding left` : `8dp`
  - `hint` : `Enter Message`
  - `text`を削除
  - `background`:`@drawable/edittext_chatlog_frame`
- `Palette`から`RecyclerView`を配置し、以下のように設定します。
  - `id` : `recyclerview_chatlog`
  - `layout_width` : `0dp`
  - `layout_height` : `0dp`
  - `marginBottom` : `8dp`
  - constraint
    - left : 画面左端
    - right : 画面右端
    - Top : 画面上端
    - bottom : edittext_chatlogの上
- 以下のような画面になっていればOKです。

![session4 1-chat-log-layout](https://user-images.githubusercontent.com/57338033/157075063-94a70017-ac70-4d99-af78-0599298bcfd8.png)

## Diff

[前回との差分](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/compare/session3.3...session4.1)

## Next

[session4.2 チャットのレイアウト作成](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/tree/session4.2)
