# アプリナビ Kotlin HandsOn

## 2.1 Firebaseを導入する

今回からFirebaseを導入していきます。プロジェクトのFirebaseを導入する作業はFirebaseが丁寧に解説してくれているので、基本そちらを見ていただければ問題有りません。

ただ`build.gradle`への追加方法が説明とは多少異なるのでその際はこちらを参照していただければと思います。

ここではFirebaseのバージョンも含めて貼っておりますが、バージョンは今後変わるのでその際はそちらに合わせていただくようお願いいたします。

- [Firebase](https://firebase.google.com/)にアクセス
- 「使ってみる」を選択

![session2 1-try-to-use-firebase](https://user-images.githubusercontent.com/57338033/156891066-72fda812-4efd-495f-aa8e-ac80256bdd6f.png)

- 「プロジェクトを追加」を選択。

![session2 1-add-new-project](https://user-images.githubusercontent.com/57338033/156891177-4818bd27-ebc6-44ef-a6bd-bf231b793655.png)

- プロジェクト名を入力して「続行」を選択。(プロジェクト名は自分がわかりやすければ何でもいいです。ここでは`HandsOnChatApp`としています)

![session2 1-project-name](https://user-images.githubusercontent.com/57338033/156891326-40c65afa-afb1-4d6a-b11c-0ca032e80284.png)

- Googleアナリティクスは「続行」でいいです。(特に使いませんが、オフにする理由も無いのでオンのままでいいと思います。)
- Googleアナリティクスアカウントは`Default Account for Firebase`で大丈夫です。「プロジェクト作成」をクリックします。少々時間がかかります(１分くらい？)。

![image](https://user-images.githubusercontent.com/57338033/156891413-4d1b78b2-d4be-419c-a3f4-7bb9b989fb86.png)

- 以下のような画面になればOKです。
![session2 1-done-create-firebase-project](https://user-images.githubusercontent.com/57338033/156891488-dea12f15-2762-42c3-b6b5-91a1739a4475.png)

- ではAndroidプロジェクトにFirebaseを追加していきます。下図で赤い丸で囲まれたAndroidマークをクリックします。

![session2 1-done-create-firebase-project](https://user-images.githubusercontent.com/57338033/156891559-71fe643d-a6c1-4534-a4c6-0991e8af3331.png)

- アプリを登録します。
  - `Android パッケージ名`には`build.gradle(Module)`の`applicationId`をコピペしましょう
  - 残り２つは特に記入しなくて大丈夫です。
- 設定ファイルをダウンロードします。
- `google-services.jsonをダウンロード`をクリックし、ダウンロードします。

![session2 1-download-google-services-json](https://user-images.githubusercontent.com/57338033/156891800-6c1c5287-7771-4db3-84ce-36c10f9d64f2.png)

- ダウンロードできましたらダウンロードしたファイルをAndroidプロジェクトの`app`フォルダー直下に置きます。

![session2 1-add-json-project](https://user-images.githubusercontent.com/57338033/156892096-c9fe7091-6f9e-491e-91cb-50a0ebd741f4.png)

- `app`直下に配置できたか現状確認しづらいのでファイルの表示方法を変更します。AndroidStudioの画面左上の`Android`をクリックし、`Project Files`に変更します。表示方法を変更して`app`直下に`google-services.json`が表示されていれば問題ないです。

![session2 1-change-view-mode](https://user-images.githubusercontent.com/57338033/156892388-60ef2786-ccf2-41aa-bfd4-c7bacc6d6071.png)

- 次にgradleファイルに追加します。`build.gradle(Project)`を開いてください。先程まで編集してきた`build.gradle`とは逆のファイルです。
- 以下のように編集します。`'com.google~'`の部分は各自の情報で設定してください。2022年3月時点では以下のとおりです。

```diff
+ buildscript {
+     dependencies {
+         classpath 'com.google.gms:google-services:4.3.10'
+     }
+ }

  plugins {
      id 'com.android.application' version '7.1.1' apply false
      id 'com.android.library' version '7.1.1' apply false
      id 'org.jetbrains.kotlin.android' version '1.5.30' apply false
  }

  task clean(type: Delete) {
      delete rootProject.buildDir
  }
```

- `build.gradle(Module)`の方も編集します。
- 以下のように編集します。

```diff
  plugins {
      id 'com.android.application'
      id 'org.jetbrains.kotlin.android'
+     id 'com.google.gms.google-services'
  }

  android {
      compileSdk 31

      defaultConfig {
          applicationId "com.example.handsonchatapp"
          minSdk 21
          targetSdk 31
          versionCode 1
          versionName "1.0"

          testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
      }

      buildTypes {
          release {
              minifyEnabled false
              proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
          }
      }
      compileOptions {
          sourceCompatibility JavaVersion.VERSION_1_8
          targetCompatibility JavaVersion.VERSION_1_8
      }
      kotlinOptions {
          jvmTarget = '1.8'
      }
      viewBinding {
          enabled = true
      }
  }

  dependencies {

      implementation 'androidx.core:core-ktx:1.7.0'
      implementation 'androidx.appcompat:appcompat:1.4.1'
      implementation 'com.google.android.material:material:1.5.0'
      implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
      testImplementation 'junit:junit:4.13.2'
      androidTestImplementation 'androidx.test.ext:junit:1.1.3'
      androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'

+     implementation platform('com.google.firebase:firebase-bom:29.1.0')
+     implementation 'com.google.firebase:firebase-analytics-ktx'
 
      implementation 'de.hdodenhof:circleimageview:3.1.0'
  }
```

これでプロジェクトにFirebaseを追加する作業は終わりです。今後必要に応じてFirebaseの依存関係を追加します。

## Diff

[前回との差分](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/compare/session1.4...session2.1)

## Next

[Session2.2 アプリに認証機能とログイン機能を追加する](https://github.com/Juris710/AppNavi_Kotlin_ChatApp_HandsOn_v2/tree/session2.2)
