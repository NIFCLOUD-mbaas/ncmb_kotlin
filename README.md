# Kotlin SDK for NIFCLOUD mobile backend

# 概要

ニフクラ mobile backend Kotlin SDKは、 モバイルアプリのバックエンド機能を提供するクラウドサービス ニフクラ mobile backend用のKotlin SDKであり、Android SDK (Javaベース)と違い、Kotlinで開発され、Kotlin専用インタフェースを持ったSDKです。

  - データストア(デベロッパープレビュー版提供)
  - 会員管理(デベロッパープレビュー版提供)
  - プッシュ通知 (未提供)
  - ファイルストア(未提供)
  - SNS連携(未提供)

といった機能をアプリから利用することが可能です。

このSDKを利用する前に、ニフクラ mobile backendのアカウントを作成する必要があります。 ニフクラ mobile backendのサービスサイトからアカウント登録を行ってください。

# 動作環境
本SDKは、Android 8.x ～ 11.x, Android Studio 4.2~ にて動作確認を行っております。
(※2021年6月時点)

# テクニカルサポート窓口対応

テクニカルサポート窓口対応はデベロッパープレビュー版のため実施していません。
不具合や問題が見つかった場合はissueにてご報告のほどお願いいたします。

# ライセンス

このSDKのライセンスは Apache License Version 2.0 に従います。

# インストール
Android Studioでプロジェクトを開き、以下の手順でSDKをインストールしてください。
1. app/libsフォルダにNCMB.jarをコピーします
2. app/build.gradleファイルに以下を追加します
```
dependencies {
    implementation 'com.squareup.okhttp3:okhttp:4.8.1'
    implementation 'com.google.code.gson:gson:2.3.1'
    api files('libs/NCMB.jar')

    //同期処理を使用する場合はこちらを追加していただく必要があります
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9'
}
```

# クイックスタート

* AndroidManifest.xmlの編集
&lt;application&gt;タグの直前に以下のpermissionを追加します。
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

* 初期化
ActivityのonCreateメソッド内に以下を記載します。

```kotlin
NCMB.initialize(this.getApplicationContext(),"YOUR_APPLICATION_KEY","YOUR_CLIENT_KEY");
```

* 利用する機能に合わせて使用するライブラリのimport

```kotlin
import com.nifcloud.mbaas.core.NCMB //全機能必須
import com.nifcloud.mbaas.core.NCMBCallback //非同期処理を行う場合
import com.nifcloud.mbaas.core.NCMBException //例外処理を行う場合
import com.nifcloud.mbaas.core.NCMBObject //データストアを利用する場合
import com.nifcloud.mbaas.core.NCMBUser //会員管理を利用する場合
```

* オブジェクトの保存

NCMB.initializeの下に以下を記載します。

```kotlin
    // TestClassのNCMBObjectを作成
    val obj = NCMBObject("TestClass")
    // オブジェクトに値を設定
    obj.put("message", "Hello, NCMB!")
    // データストアへの登録を実施
    obj.saveInBackground(NCMBCallback { e, ncmbObj ->
        if (e != null) {
            //保存に失敗した場合の処理
            Log.d("error","保存に失敗しました : " + e.message)
        } else {
            //保存に成功した場合の処理
            val result = ncmbObj as NCMBObject
            Log.d("success","保存に成功しました ObjectID :" + result.getObjectId())
        }
    })
```

* オブジェクトの保存(非同期処理を利用し、通信完了結果をUI上で反映する場合)

以下の実装例は保存されたオブジェクトIDをToastを利用し、UI上で結果を表示する例です。

```kotlin
    // TestClassのNCMBObjectを作成
    val obj = NCMBObject("TestClass")
    // オブジェクトに値を設定
    obj.put("message", "Hello, NCMB!")
    // データストアへの登録を実施
    obj.saveInBackground(NCMBCallback { e, ncmbObj ->
        if (e != null) {
            //保存に失敗した場合の処理
            Log.d("error","保存に失敗しました : " + e.message)
            backgroundToastShow(NCMB.getCurrentContext(), "NCMB Error:" + e.message);
        } else {
            //保存に成功した場合の処理
            val result = ncmbObj as NCMBObject
            Log.d("success","保存に成功しました ObjectID :" + result.getObjectId())
            backgroundToastShow(NCMB.getCurrentContext(), "Save successfull! with ID:" + result.getObjectId());
        }
    })
    　
    fun backgroundToastShow(context: Context?, msg: String?) {
        if (context != null && msg != null) {
            Handler(Looper.getMainLooper()).post(object : Runnable {
                override fun run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
```

### データストア

#### オブジェクトをデータストアに保存する

```kotlin
    // TestClassのNCMBObjectを作成
    val obj = NCMBObject("TestClass")
    // オブジェクトに値を設定
    obj.put("fieldA", "Hello, NCMB!")
    obj.put("fieldB", 25)
    // データストアへの登録を実施
    obj.saveInBackground(NCMBCallback { e, ncmbObj ->
        if (e != null) {
            //保存に失敗した場合の処理
            Log.d("error","保存に失敗しました : " + e.message)
        } else {
            //保存に成功した場合の処理
            val result = ncmbObj as NCMBObject
            Log.d("success","保存に成功しました ObjectID :" + result.getObjectId())
        }
    })

```

#### オブジェクトの取得

```kotlin
    // TestClassへのNCMBObjectを設定
    val obj = NCMBObject("TestClass")
    // objectIdプロパティを設定
    obj.setObjectId("Mz6xym6wNi63lxb8")
    // データストアの取得を実施
    obj.fetchInBackground(NCMBCallback { e, ncmbObj ->
        if (e != null) {
            //検索に失敗した場合の処理
            Log.d("failure","取得に失敗しました : " + e.message)
        }
        else {
            //検索に成功した場合の処理
            val result = ncmbObj as NCMBObject
            Log.d("success","取得に成功しました")
            Log.d("success","fieldB value " + result.get("fieldB"))
        }
    })
```

#### オブジェクトの更新

保存済み（または、objectIdを持っている）のオブジェクトに新しい値をセットして `saveInBackground` メソッドを実行することでデータストアの値が更新されます。

```kotlin
    // TestClassへのNCMBObjectを設定
    val obj = NCMBObject("TestClass")
    // objectIdプロパティを設定
    obj.setObjectId("Mz6xym6wNi63lxb8")    
    // オブジェクトに値を設定
    obj.put("fieldA", "Hello, NCMB!!")
    obj.put("fieldB", 30)
    // データストアへの更新を実施
    obj.saveInBackground(NCMBCallback { e, ncmbObj ->
        if (e != null) {
            //更新に失敗した場合の処理
            Log.d("error","更新に失敗しました : " + e.message)
        } else {
            //更新に成功した場合の処理
            val result = ncmbObj as NCMBObject
            Log.d("success","更新に成功しました ObjectID : " + result.getObjectId())
        }
    }) 

```

#### オブジェクトの削除

```kotlin
    // TestClassへのNCMBObjectを設定
    var obj = NCMBObject("TestClass")
    // objectIdプロパティを設定
    obj.setObjectId("Mz6xym6wNi63lxb8")
    // データストアから削除
    obj.deleteInBackground(NCMBCallback { e, ncmbObj ->
            if (e != null) {
                //保存に失敗した場合の処理
                Log.d("failure", "削除に失敗しました ： " + e.message)
            }
            else {
                Log.d("success", "削除に成功しました")
            }
    })
```

### 会員管理

* 使用するライブラリのimport

```kotlin
import com.nifcloud.mbaas.core.NCMBUser
```

#### ユーザーの新規登録

```kotlin
    //　Userインスタンスの生成
    var user = NCMBUser()
    // ユーザー名・パスワードを設定
    user.userName = "takanokun"
    user.password = "openGoma"
    // ユーザーの新規登録
    try {
        user.signUp()
        // 新規登録に成功した場合の処理
        Log.d("success","新規登録に成功しました")
    }
    catch(e:NCMBException){
        // 新規登録に失敗した場合の処理
        Log.d("failure","新規登録に失敗しました ： " + e.message)
    }
```

#### ログイン

ユーザー名、パスワードでのログイン

```kotlin
    //　Userインスタンスの生成
    var user = NCMBUser()
    //ユーザー名・パスワードを設定
    user.userName = "takanokun" /* ユーザー名 */
    user.password = "openGoma" /* パスワード */
    try{
        // ログイン
        user.login(user.userName,user.password)
        // ログインに成功した場合の処理
        Log.d("success","ログインに成功しました")
    }
    catch(e:NCMBException){
        // ログインに失敗した場合の処理
        Log.d("failure","ログインに失敗しました ： " + e.message)
    }
    // ログイン状況の確認
    val currentUser: NCMBUser = NCMBUser().getCurrentUser()
    if (currentUser.getObjectId() != null) {
        Log.d("Info","ログインしています ユーザー: " + currentUser.userName)
    } else {
        Log.d("Info","ログインしていません")
    }
```

#### ログアウト

```kotlin
    //　Userインスタンスの生成
    var user = NCMBUser()
    // ログアウト
    try{
        // ログアウト
        user.logout()
        // ログアウトに成功した場合の処理
        Log.d("success","ログアウトに成功しました")
    }
    catch(e:NCMBException){
        // 新規登録に失敗した場合の処理
        Log.d("failure","ログアウトに失敗しました ： " + e.message)
    }
    // ログイン状況の確認
    if (NCMBUser().getCurrentUser().getObjectId() != null) {
        Log.d("Info","ログインしています ユーザー: " + NCMBUser().getCurrentUser().userName)
    } else {
        Log.d("Info","ログインしていません")
    }
```

# 参考URL集

- [ニフクラ mobile backend](https://mbaas.nifcloud.com/)
- [ユーザーコミュニティ](https://github.com/NIFCLOUD-mbaas/UserCommunity)
