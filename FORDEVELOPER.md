## SDK開発者向け資料

* 本資料は SDK の開発を円滑にするために SDK 開発者に向けて以下の内容をお伝えするために作成いたしました。
  * SDK の内部構造、 REST API の呼び出し方法
  * テストコードのルールと補助ツール
  * p-r など開発フロー について
* 機能追加、修正される際などに参考としていただけたらと思います。

### 1. SDK説明について
#### ディレクトリ・ファイル構成

SDKのメイン構成は以下となります。

```
build/ ビルドフォルダー
src/　　本体のソースコード
   androidTest/　　Android関連のUnitテスト
   main/
      java/com/nifcloud/mbaas/core/
         NCMB.kt	SDKの基本情報を定義し、初期化実施するクラス		
         NCMBObject.kt データストア機能を実施するインターフェース部分のクラス（NCMBBaseを継承）
         NCMBUser.kt, NCMBPush.kt, NCMBInstallation.ktなど   ユーザ管理、プッシュ通知、端末管理など機能を実施するクラス（NCMBOBjectを継承）

         NCMBObjectService.kt　データストア機能を実施する機能部分のクラス（NCMBService継承)
         NCMBUserService.kt, NCMBPushService.kt, NCMBInstallationService ..  ユーザ管理、プッシュ通知、端末管理などの機能を実施する機能部分のクラス（NCMBService継承)
         NCMBAnonymousParameters.kt
         NCMBQuery.kt  クエリを実施するクラス

         NCMBAcl.kt	　ACL管理機能を実施
         NCMBBase.kt　共有で使用する関数部分のクラス（NCMBObject, NCMBUserのベースクラス）		

         NCMBService.kt　NCMBConnectionの通信用メソッド（sendRequest()）を呼び出すクラス（NCMBUserService, NCMBObjectServiceのベースクラス）
         NCMBServiceInterface.kt サービスクラスのインターフェースクラス（NCMBQueryの初期化時に利用）
         NCMBRequest.kt     リクエスト内容を入れておくクラス
         NCMBResponse.kt    レスポンスの成功・失敗を管理するクラス
         NCMBCallback.kt   非同期処理用のコールバックを受けるためクラス
         NCMBResponseBuilder.kt　通信結果からレスポンス成功・失敗を判断しレスポンス用オブジェクトを作成
         NCMBConnection.kt	通信を実施するクラス（内部はOKHTTPを利用）

         NCMBConstant.kt	mBaaS APIを利用するための通信関連の設定値（固定値）
         NCMBSignatureCal.kt　通信ヘッダに設定必要があるsignatureを計算実施するクラス
         NCMBDateFormat.kt　日付のデータをIso8061形式に変換するクラス
         NCMBApplicationController.kt  アプリケーションの状態（state）を代表するクラス
         NCMBException.kt	SDK内部処理、通信関連の例外処理
         NCMBHandler.kt	レスポンス処理を反映し、NCMBCallbackを実施するために、SDK内部の通信後のコールバック
         NCMBUtils.kt　
         NCMBLocalFile.kt ローカルストレージを利用するためのクラス
      res/
      AndroidManifest.xml  
   test/
     assets/
     java/com/nifcloud/mbaas/core/
         helper/
         helpertest/
         NCMBAclTest.kt		
         NCMBDispatcher.kt
         NCMBErrorDispatcher.kt
         NCMBUserTest.kt
         NCMBBaseTest.kt		
         NCMBObjectTest.kt
         NCMBPushTest.kt, NCMBUserTest.ktなど
         NCMBErrorPushTest.kt, NCMBErrorUserTest.ktなど
         NCMBUtilsTest.kt
         NCMBConnectionTest.kt
         NCMBSignatureCalTest.kt
         NCMBDateFormatTest.kt
         NCMBTest.kt
FORDEVELOPER.md		このドキュメント
build.gradle		ビルド関連の設定
gradlew.bat
README.md		　　READMEドキュメント
gradle/			　　
local.properties		
gradle.properties
settings.gradle
build/			
gradlew/
```

#### SDK の内部処理と REST API の関係

SDK では以下の順序で REST API リクエストを行い、レスポンスを取得している。

例: データストアへの新規保存

```
var obj = NCMBObject("TestClass")
obj.put("key", "value")
obj.save()
```

NCMBObject.save() /saveInBackground() データを保存するメソッドのUser interfaceの役割、内部は以下の流れ
で他のメソッドを呼び出します。(NCMBObjectはNCMBBaseを継承し、NCMBBaseではkey, valueを操作するメソッドを定義しています。)

  -> NCMBObjectService.saveObject()
      （NCMBObjectServicetはNCMBServiceを継承しているため、実体は NCMBService.sendRequest() ）
      この中で REST API に送信するリクエスト情報を準備します。
      これを引数とし NCMBService の sendRequestメソッドを用いて REST API を実行する。

      -> NCMBService.sendRequest()
          こちらのメソッドの内部は sendRequest()メソッドの引数を受け、NCMBRequestを生成し
          NCMBConnectionを呼び出し、NCMBConnection.sendRequestを実施します。

        -> NCMBConnection.sendRequest()
          実際サーバ側との通信はこちらで行っています。内部はOkHttpClientを呼び出しし、通信を実施します。
          val response = client.newCall(request).execute()
          サーバ側から返されたレスポンス／エラーより NCMBResponseBuilderを利用し、判定し、NCMBResponseを Success/Failureか反映

      -> NCMBService.sendRequest()
          上記のレスポンスをそのまま返却します
　　　
  -> NCMBObjectService.saveObject()
    REST API リクエストの成功／失敗を処理には、受け取った NCMBResponse がSuccess/Failureか判断し、
    戻り値としてオブジェクトに反映し、エラーがあった場合、エラーの処理（例外処理）を実施します。
   （オブジェクトへの反映に使用した情報は返却しない）

参考: [REST APIリファレンス：オブジェクト登録](https://mbaas.nifcloud.com/doc/current/rest/datastore/objectRegistration.html)

#### 同期処理と非同期処理

SDK 利用者が直接利用するメソッドの中で REST API を利用するものについては同期処理メソッドと非同期処理メソッドの2点を用意する。

##### 同期処理

メソッドは fun メソッド名() の形式とし、 戻り値は REST API リクエストの成功の場合、リクエストが成功した結果が返却されます。
エラーが発生した場合、例外としてthrowsされます。（詳細は各メソッドのレファレンスを参照）

例:
NCMBObject.save() については REST API のレスポンスは 成功した場合、保存されたNCMBObject インスタンスとして返却されます。

```
var obj = NCMBObject("TestClass")
obj.put("key", "value")
obj.save()
```

##### 非同期処理

メソッドは fun メソッド名InBackground(callback:NCMBCallback)  の形式とし、 REST API リクエストの成功／失敗が完了するかタイムアウトした時点で callback を実行する。 リクエスト結果 については、同期処理と同様。

例:

```
val obj = NCMBObject("TestClass")
val callback = NCMBCallback { e, ncmbObj ->
    //通信後の処理
}
obj.put("key", "value")
obj.saveInBackground(callback)
```

### 2. 環境について
#### 動作保証環境

Android 8.x ~ Android 11.x

#### ビルドの環境

- Android Studio 4.2.1
- AndroidStudio組み込まれたJDK Java 1.8
- maven
- gradle 4.1.1
- Kotlin version 1.3.61
- compile sdk version: 29

#### ビルド方法

- clone した ncmb_kotlinをAndroidStudioで開く
- jarファイルを作成する方法
  - [View(menu bar)] -> [ToolWindows] -> [Gradle]を選択し、Gradleタブを開きます。
  - makeJarタスクを実行します。[Tasks] -> [other] -> [makeJar]
  - releaseのdirectoryを開き、jarファイルはその中に作成されます。

### 3. テストについて
#### 利用するテストツール

テストでは以下のライブラリを利用しています。
- JUnit
- robolectric
- okhttp3.mockwebserver

#### テストコード実行方法

* 原則的にすべてのメソッドについてテストコードを作成し、不足している部分についても今後追加を行います。
* バグ修正、新機能開発などの改修時には必ず全パターンのテストを行い、テスト失敗が発生していないことを確認します。

##### 利用するライブラリー

- テストでは以下のライブラリを利用しています。
 - JUnit
 - robolectric
 - okhttp3.mockwebserver

##### テストコード実行方法

- テストケースは、`src/test/java/com/nifcloud/mbaas/core/` フォルダー内に格納されています。
- 実行する際、Android Studioでテストファイルもしくはフォルダーを選択し、右クリックメニューにて、「Run Tests ...」を選択し、行います。ファイルごとに、メソッドごとに、フォルダーごとにどちらでも可能。

##### Kotlin SDKのテスト構成

- モックの設定
  - ymlファイル：`src/test/assets/yaml/mbaas***.yml` リクエストレスポンスのマッピングファイル
  - 例

```
request:
    url: /2013-09-01/classes/TestClass
    method: POST
    body:
        key: value
response:
    status: 201
    file: valid_post_response.json
```

  - リクエストレスポンスファイル（複数）：`src/test/assets/json` (NCMBDispatcher用), `src/test/assets/json_error` (NCMBErrorDispatcher用)
  - 例

```
{"objectId":"7FrmPTBKSNtVjajm","createDate":"2014-06-03T11:28:30.348Z"}
```

- モック制御する部分：`ncmb_kotlin/src/test/java/com/nifcloud/mbaas/core/`にある、以下のファイルとなります。ymlファイルを読み込み、受けているリクエストの情報とymlファイル設定情報を比較し、該当するレスポンスファイルをMockResponse()として返却します。
    - 正常リクエスト用 `NCMBDispatcher.kt`
    - 例外リクエスト用 `NCMBErrorDispatcher.kt`

- その他
  - 非同期処理のcallbackをテストするためのhelperクラス：`ncmb_kotlin/src/test/java/com/nifcloud/mbaas/core/helper/NCMBInBackgroundTestHelper.kt`。コールバック内の結果のチェックを実施するために、スレッドを一時的に止めるように処理します。

##### テストを書く時に必要なこと

- Robolectricを利用するため、テストクラスの前に以下の記載が必要
```
@RunWith(RobolectricTestRunner::class)
```

また、テストメソッドの前に以下のように記載してください。

```
@Test
fun testAbc(){

}
```
- API利用メソッドのテストに関しては、サーバモックを利用するために、以下のようにモックを定義しています。

```
private var mServer: MockWebServer = MockWebServer()
```

- `@Before` 処理で、必要な初期化設定を行います

```kotlin
@Before
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher()
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )

        callbackFlag = false;
    }
```
- 同期処理テスト

```kotlin
    @Test
    @Throws(NCMBException::class)
    fun save_object_with_post_data_save_success() {
        var obj = NCMBObject("TestClass")
        obj.put("key", "value")
        obj = obj.save()
        Assert.assertEquals(obj.getObjectId(), "7FrmPTBKSNtVjajm")
    }
```

- 非同期処理

```kotlin
    @Test
    fun saveInBackground_object_with_post_data_save_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val obj = NCMBObject("TestClass")
        val callback = NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        }
        obj.put("key", "value")
        inBackgroundHelper.start()
        obj.saveInBackground(callback)
        inBackgroundHelper.await()
        print("Success saved: ObjectID " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId() + " | Response data: CreateDate " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate())
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
    }
```


#### レファレンス作成

* 本SDKはdokkaを利用し、ドキュメントレファレンスの生成を行います。
* 生成方法１）Android StudioのGradleタブ＞ncmb_kotlin＞Tasks＞documentation>dokkaHtmlをクリックし、実施します。
  - ※Gradleタブにて、タスクが表示されない場合、タブの右にある「設定アイコン」＞Gradle Settings... をクリックします。表示されるPreferences画面にて、左にExperimentalを選択し、「Do not build Gradle task list during Gradle sync」のチェックを外し、OKを押します。Gradleタブでプロジェクト名を選択肢、右クリックで「Reload Gradle Project」を実施します。
* 生成方法２）コマンドラインで、ncmb_kotlinのプロジェクトパスにて以下のコマンドを実施
```
./gradlew dokkaHtml
```

### 4. 開発について

#### 開発フロー
* バグ修正、新機能開発での開発フローは Github フローを用いる。
* 各開発、改修は develop ブランチ へ向けたプルリクエストにて行う。
* バージョン番号が設定されるリリースにて develop ブランチを再度検証した上で、master ブランチにマージする。

#### コーディング規約
* Kotlinのコーディング規約
https://kotlinlang.org/docs/reference/coding-conventions.html
* ncmb_kotlinの規約
https://kotlinlang.org/docs/reference/coding-conventions.html#naming-rules
#### ドキュメント
以下のようにコメントする際に、フォーマットに従い、コメントを行ってください。
https://kotlinlang.org/docs/reference/kotlin-doc.html
```
/**
 * A group of *members*.
 *
 * This class has no useful logic; it's just a documentation example.
 *
 * @param T the type of a member in this group.
 * @property name the name of this group.
 * @constructor Creates an empty group.
 */
class Group<T>(val name: String) {
    /**
     * Adds a [member] to this group.
     * @return the new size of the group.
     */
    fun add(member: T): Int { ... }
}
```


### 5. 利用中外部ライブラリについて

以下のライブラリを利用しています。それぞれライブラリのライセンスに従います。
- 通信用：
  - okhttp 4.8.1: https://square.github.io/okhttp/
  (ライセンス：https://square.github.io/okhttp/#license)
- テスト用:
  - JUnit 4.12: https://junit.org/junit4/
  - robolectric 4.5.1: http://robolectric.org/getting-started/
  - okhttp3.mockwebserver 4.8.1: https://github.com/square/okhttp/tree/master/mockwebserver

### 6. ライセンスについて

このSDKのライセンスは Apache License Version 2.0 に従います。
