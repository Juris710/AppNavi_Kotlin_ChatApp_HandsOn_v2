# アプリナビ Kotlin HandsOn

## 1.2 ボタン・画像を配置する

ユーザー登録画面にボタンやイメージビューを配置していきます。

## UI制作

まずはボタンを配置します(以下このボタンを`RegisterButton`と呼びます)。

- Paletteから`Button`をドラッグ＆ドロップします。
- 以下のように設定します。
  - Constraint Start : Passwordの左
  - Constraint End : Passwordの右
  - Constraint Top : Passwordの下
  - `layout_width` : `0dp`
  - `layout_height` : `50dp`
  - `mergin Top` : `12dp`
  - `Text`を`登録`に変更
  - `id`を`register_button_register`に変更

下図のようになっていれば大丈夫です。

![session1-2-done-put-register-button](https://user-images.githubusercontent.com/57338033/156706694-7c9829c6-2409-4758-b740-ddb86d141052.png)

次にテキストを配置します。このテキストはすでにアカウントを作っている人をログイン画面に誘導する役割をもたせます。

- Paletteから`TextView`をドラッグ＆ドロップします。
- 以下のように設定します。
  - Constraint Start : RegisterButtonの左
  - Constraint End : RegisterButtonの右
  - Constraint Top : RegisterButtonの下
  - `mergin Top` : `16dp`
  - `Text`を`すでにアカウントをお持ちですか？`に変更
  - `id`を`have_account_text_register`に変更

下図のようになっていれば大丈夫です。

![session1-2-done-already_have_an_account](https://user-images.githubusercontent.com/57338033/156707474-9d2ca47b-342d-4657-a700-cd6eb27bbc1e.png)

続いてボタンと画像を配置します。ボタンが押されると端末の画像を選択する画面に遷移するようにし、画像が選ばれると登録画面に戻り、選ばれた画像を表示させます。<br>
まずは端末に保存されている画像にアクセスするためのボタンを配置します。

- Paletteから`Button`をドラッグ＆ドロップします(以下SelectPhotoButtonと呼びます)。
- 以下のように設定します。
  - Constraint Start : 画面左端
  - Constraint End : 画面右端
  - Constraint Top : 画面上端
  - `layout_width` : `150dp`
  - `layout_height` : `150dp`
  - `mergin Top` : `32dp`
  - `Text`を`写真を選択`に変更
  - `id`を`select_photo_button_register`に変更
- ここまですると入力フォームと先ほど配置したボタンがかぶってしまっています。これを修正します。

![session1-2-Overlap](https://user-images.githubusercontent.com/57338033/156711816-e63dccad-754d-4614-9627-dd059cfe6816.png)

- `id`が`username_edittext_register`である`PlaneText`の設定を以下のように変更します。
  - Constraint Top : SelectPhotoButtonの下
  - `merginTop` : '32dp'

次に画像を配置します。これに選択した画像を表示する役割をもたせます。

- Paletteから`ImageView`をドラッグ＆ドロップします。Avatorの選択画面が出ると思いますが、何でもいいです。
- 以下のように設定します。
  - Constraint Start : SelectPhotoButtonの左
  - Constraint End : SelectPhoroButtonの右
  - Constraint Top : SelectPhotoButtonの上
  - `layout_width` : `150dp`
  - `layout_height` : `150dp`
  - `id`を`circle_view_register`に変更
- 下図のようになっていれば大丈夫です。

![session1-2-result](https://user-images.githubusercontent.com/57338033/156715315-7edaa0b2-2580-4527-918a-32a5f7babc3f.png)

1.2はここまでです。とりあえずユーザー登録画面のUIの配置は完了です。次は色やフォームの形の設定を行っていきます。

## Diff

<details>
  
<summary>前回との差分</summary>

- [diff](https://github.com/syota-kawaguchi/AppNavi_Kotlin_ChatApp_HandsOn/commit/cdfa306e3c6219f4b983fd9d7addf2d60a545926)
  
</details>

## Next

[session1.3 UIの形や色を変更する](https://github.com/syota-kawaguchi/AppNavi_Kotlin_ChatApp_HandsOn/compare/session1.1...session1.2)
