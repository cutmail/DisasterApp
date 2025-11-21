# CLAUDE.md - DisasterApp向けAIアシスタントガイド

> **最終更新日:** 2025-11-20
> **バージョン:** 1.9.3 (versionCode: 22)

このドキュメントは、DisasterAppコードベースで作業するAIアシスタント向けの包括的なガイダンスを提供します。プロジェクト構造、規約、ワークフロー、ベストプラクティスを網羅しています。

---

## 目次

1. [プロジェクト概要](#プロジェクト概要)
2. [コードベース構造](#コードベース構造)
3. [主要技術](#主要技術)
4. [開発環境セットアップ](#開発環境セットアップ)
5. [コーディング規約](#コーディング規約)
6. [テスト戦略](#テスト戦略)
7. [Firebase連携](#firebase連携)
8. [ビルドとデプロイ](#ビルドとデプロイ)
9. [Gitワークフロー](#gitワークフロー)
10. [よくある作業](#よくある作業)
11. [トラブルシューティング](#トラブルシューティング)

---

## プロジェクト概要

**DisasterApp（地震・災害情報）**は、リアルタイムで地震・災害情報をユーザーに提供する日本語Androidアプリケーションです。

### 主要機能
- Firebase Firestoreからのリアルタイム災害・地震情報フィード
- 効率的な読み込みを備えたページング対応リスト表示（20件/ページ）
- 外部リンク付きWebViewベースの詳細ビュー
- 日本語ローカライゼーション対応のマテリアルデザインUI
- Firebase Analytics追跡
- アプリ内レビュープロンプト（AppRateライブラリ）
- メールによるユーザー問い合わせサポート

### 対象ユーザー
タイムリーな災害情報を求める日本のユーザー。

### ビジネスモデル
Google Playストアで提供される無料アプリ。

---

## コードベース構造

```
DisasterApp/
├── .ci/
│   └── google-services.json              # CI用モックFirebase設定
├── .github/
│   └── workflows/
│       └── build-and-test.yml            # GitHub Actions CI/CD
├── app/
│   ├── build.gradle                       # アプリビルド設定
│   ├── proguard-rules.pro                 # ProGuardルール
│   └── src/
│       ├── androidTest/
│       │   └── java/me/cutmail/disasterapp/
│       │       └── ApplicationTest.java   # インストルメンテーションテスト
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/me/cutmail/disasterapp/
│           │   ├── DisasterApplication.java        # Applicationクラス
│           │   ├── activity/
│           │   │   ├── MainActivity.java           # メインリスト画面
│           │   │   ├── EntryDetailActivity.java    # 詳細WebView
│           │   │   └── AboutActivity.java          # About画面
│           │   └── model/
│           │       └── Entry.java                  # データモデル
│           └── res/
│               ├── layout/                         # XMLレイアウト
│               ├── menu/                           # メニュー定義
│               ├── values/                         # 文字列、スタイル、寸法
│               └── xml/
│                   └── global_tracker.xml          # Analytics設定
├── fastlane/
│   ├── Appfile                            # Fastlane設定
│   ├── Fastfile                           # デプロイレーン
│   └── metadata/android/ja-JP/            # Playストアメタデータ
├── build.gradle                           # ルートビルド設定
├── settings.gradle                        # プロジェクト設定
├── gradle.properties                      # Gradleプロパティ
├── Gemfile                                # Ruby依存関係（Fastlane）
└── README.md                              # プロジェクトドキュメント
```

### Javaパッケージ構造

**パッケージ:** `me.cutmail.disasterapp`

- **ルート:** `DisasterApplication.java`（Applicationクラス）
- **activity/:** すべてのActivityクラス
  - `MainActivity.java`（199行）- ページングリスト付きメイン画面
  - `EntryDetailActivity.java`（110行）- WebView詳細画面
  - `AboutActivity.java`（75行）- About/ライセンス画面
- **model/:** データモデル
  - `Entry.java`（15行）- Firestoreエントリーモデル（title、url）

**Javaコード総行数:** 6ファイルで約443行

---

## 主要技術

### 言語とビルドシステム
- **言語:** Java 8
- **ビルドツール:** Gradle 6.5
- **Android Gradleプラグイン:** 4.1.1
- **ビルドツール:** 30.0.3

### Android設定
- **compileSdkVersion:** 30
- **minSdkVersion:** 26（Android 8.0 Oreo）
- **targetSdkVersion:** 30（Android 11）
- **AndroidX:** 有効

### コアライブラリ

#### Firebaseスタック
```gradle
firebase-ui-firestore: 7.1.1          // Firestore用ページングアダプター
firebase-firestore: 22.0.1            // Cloud Firestoreデータベース
firebase-crashlytics: 17.3.0          // クラッシュレポート
firebase-analytics: 18.0.0            // Analytics追跡
firebase-database: 19.6.0             // Realtime Database
firebase-core: 18.0.0                 // Firebase Core SDK
```

#### AndroidXライブラリ
```gradle
appcompat: 1.2.0                      // 後方互換性
paging-runtime: 2.1.2                 // ページングライブラリ
```

#### UIとユーティリティ
```gradle
butterknife: 10.2.3                   // ビューバインディング（@BindView）
timber: 4.7.1                         // ロギングフレームワーク
android-rate: 1.0.1                   // アプリ内レビュープロンプト
play-services-oss-licenses: 17.0.0    // OSSライセンス画面
```

### アーキテクチャパターン
**従来型Android（MVVM/MVP/MVIなし）**
- ActivityがFirebaseと直接やり取り
- ViewModel、Repository、UseCaseレイヤーなし
- Dependency Injectionフレームワークなし

---

## 開発環境セットアップ

### 前提条件
1. **Android Studio Arctic Fox以降**
2. **JDK 8以上**
3. **API 30対応Android SDK**
4. **Firebaseプロジェクトのセットアップ**（ローカル開発に必須）

### 初期セットアップ手順

#### 1. リポジトリのクローン
```bash
git clone https://github.com/cutmail/DisasterApp.git
cd DisasterApp
```

#### 2. Firebaseの設定（重要）
**このステップなしではアプリはビルドできません。**

1. [Firebaseコンソール](https://console.firebase.google.com/)にアクセス
2. DisasterAppプロジェクトを選択（テスト用に新規作成も可）
3. プロジェクト設定から`google-services.json`をダウンロード
4. `app/google-services.json`に配置

**注意:** このファイルはセキュリティのためgitignoreされています。CIビルドでは`.ci/google-services.json`のモック版を使用します。

#### 3. プロジェクトのビルド
```bash
./gradlew build
```

#### 4. テストの実行
```bash
./gradlew test           # ユニットテスト（現在は最小限）
./gradlew connectedTest  # インストルメンテーションテスト
```

#### 5. デバッグAPKのインストール
```bash
./gradlew installDebug
```

または、Android Studioから直接実行。

### Fastlaneセットアップ（オプション）
自動デプロイ用:

```bash
bundle install
bundle exec fastlane test    # テスト実行
bundle exec fastlane beta    # Crashlytics Betaへデプロイ
bundle exec fastlane deploy  # Playストアへデプロイ
```

**注意:** `fastlane/service-account.json`（gitignore）と署名認証情報が必要です。

---

## コーディング規約

### Javaスタイル

#### 1. ButterKnifeによるビューバインディング
**パターン:**
```java
@BindView(R.id.view_id) ViewType mViewName;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_name);
    ButterKnife.bind(this);
}
```

**例:**
- `MainActivity.java:39-43` - RecyclerViewとProgressBarのバインディング
- `AboutActivity.java:21-22` - バージョンTextViewのバインディング
- `EntryDetailActivity.java:29` - WebViewのバインディング

**クリックリスナー:**
```java
@OnClick(R.id.button_id)
void onButtonClick() {
    // クリック処理
}
```

#### 2. Timberによるロギング
**セットアップ:** `DisasterApplication.setupTimber()`（22行目）

**使用法:**
```java
Timber.d("Debug message");
Timber.i("Info message");
Timber.w("Warning message");
Timber.e(exception, "Error message");
Timber.e(exception);  // 例外のみをログ
```

**本番環境での動作:**
- デバッグビルド: Logcatにログ出力（全レベル）
- リリースビルド: WARN以上をFirebase Crashlyticsにログ出力

**参照:** `DisasterApplication.java:30-43`のCrashReportingTree実装

#### 3. Intentファクトリーパターン
**パターン:**
```java
public static Intent createIntent(Context context, String param1, String param2) {
    Intent intent = new Intent(context, TargetActivity.class);
    intent.putExtra(EXTRA_PARAM1, param1);
    intent.putExtra(EXTRA_PARAM2, param2);
    return intent;
}
```

**例:** `EntryDetailActivity.createIntent()`（32-37行目）

#### 4. Firebaseページングパターン
**参照:** `MainActivity.java:91-102`

```java
Query query = FirebaseFirestore.getInstance()
    .collection("collection_name")
    .orderBy("field_name");

PagedList.Config config = new PagedList.Config.Builder()
    .setEnablePlaceholders(false)
    .setPrefetchDistance(10)
    .setPageSize(20)
    .build();

FirestorePagingOptions<Model> options = new FirestorePagingOptions.Builder<Model>()
    .setLifecycleOwner(this)
    .setQuery(query, config, Model.class)
    .build();
```

#### 5. エラーハンドリング
**パターン:**
```java
try {
    // 失敗する可能性のある操作
} catch (Exception e) {
    Timber.e(e);
    // ユーザー向けエラーメッセージなし（現在のパターン）
}
```

**注意:** このアプリはエラーをサイレント処理します。UX向上のためユーザーフィードバック追加を検討してください。

### リソース命名規則

#### レイアウトファイル
- `activity_*.xml` - Activityレイアウト
- `*_list_item.xml` - RecyclerViewアイテムレイアウト
- `fragment_*.xml` - Fragmentレイアウト（追加された場合）

#### ビューID
- アンダースコア付き小文字: `@id/paging_recycler`、`@id/paging_loading`
- ビュータイプを示す説明的な名前

#### 文字列
- すべて日本語（主要言語）
- キーは英語: `action_inquiry`、`action_review`、`title_activity_about`

#### 寸法
- 標準マージン: `activity_horizontal_margin`（16dp）、`activity_vertical_margin`（16dp）
- `values-w820dp/`でタブレット用オーバーライド（64dp水平マージン）

---

## テスト戦略

### 現状
**テストカバレッジは最小限**で大幅な改善が必要です。

### 既存テスト
**ファイル:** `app/src/androidTest/java/me/cutmail/disasterapp/ApplicationTest.java`
- 非推奨の`ApplicationTestCase<Application>`を使用
- わずか13行、実際のテストメソッドなし
- 近代化が必要

### 不足しているテストインフラ
- `app/src/test/`ディレクトリなし（ユニットテストなし）
- 最新のテストフレームワークなし（JUnit4、Espresso、Mockito、Robolectric）
- テストカバレッジ測定なし

### 推奨テストアプローチ

#### 1. ユニットテスト（追加予定）
**ディレクトリ:** `app/src/test/java/me/cutmail/disasterapp/`

**追加すべきフレームワーク:**
```gradle
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito:mockito-core:4.0.0'
testImplementation 'androidx.arch.core:core-testing:2.1.0'
```

**テスト対象:**
- `Entry.java` - モデル検証
- ユーティリティメソッド
- Intent作成メソッド

#### 2. インストルメンテーションテスト（近代化予定）
**ディレクトリ:** `app/src/androidTest/java/me/cutmail/disasterapp/`

**追加すべきフレームワーク:**
```gradle
androidTestImplementation 'androidx.test.ext:junit:1.1.3'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
androidTestImplementation 'androidx.test:runner:1.4.0'
androidTestImplementation 'androidx.test:rules:1.4.0'
```

**テスト対象:**
- MainActivityのUIインタラクション
- ナビゲーションフロー
- Firebase Firestoreクエリ（エミュレータ使用）

#### 3. テストの実行

**ユニットテスト:**
```bash
./gradlew test --stacktrace
```

**インストルメンテーションテスト:**
```bash
./gradlew connectedTest --stacktrace
```

**CI/CD統合:**
`main`、`master`、`develop`へのすべてのPRとプッシュでGitHub Actionsが自動実行されます。

---

## Firebase連携

### 使用サービス

#### 1. Firestore（プライマリデータベース）
**コレクション:** `entries`

**スキーマ:**
```javascript
{
  title: String,    // 災害エントリータイトル
  url: String       // 外部リンクURL
}
```

**クエリ:** `orderBy("title")`（`MainActivity.java:91`参照）

**アクセスパターン:**
- アプリからは読み取り専用
- ページング: 20件/ページ、10件プリフェッチ距離
- ライフサイクル対応アダプターが読み込み/エラー状態を処理

**コード参照:** `MainActivity.java:91-129`

#### 2. Realtime Database
**使用:** 永続化有効（`DisasterApplication.java:18`）

**目的:** オフラインデータキャッシング（使用されている場合）

**注意:** コード内に明示的なRealtime Databaseの参照なし。レガシーまたは将来の使用のためと思われます。

#### 3. Crashlytics
**統合:** カスタム`CrashReportingTree`がWARN以上のメッセージと例外をログ

**自動レポート:**
- `Timber.e(exception)`でログされたすべてのキャッチ済み例外
- 未キャッチ例外は自動レポート

**コード参照:** `DisasterApplication.java:30-43`

#### 4. Analytics
**追跡されるイベント:**
- `open_about` - ユーザーがAbout画面を開く
- `open_inquiry` - ユーザーが問い合わせメールを開始
- `open_playstore` - ユーザーがPlayストアでアプリを開く
- `SELECT_CONTENT` - ユーザーがエントリー詳細を表示（item_nameとurlを含む）

**コード参照:**
- `MainActivity.java:143, 148, 162`
- `EntryDetailActivity.java:71-76`

**レガシー:** Google Analytics設定（`res/xml/global_tracker.xml`）、トラッキングID `UA-3314949-13`

### 設定ファイル

#### 本番環境設定
**ファイル:** `app/google-services.json`
- **GITIGNORED**（`.gitignore:45`参照）
- Firebaseコンソールから取得必須
- ローカルビルドに必須

#### CIモック設定
**ファイル:** `.ci/google-services.json`
- リポジトリにコミット済み（参照用）
- GitHub Actionsワークフローでテンプレートとして使用

**GitHub Actionsがモック設定を自動生成:**
- `.github/workflows/build-and-test.yml:26-69`参照
- モックプロジェクト: `mock-project`
- モックAPIキー: `AIzaSyDummyKeyForCIBuildOnly123456789`

### Firebaseコンソールアクセス
**連絡先:** cutmailapp@gmail.com（プロジェクトオーナー）

---

## ビルドとデプロイ

### ビルドバリアント
- **debug** - 開発ビルド（ProGuardなし）
- **release** - 本番ビルド（ProGuard有効）

### ビルドコマンド

#### デバッグビルド
```bash
./gradlew assembleDebug
# 出力: app/build/outputs/apk/debug/app-debug.apk
```

#### リリースビルド
```bash
./gradlew assembleRelease
# 出力: app/build/outputs/apk/release/app-release.apk
# 署名認証情報が必要
```

#### デバイスへのデバッグインストール
```bash
./gradlew installDebug
```

### ProGuard設定
**ファイル:** `app/proguard-rules.pro`

**現状:** 最小限のルール（18行）

**注意:** リフレクションやJNIを使用するライブラリを追加する場合は、ここにkeepルールを追加してください。

### 署名設定
**リリースビルド用:**

環境変数を設定:
```bash
export SIGNING_STORE_FILE=/path/to/releasekey.keystore
export SIGNING_STORE_PASSWORD=store_password
export SIGNING_KEY_ALIAS=key_alias
export SIGNING_KEY_PASSWORD=key_password
```

または`local.properties`（gitignore）で設定:
```properties
signing.storeFile=/path/to/releasekey.keystore
signing.storePassword=store_password
signing.keyAlias=key_alias
signing.keyPassword=key_password
```

**注意:** `releasekey.keystore`はgitignoreされています（`.gitignore:18`）。

### Fastlaneデプロイ

#### テストレーン
```bash
bundle exec fastlane test
```
実行内容: `./gradlew test`

#### ベータレーン（Crashlytics Beta）
```bash
bundle exec fastlane beta
```
- リリースAPKをビルド
- Crashlytics Beta配布にアップロード

**必要なもの:** `fastlane/service-account.json`（gitignore）

#### デプロイレーン（Google Play）
```bash
bundle exec fastlane deploy
```
- 署名済みリリースAPKをビルド
- Google Playストアにアップロード

**必要なもの:**
- サービスアカウントJSON
- 署名認証情報（上記参照）

### CI/CDパイプライン

#### GitHub Actionsワークフロー
**ファイル:** `.github/workflows/build-and-test.yml`

**トリガー:**
- `main`、`master`、`develop`へのプッシュ
- `main`、`master`、`develop`へのプルリクエスト

**ステップ:**
1. コードのチェックアウト
2. JDK 11のセットアップ
3. `gradlew`に実行権限付与
4. **モック`google-services.json`の作成**（CIに必須）
5. Gradleパッケージのキャッシュ
6. Gradleでビルド
7. ユニットテストの実行
8. Lintチェックの実行
9. ビルドレポートのアップロード（失敗時）
10. デバッグAPKのアップロード（成功時、7日間保持）

**バッジ:** [![Build and Test](https://github.com/cutmail/DisasterApp/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/cutmail/DisasterApp/actions/workflows/build-and-test.yml)

**成果物:**
- ビルドレポート（失敗時）: `app/build/reports/`、`app/build/test-results/`
- デバッグAPK（成功時）: `app/build/outputs/apk/debug/*.apk`

---

## Gitワークフロー

### ブランチ命名規則

#### AIアシスタント（Claude）用
**パターン:** `claude/<タスク説明>-<ユニークセッションID>`

**例:**
- `claude/update-readme-011CV2vJirVvQ5kUvcjpZMqV`
- `claude/add-github-actions-workflow-011CV1p38as5EaPUbcnJhmPg`
- `claude/claude-md-mi83c5kntvqxskzy-0147XmQSYNyWVHPozAsMZ9UR`

**重要:** ブランチは以下の条件を満たす必要があります:
- `claude/`で始まる
- 現在のセッションIDで終わる
- プッシュ時に`-u`フラグを使用: `git push -u origin <branch-name>`
- 正しく命名されていないブランチへのプッシュは403エラーになります

#### 人間の貢献者用
**パターン:**
- `feature/<機能名>` - 新機能
- `fix/<バグ説明>` - バグ修正
- `update-<コンポーネント>` - 依存関係/コンポーネント更新
- `dependabot/<パッケージ>-<バージョン>` - DependabotのPR

**例:**
- `update-libraries`
- `update-butterknife`
- `dependabot/bundler/fastlane-2.156.1`

### メインブランチ
- **develop** - ステージングブランチ（`.git-pr-release`による）
- **main/master** - 本番ブランチ

**注意:** 現在のチェックアウトではmain/masterがローカルにない場合があります。

### コミットメッセージ規約

#### スタイル
- **命令形**（Add、Update、Fix、Delete、Migrate）
- 簡潔で説明的
- 末尾にピリオドなし
- 小文字で開始（固有名詞を除く）

#### パターン

**機能追加:**
```
Add GitHub Actions workflow for build and test
Add missing Firebase Firestore dependency
```

**更新:**
```
Update firebase libs
Update buildToolsVersion 30.0.3
Update README to reflect GitHub Actions workflow
```

**修正:**
```
Fix CI build by adding mock google-services.json
```

**バージョンバンプ:**
```
Bump up 1.9.3 (22)
Bump addressable from 2.7.0 to 2.8.1
```

**削除:**
```
Delete fabric script
```

**移行:**
```
Migrate to Firebase from Fabric
```

**マージコミット:**
```
Merge pull request #79 from cutmail/claude/update-readme-<id>
```

#### 言語
- **混在:** 英語と日本語
- **例:** `README を日本語化`

### プルリクエストワークフロー

1. **フィーチャーブランチを作成**（`develop`から）
2. **変更を加えて定期的にコミット**
3. **originにプッシュ:** `git push -u origin <branch-name>`
4. **プルリクエストを作成**（`develop`へ、または緊急修正は`main`へ）
5. **CIチェック**が自動実行
6. **レビューしてマージ**（チェック通過後）

**自動化ツール:**
- `.git-pr-release`設定によるdevelopからmainへの自動PR作成

### Git操作のベストプラクティス

#### リトライロジック付きプッシュ
```bash
# 初回プッシュ（新規ブランチには-u付き）
git push -u origin <branch-name>

# ネットワークエラーが発生した場合、指数バックオフで最大4回リトライ:
# 2秒待機、リトライ
# 4秒待機、リトライ
# 8秒待機、リトライ
# 16秒待機、リトライ
```

#### 特定ブランチのフェッチ
```bash
# 特定ブランチのフェッチを推奨
git fetch origin <branch-name>

# ネットワーク障害時のリトライ付きプル
git pull origin <branch-name>
```

---

## よくある作業

### タスク1: 新しいActivityの追加

#### 手順:
1. **activityクラスを作成** `app/src/main/java/me/cutmail/disasterapp/activity/`に
2. **レイアウトXMLを作成** `app/src/main/res/layout/`に
3. **AndroidManifest.xmlに追加**
4. **文字列を追加** `res/values/strings.xml`に
5. **ButterKnifeを使用** ビューバインディングに
6. **Timberをセットアップ** ロギングに

#### 例:
```java
package me.cutmail.disasterapp.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.cutmail.disasterapp.R;
import timber.log.Timber;

public class NewActivity extends AppCompatActivity {

    @BindView(R.id.some_view) ViewType mSomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        ButterKnife.bind(this);

        Timber.d("NewActivity created");
    }
}
```

**AndroidManifest.xml:**
```xml
<activity
    android:name=".activity.NewActivity"
    android:label="@string/title_activity_new">
</activity>
```

### タスク2: Firebase Analyticsイベントの追加

#### 手順:
1. **FirebaseAnalyticsインスタンスを取得** Activity内で
2. **Bundleを作成** イベントパラメータを含む
3. **イベントをログ** 説明的な名前で

#### 例:
```java
private FirebaseAnalytics mFirebaseAnalytics;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ...
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
}

private void trackCustomEvent(String itemName) {
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
    bundle.putString("custom_param", "value");
    mFirebaseAnalytics.logEvent("custom_event", bundle);
}
```

**参照:** `MainActivity.java:141-144, 146-150, 160-164`の例

### タスク3: 依存関係の更新

#### 手順:
1. **`app/build.gradle`を編集**
2. **バージョン番号を更新**
3. **Gradleを同期** (`./gradlew --refresh-dependencies`)
4. **ビルドをテスト** (`./gradlew build`)
5. **テストを実行** (`./gradlew test`)
6. **コミットメッセージ:** `Update <library-name> to <version>`

#### 古い依存関係のチェック:
```bash
./gradlew dependencyUpdates
```

**注意:** minSdkVersionとAndroidX互換性への影響を考慮してください。

### タスク4: Firestoreクエリの追加

#### 手順:
1. **Firestoreインスタンスを取得**
2. **クエリを構築** collection、where、orderByで
3. **FirestorePagingAdapterを使用** RecyclerView用に
4. **読み込み/エラー状態を処理**

#### 例（MainActivity.java:91-129参照）:
```java
Query query = FirebaseFirestore.getInstance()
    .collection("collection_name")
    .whereEqualTo("field", value)
    .orderBy("timestamp", Query.Direction.DESCENDING);

PagedList.Config config = new PagedList.Config.Builder()
    .setEnablePlaceholders(false)
    .setPrefetchDistance(10)
    .setPageSize(20)
    .build();

FirestorePagingOptions<Entry> options = new FirestorePagingOptions.Builder<Entry>()
    .setLifecycleOwner(this)
    .setQuery(query, config, Entry.class)
    .build();

FirestorePagingAdapter<Entry, ViewHolder> adapter =
    new FirestorePagingAdapter<Entry, ViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull ViewHolder holder,
                                       int position,
                                       @NonNull Entry model) {
            // ビューホルダーにデータをバインド
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                            int viewType) {
            // ビューホルダーを作成
        }

        @Override
        protected void onLoadingStateChanged(@NonNull LoadingState state) {
            // 読み込み状態を処理（LOADING_INITIAL、LOADING_MORE、LOADED、ERROR、FINISHED）
        }
    };

recyclerView.setAdapter(adapter);
```

### タスク5: メニューアイテムの追加

#### 手順:
1. **メニューXMLを作成/編集** `res/menu/`に
2. **文字列リソースを追加** `res/values/strings.xml`に
3. **メニューをインフレート** `onCreateOptionsMenu()`で
4. **クリックを処理** `onOptionsItemSelected()`で
5. **オプション:** Firebase Analyticsで追跡

#### 例（MainActivity.java:71-89、131-172参照）:
```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_name, menu);
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_item) {
        // Analyticsで追跡
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "action_item");
        mFirebaseAnalytics.logEvent("menu_action", bundle);

        // アクションを処理
        Intent intent = new Intent(this, TargetActivity.class);
        startActivity(intent);
        return true;
    }

    return super.onOptionsItemSelected(item);
}
```

### タスク6: Playストアメタデータの更新

#### 場所:
`fastlane/metadata/android/ja-JP/`

#### ファイル:
- `title.txt` - アプリタイトル（最大30文字）
- `short_description.txt` - 短い説明（最大80文字）
- `full_description.txt` - 完全な説明（最大4000文字）
- `changelogs/<version-code>.txt` - リリースノート（最大500文字）

#### 画像:
- `images/icon.png` - 512x512 PNG
- `images/featureGraphic.png` - 1024x500 PNG
- `images/phoneScreenshots/<N>_ja-JP.png` - スマートフォンスクリーンショット

#### Playストアへのデプロイ:
```bash
bundle exec fastlane deploy
```

**注意:** すべてのテキストは日本語です。適切なビジネストーンを使用してください。

---

## トラブルシューティング

### 問題1: "google-services.json Missing"でビルドが失敗

**症状:**
```
File google-services.json is missing. The Google Services Plugin cannot function without it.
```

**解決策:**
1. Firebaseコンソールから`google-services.json`をダウンロード
2. `app/google-services.json`に配置
3. コミットされていないことを確認（`.gitignore`をチェック）

**CI用:**
- モック設定はGitHub Actionsで自動生成
- `.github/workflows/build-and-test.yml:26-69`参照

### 問題2: ButterKnifeバインディングが失敗

**症状:**
```
@BindViewアノテーションフィールドにアクセス時にNullPointerException
```

**解決策:**
1. `ButterKnife.bind(this)`が`onCreate()`内で`setContentView()`の**後**に呼ばれていることを確認
2. ビューIDがJavaとXML間で一致していることを確認
3. `build.gradle`にアノテーションプロセッサがあることを確認:
   ```gradle
   annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
   ```
4. プロジェクトを再ビルド: `./gradlew clean build`

### 問題3: Firestoreクエリが空を返す

**症状:**
- RecyclerViewにデータが表示されない
- ログにエラーなし

**解決策:**
1. Firestoreコレクション名がクエリと一致しているか確認（`"entries"`）
2. Firestoreルールが読み取りアクセスを許可しているか確認
3. フィールド名がモデルと一致しているか確認（`title`、`url`）
4. Firebaseデバッグロギングを有効化:
   ```java
   FirebaseFirestore.setLoggingEnabled(true);
   ```
5. ネットワーク接続を確認
6. `google-services.json`が正しいことを確認

### 問題4: Git Pushが403で失敗

**症状:**
```
error: failed to push some refs (403 Forbidden)
```

**Claudeブランチの解決策:**
- ブランチ名が`claude/`で始まりセッションIDで終わることを確認
- `-u`フラグを使用: `git push -u origin claude/<task>-<session-id>`
- セッションIDが現在のセッションと一致しているか確認

**人間の貢献者の解決策:**
- リポジトリ権限を確認
- 認証情報を確認
- 再認証を試行: `git config --global credential.helper cache`

### 問題5: ProGuardがリリースビルドを破壊

**症状:**
- リリースAPKが起動時にクラッシュ
- ClassNotFoundExceptionまたはMethodNotFoundException

**解決策:**
1. `app/proguard-rules.pro`にkeepルールを追加:
   ```proguard
   -keep class me.cutmail.disasterapp.model.** { *; }
   -keepclassmembers class * {
       @butterknife.* <methods>;
   }
   ```
2. ライブラリドキュメントで必要なProGuardルールを確認
3. リリースビルドを頻繁にテスト: `./gradlew assembleRelease`
4. ProGuardマッピングファイルでクラッシュを復号: `app/build/outputs/mapping/release/mapping.txt`

### 問題6: Fastlaneデプロイが失敗

**症状:**
```
Google Api Error: Forbidden - The current user has insufficient permissions
```

**解決策:**
1. `fastlane/service-account.json`が存在し有効か確認
2. サービスアカウントが必要なPlayコンソール権限を持っているか確認:
   - Release ManagerまたはAdmin役割
3. Playコンソール設定でAPIアクセスが有効か確認
4. パッケージ名が一致するか確認: `me.cutmail.disasterapp`

### 問題7: Gradleビルドが遅い

**解決策:**
1. **Gradleデーモンを有効化**（通常はデフォルトで有効）
   ```properties
   # gradle.properties
   org.gradle.daemon=true
   ```

2. **ヒープサイズを増やす:**
   ```properties
   # gradle.properties
   org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
   ```

3. **ビルドキャッシュを使用:**
   ```properties
   # gradle.properties
   org.gradle.caching=true
   ```

4. **Gradleキャッシュをクリア:**
   ```bash
   ./gradlew clean
   rm -rf ~/.gradle/caches/
   ```

5. **Gradleラッパーをアップグレード:**
   ```bash
   ./gradlew wrapper --gradle-version=7.0
   ```

### 問題8: AndroidX移行の問題

**症状:**
- 重複クラスエラー
- サポートライブラリクラスのNoClassDefFoundError

**解決策:**
1. `gradle.properties`に以下があることを確認:
   ```properties
   android.useAndroidX=true
   android.enableJetifier=true
   ```
2. `build.gradle`にレガシーサポートライブラリ依存関係が**ない**ことを確認
3. 必要に応じてJetifierを手動実行:
   ```bash
   ./gradlew clean
   ./gradlew build --refresh-dependencies
   ```

---

## セキュリティ考慮事項

### 現在のセキュリティ注意事項

#### 1. クリアテキストトラフィック有効
**場所:** `AndroidManifest.xml:20`
```xml
android:usesCleartextTraffic="true"
```

**リスク:** 暗号化されていないHTTP接続を許可

**推奨:** 必要でない限り無効化。HTTPSのみを使用

#### 2. Gitignoreされたシークレット
**バージョン管理に含まれない重要なファイル:**
- `app/google-services.json` - Firebase設定
- `fastlane/service-account.json` - Playストアデプロイ認証情報
- `releasekey.keystore` - APK署名キー
- `local.properties` - ローカル設定
- `.envrc` - 環境変数

**コミット前にこれらがgitignoreされていることを必ず確認してください。**

#### 3. 本番環境でのProGuard
- リリースビルドで有効
- コードを難読化
- 最小限の設定（強化が必要な場合あり）

#### 4. 権限使用
**宣言された権限:**
- `INTERNET` - ネットワークアクセス（必須）
- `ACCESS_NETWORK_STATE` - ネットワーク状態チェック（必須）
- カスタム`C2D_MESSAGE`署名権限（レガシー、削除可能かも）

**危険な権限は不要** - 良好なセキュリティ姿勢。

---

## パフォーマンス最適化

### 現在の最適化
1. **Firebase Firestoreページング** - 効率的なデータ読み込み（20件/ページ）
2. **Firebase Realtime Database永続化** - オフラインキャッシング
3. **RecyclerView** - 効率的なリストレンダリング
4. **ProGuard** - リリースでのコード縮小と難読化

### 潜在的な改善
1. **画像読み込み** - 画像追加時はGlideまたはPicassoを追加
2. **ネットワークキャッシング** - WebView用HTTPキャッシュを実装
3. **データベースインデックス** - クエリ用Firestoreインデックスを確保
4. **遅延読み込み** - ページングで既に実装済み
5. **メモリリーク** - 検出用LeakCanaryの使用を検討

---

## 将来の拡張

### 推奨される近代化
1. **アーキテクチャ:** Jetpack ViewModelとLiveDataでMVVMへ移行
2. **ビューバインディング:** ButterKnifeをViewBinding（公式Androidソリューション）に置き換え
3. **依存性注入:** HiltまたはKoinを追加
4. **テスト:** 包括的なユニットとUIテストカバレッジ
5. **Kotlin:** モダンなAndroid開発のためKotlin移行を検討
6. **Jetpack Compose:** UI近代化を検討（長期的）
7. **Room Database:** オフラインファーストアーキテクチャ用ローカルキャッシングレイヤーを追加
8. **WorkManager:** バックグラウンドサービスを置き換え（追加された場合）

### 機能アイデア
1. 緊急災害警報のプッシュ通知
2. 災害発生場所を示すマップビュー
3. ユーザーカスタマイズ（地域、災害タイプでフィルタ）
4. ローカルストレージ付きオフラインモード
5. ホーム画面クイックアクセス用ウィジェット
6. ダークモードサポート

---

## リソース

### ドキュメント
- **README.md** - プロジェクト概要とセットアップ（日本語）
- **このファイル（CLAUDE.md）** - AIアシスタントガイド
- **Fastlane README** - `fastlane/README.md`

### 外部リンク
- [Google Playストア掲載](https://play.google.com/store/apps/details?id=me.cutmail.disasterapp)
- [GitHubリポジトリ](https://github.com/cutmail/DisasterApp)
- [GitHub Actionsワークフロー](https://github.com/cutmail/DisasterApp/actions)

### 主要ドキュメントサイト
- [Android Developer Docs](https://developer.android.com/)
- [Firebaseドキュメント](https://firebase.google.com/docs)
- [ButterKnifeドキュメント](https://jakewharton.github.io/butterknife/)
- [Timberドキュメント](https://github.com/JakeWharton/timber)
- [Fastlaneドキュメント](https://docs.fastlane.tools/)

### 連絡先
**サポートメール:** cutmailapp@gmail.com

---

## 変更履歴

### バージョン 1.9.3（versionCode: 22）
- 最新安定版リリース
- Firebase統合が完全に機能
- GitHub Actions CI/CD実装

### 最近の主要な変更
- **2024年:** FabricからFirebase Crashlyticsへ移行
- **2024年:** CI/CD用GitHub Actionsワークフロー追加
- **2024年:** README日本語化とドキュメント改善
- **2024年:** AndroidXと最新Firebase SDKへ更新

---

## コントリビューションガイドライン

### AIアシスタント向け
1. **このドキュメント全体を読む** 変更前に
2. **既存のコード規約に従う**（ButterKnife、Timber、パターン）
3. **テストを更新** テストインフラが追加された場合
4. **Lintチェックを実行** コミット前に: `./gradlew lint`
5. **ローカルでテスト** プッシュ前に
6. **明確なコミットメッセージを書く** 確立されたパターンに従う
7. **このドキュメントを更新** アーキテクチャ変更を行った場合

### 人間の貢献者向け
1. **リポジトリをフォーク**
2. **フィーチャーブランチを作成** `develop`から
3. **Javaコードスタイルに従う**（Android Studioデフォルト）
4. **テストを追加** 新機能用（テストインフラが存在する場合）
5. **README.mdを更新** ユーザー向け変更の場合
6. **プルリクエストを送信** `develop`ブランチへ
7. **CIチェックを待つ** 通過を確認

### コードレビューチェックリスト
- [ ] ビルド成功（`./gradlew build`）
- [ ] テスト合格（`./gradlew test`）
- [ ] Lintチェック合格（`./gradlew lint`）
- [ ] 新しい警告が導入されていない
- [ ] 既存のコードパターンに従っている
- [ ] Firebase Analyticsイベント追加済み（新しいユーザーアクションの場合）
- [ ] Timberロギングを適切に使用
- [ ] ハードコード文字列なし（`strings.xml`を使用）
- [ ] シークレットや認証情報がコミットされていない
- [ ] ProGuardルール追加済み（新しいリフレクション/JNI使用の場合）

---

**最終更新者:** Claude AIアシスタント
**日付:** 2025-11-20
**連絡先:** cutmailapp@gmail.com

---

*このドキュメントは、DisasterAppで作業するAIアシスタントと人間の開発者を対象としています。コードベースの進化に合わせて更新してください。*
