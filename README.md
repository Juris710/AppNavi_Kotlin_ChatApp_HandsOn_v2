# アプリナビ Kotlin HandsOn

## 3.1 メッセージ画面の実装

今回から以下のようなユーザー一覧画面を作成していきます。

![session3 3-show-userImage-message-activitypng](https://user-images.githubusercontent.com/57338033/157044187-ba992d06-872c-4878-b1e8-3e2e23feb013.png)

まずは画面を追加し、この画面にたどり着けるようにします。

## Activityを追加する

- `EmptyActivity`を追加します。 ファイル名は`MessageActivity`とします。

![session3 1-add-latest-message-activity](https://user-images.githubusercontent.com/57338033/156921369-cd81892e-f23c-4ad6-a7c9-d84e97e5819f.png)

- すると`MessageActivity.kt`と`activity_message.xml`が追加されます。

![image](https://user-images.githubusercontent.com/57338033/156922811-af8d27da-245d-4b34-aa36-643e9da40b28.png)

## 登録・ログイン完了時MessageActivityに遷移させる。

- ユーザー登録・ログイン完了時にMessageActivityに画面遷移するようにします。
- `RegisterActivity`を開き、以下の緑色のハイライトを追加します。

```diff
  private fun performRegister() {
        val email = binding.emailEdittextRegister.text.toString();
        val password = binding.passwordEdittextRegister.text.toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email or password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Email is: ${email}")
        Log.d(TAG, "password is: ${password}")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (it.isCanceled) {
                    Log.d(TAG, "Canceled")
                }
                if (!it.isSuccessful) {
                    Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                //else if successful
                Log.d(TAG, "Successfully created user with uid: ${it.result.user?.uid}")
+               val intent = Intent(this, MessageActivity::class.java)
+               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
+               startActivity(intent)
            }
            .addOnFailureListener{
                //emailのformatが違ったら実行
                Log.d(TAG, "failed to create user message ${it.message}")
                Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
            }
    }
```

- 追加できましたら実行してみましょう。ユーザー名・メールアドレス・パスワードを入力して登録ボタンを押したあと画面遷移したらOKです。
- **すでに登録しているメールアドレスを使用すると登録に失敗します。**
- つづいて`LoginActivity`にも同様に画面遷移処理を追加します。以下の緑色のハイライトを追加しましょう

```diff
...略
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginButtonLogin.setOnClickListener {
            val email = binding.emailEdittextLogin.text.toString()
            val password = binding.passwordEdittextLogin.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter text in email or password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "email : ${email}, password:${password}")

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
+                   val intent = Intent(this, MessageActivity::class.java)
+                   intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
+                   startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to Login", Toast.LENGTH_SHORT).show()
                }
        }
...略
```

- こちらはすでにユーザーとして登録されている情報を入力し、ログインボタンを押して画面が遷移されましたら問題ないです。

これで画面の追加とこの画面にたどり着くことができました。
次から画面のレイアウトを作ります。

## Diff

[前回との差分](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/compare/session2.2...session3.1)

## Next

[Session3.2メッセージ画面の作成](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/tree/session3.2)
