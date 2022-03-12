# アプリナビ Kotlin HandsOn

## 1.3 UIの形や色を変更する

現状のUIと目標のUIを比較します。左：現状、右：目標

![session1-3-compare-init-result](https://user-images.githubusercontent.com/57338033/156861276-a67d6b0c-1919-469e-9796-50135cffb4a0.png)

フォームやボタンの形・全体の色が違いますね、今回は色や形を設定していきます。

## Statusbarの変更

まずはステータスバー・アクションバーの色を変更します。
ちなみにアクションバーは`HandsOnChatAppTest`と表示されている紫のバーで、ステータスバーは充電や時間が記載されている少し濃い紫の部分のことを言います。

![Screenshot_1646887361](https://user-images.githubusercontent.com/57338033/157591947-f1eb3d35-10b4-400d-b6f8-054ba90db77b.png)

- `res/values/colors.xml`を開きましょう
- `colors.xml`では色を定義することができます。自分で定義することで使い回しやすくなります。
- 以下の行を`colors.xml`の`<resources></resources>`内に追加しましょう

```xml
  <color name="base_color_blue">#0A84FF</color>
```

- `base_color_blue`はこの色の名前でありリソースIDです。`name`で指定した文字列が`color`要素の名前とリソースIDの役割を持ちます。

![session1-3-edit-color-xml](https://user-images.githubusercontent.com/57338033/156861294-caa3091b-5833-4747-8547-8423ead13812.png)

- 次に`res/values/themes/themes.xml`を開きましょう。
- `@color/purple_500`を`@color/base_color_blue`に変更しましょう。
- `?attr/colorPrimryVariant`を`@color/base_color_blue`に変更しましょう。
- 以下に編集後の`theme.xml`を載せておきます

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.HandsOnChatApp" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/base_color_blue</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">@color/base_color_blue</item>
        <!-- Customize your theme here. -->
    </style>
</resources>
```

編集後の画面は以下の通りです。

![session1-3-changed_primary_color](https://user-images.githubusercontent.com/57338033/156863695-bdc3c365-e783-4626-8e4a-26c46e485ff7.png)

## 入力Formの編集

- まずは`res/drawable`を右クリックし、`New`→`Drawable Resource File`を選択します。
- `edittext_frame.xml`という名前でxmlファイルを作成します。
- 以下の内容を作成したファイルに記入しましょう。

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <stroke android:width="2dp" android:color="@color/base_color_blue" />
            <corners android:radius="25dp" />
            <solid android:color="@color/white" />
        </shape>
    </item>
</selector>
```

- 続いて`activity_register.xml`を開きましょう
- ユーザー名の入力Formを選択し、`Attributes`の`background`に`@drawable/edittext_frame`を入力します。
- この操作をメールアドレス・パスワードにも行います。
- これでFormの見た目を変えることができました。しかしヒントが左によりすぎて少しかなり見にくいです。そこでPaddingを設定します。
- `Attributes`からユーザー名、メールアドレス、パスワードそれぞれの`padding left`を`16dp`にします。
- 以下にここまでの画面を表示します

![session1-3-apply-background-from](https://user-images.githubusercontent.com/57338033/156864257-6ae8f3ae-8392-4049-a3cb-af679fcd38c7.png)

## 課題

- 同様の手順で`register_button`、`select_photo_button`という名前で`res/drawable`直下にxmlファイルを作成し、`register_button.xml`は`RegisterButton`に、`select_photo_button.xml`は`SelectPhotoButton`の`background`に適用しましょう。
- 以下に`register_button.xml`、`select_photo_button.xml`に記載する内容を載せます。

- `register_button.xml`

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <selector xmlns:android="http://schemas.android.com/apk/res/android">
      <item>
          <shape android:shape="rectangle">
              <corners android:radius="25dp" />
          </shape>
      </item>
  </selector>  
  ```

- `select_photo_button.xml`

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <selector xmlns:android="http://schemas.android.com/apk/res/android">
      <item>
          <shape android:shape="rectangle">
              <corners android:radius="75dp" />
          </shape>
      </item>
  </selector> 
  ```

- `selectPhotoButton`をレイアウトから選択しようとしても前に画像があるため、できないです。そこで`Palette`の下にある`Component Tree`から選択するとうまくいきます。
- 以下のような画面になっていればOKです。`RegisterButton`と`SelectPhotoButton`が丸くなっていれば正解です。

![session1-3-apply-background-select-photo-button](https://user-images.githubusercontent.com/57338033/156864775-6e423c98-09cd-4a82-a2f3-dacb0a4afd8b.png)

# 画像を丸くする

- 現状画像のみが丸くなっておりません。
- 先程のようにxmlファイルで画像を丸くするようにしても画像自体は丸くなりますが、端末に保存されている画像を表示したとき四角い画像になってしまいます。
- そこで[CircleImageView](https://github.com/hdodenhof/CircleImageView)というライブラリを使います。
- まずは`build.gradle(Module)`を開きます。
- 以下の緑のハイライトを`dependencies`内に追加します。

```diff
dependencies {
    ...
+    implementation 'de.hdodenhof:circleimageview:3.1.0'
}

```

- 追加できましたら`Sync Now`を押しましょう。
- 続いて`activity_register`を開きましょう
- ViewModeをCodeモードに変更します。
- `ImageView`を`de.hdodenhof.circleimageview.CircleImageView`に書き換えましょう

```diff
- <ImageView
+ <de.hdodenhof.circleimageview.CircleImageView
      android:id="@+id/circle_view_register"
      略
```

- これで画像を丸くする対応は完了です！

- 現状のUIは以下のとおりです。

![session1-3-result](https://user-images.githubusercontent.com/57338033/156867540-34b3b50a-e29d-4523-9908-45d7da2e2259.png)

次からコードを記述していきます！

## Diff

<details>
  
<summary>前回との差分</summary>
  
  　DiffではActionBarが紫に指定されておりませんが、気にしないでください<br>
  　[diff](https://github.com/syota-kawaguchi/AppNavi_Kotlin_ChatApp_HandsOn/compare/session1.2...session1.3)
  
</details>

## Next

[session1.4 処理を記述する](https://github.com/syota-kawaguchi/AppNavi_Kotlin_ChatApp_HandsOn/tree/session1.4)
