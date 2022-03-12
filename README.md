# アプリナビ Kotlin HandsOn

## 1.1 入力フォームを作成する

これからユーザー登録画面を作成していきます。

## ファイル名の変更

今後わかりやすいようにするため、まずはファイル名を変更します。

- `MainActivity`上にカーソルをのせ、右クリックし`Refactor`→`Rename`を選択します。
- `MainActivity`を`RegisterActivity`に変更し`Refactor`をクリックしましょう
- `res/layout/activity_main.xml`も同様に`activity_main`から`activity_register`に変更しましょう

![session1-1-rename-mainactivity](https://user-images.githubusercontent.com/57338033/156504019-db0913b0-2174-4c5d-adb2-9742651fc47e.png)

## UI制作画面の説明

では、先程変更した`activity_register.xml`を開きましょう

![session1 1-explanation-layout-view](https://user-images.githubusercontent.com/57338033/157585232-8e1ac4e4-f5fe-479b-b9e3-14f7f014b652.png)

簡単にAndroidStudioの画面の説明をしておきます

- 左端のサイドバー

    プロジェクトのファイル・フォルダー構成

- Palette

    レイアウトにドラッグできるビュー及びビューグループが格納されている

- View mode
  - Codeモード：UIをコードベースで編集するモード
  - Desingモード：UIを視覚的に編集するモード
  - Splitモード：CodeモードとDesignモードを両方表示するモード
  - Attributes

      選択したUIパーツの詳細を設定する。

## UI制作

これからユーザー名・メールアドレス・パスワードの入力フォームを配置していきます。

- 作業前に画面中央に「Hello World」と書かれたテキストがあると思います。これを選択した状態でデリートキーを押して消してしまいましょう。
  - PaletteからPlainTextを画面にドラッグ＆ドロップします。

     ![session1-1-put-plaintext](https://user-images.githubusercontent.com/57338033/156542377-ddd91435-1dce-4d6c-af42-188766603172.png)

  - まずPlaneテキストの大きさを設定します。
  - 大きさは先程配置したPlainTextを選択した状態で`Attributes`の`layout_width`, `layout_height`から変更できます。
  - 以下のように設定します。
    - `lauout_width` : `0dp`
    - `layout_height`：`50dp`

      ![session1-1-set-width-height](https://user-images.githubusercontent.com/57338033/156543586-dab474a8-104e-4bb0-94d3-b8f6b85a0c7b.png)

- 次に`Constraint`を設定します。
- 配置したPlainTextを見ると上下左右に◯があります。左の◯は左の画面端に、右の◯は右の画面端に、上の◯は上の画面端までドラッグ＆ドロップしましょう(下の◯は設定しない)
- constraintでは左側のことを`Start`、右側のことを`End`、上側のことを`Top`、下側のことを`Bottom`と呼びます。
- なので、ConstraintStartであればViewの左側の制約のことと認識してください。

  ![session1 1-set-constraint](https://user-images.githubusercontent.com/57338033/157585936-6737d0be-09c1-428e-9b12-1616a2060441.png)

- Constraintを設定するとPlainTextが横いっぱいに広がることが確認できると思います。

  ![session1-1-Set-Constraint](https://user-images.githubusercontent.com/57338033/156557363-ed873e4f-1d22-4d1a-aa3d-18fb255b1891.png)

画面いっぱいに広がって見ずらいので余白を設定します。余白はmarginで設定します。

- `Attributes`の`Constraint Widget`から以下のようにmarginを設定します。
  - `margin left`：`32dp`
  - `margin right`：`32dp`
  - `margin top`：`32dp`

Hintを設定します。Hintは何も入力されていないときに表示されるメッセージです。このフォームにはどういったものが入力されるべきであるかを示します。

- `Attributes`の`Text`に「Name」と入力されていると思います。これを消します。
- `Attributes`の`Hint`に`ユーザー名`と入力しましょう
すると画面のPlainTextに「ユーザー名」と表示されると思います。

idを設定しましょう。これは後にコードとUIを関連付けるために使います。

- idを`username_edittext_register`とします。

ここまでで一旦ユーザー名を入力するフォームの設定は終了です。下図のようになっていれば問題ないです。

![session1-1-done-set-username-form](https://user-images.githubusercontent.com/57338033/156560816-44de6c78-c313-416c-8c4a-32466da7c7f6.png)

次にメールアドレスのフォーム、パスワードのフォームを設定していきます。

- `Palette`から`Email`、`Password`を画面にドラッグ＆ドロップ
- `Email`を以下のように設定しましょう
  - `layout_width`：`0dp`
  - `layout_height`：`50dp`
  - constrain Start → 画面左端
  - constrain End → 画面右端
  - constrain Top → Username(先程のPlainText)の下
  - `margin left`：`32dp`
  - `margin right`：`32dp`
  - `margin top`：`16dp`
  - Textを消し、Hintに`メールアドレス`と入力
  - idを`email_edittext_register`に変更
- 最後に`Password`を以下のように設定しましょう
  - `layout_width`：`0dp`
  - `layout_height`：`50dp`
  - constrain Start → 画面左端
  - constrain End → 画面右端
  - constrain Top → Emailの下
  - `margin left`：`32dp`
  - `margin right`：`32dp`
  - `margin top`：`16dp`
  - Textを消し、Hintに`パスワード`と入力
  - idを`password_edittext_register`に変更

以下のような画面になっていればOKです。

![session1-1-result](https://user-images.githubusercontent.com/57338033/156562495-dbf88cce-1250-4cbc-8fd1-0210ae8f232c.png)

## Diff

<details>
<summary>前回との差分</summary>
<a href="https://github.com/syota-kawaguchi/AppNavi_Kotlin_ChatApp_HandsOn/commit/891ba469f1756f41009004bede0bc72db1283a5c">diff</a>
</details>

## Next

[session1.2 ボタン・画像を配置する](https://github.com/syota-kawaguchi/AppNavi_Kotlin_ChatApp_HandsOn/tree/session1.2)
