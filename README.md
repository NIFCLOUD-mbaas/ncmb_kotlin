# Kotlin SDK for NIFCLOUD mobile backend

# 概要

ニフクラ mobile backend Kotlin SDKは、 モバイルアプリのバックエンド機能を提供するクラウドサービス ニフクラ mobile backend用のKotlin SDKであり、Android SDK (Javaベース)と違い、Kotlinで開発され、Kotlin専用インタフェースを持ったSDKです。

  - データストア
  - 会員管理
  - プッシュ通知 (開封通知登録は未対応)
  - 位置情報 
  - ファイルストア
  - スクリプト（パラメーター、クエリの設定は未対応）
  - SNS連携(未提供)

といった機能をアプリから利用することが可能です。

このSDKを利用する前に、ニフクラ mobile backendのアカウントを作成する必要があります。 ニフクラ mobile backendのサービスサイトからアカウント登録を行ってください。

# 動作環境
本SDKは、Android 8.x ～ 12.x, Android Studio Arctic Fox | 2020.3.1 Patch 2~ にて動作確認を行っております。
(※2022年4月時点)

## テクニカルサポート窓口対応バージョン

テクニカルサポート窓口では、1年半以内にリリースされたSDKに対してのみサポート対応させていただきます。<br>
定期的なバージョンのアップデートにご協力ください。<br>
※なお、mobile backend にて大規模な改修が行われた際は、1年半以内のSDKであっても対応出来ない場合がございます。<br>
その際は[informationブログ](https://mbaas.nifcloud.com/info/)にてお知らせいたします。予めご了承ください。

- v1.0.0 ～ (※2022年8月時点)

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

# ライセンス

このSDKのライセンスは Apache License Version 2.0 に従います。

# 参考URL集

- [ニフクラ mobile backend](https://mbaas.nifcloud.com/)
- [SDKの詳細な使い方](https://mbaas.nifcloud.com/doc/current/)
- [ユーザーコミュニティ](https://github.com/NIFCLOUD-mbaas/UserCommunity)
