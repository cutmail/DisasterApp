# ![](app/src/main/res/drawable-xhdpi/ic_launcher.png) 地震・災害情報

[![Build and Test](https://github.com/cutmail/DisasterApp/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/cutmail/DisasterApp/actions/workflows/build-and-test.yml)

<a href="https://play.google.com/store/apps/details?id=me.cutmail.disasterapp"><img width="200" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/ja-play-badge.png" /></a>

## 概要

地震・災害情報アプリは、リアルタイムで地震や災害に関する情報を提供するAndroidアプリケーションです。重要な災害関連の情報を集約・表示し、ユーザーが緊急事態について常に最新の情報を得られるようにします。

## 機能

- リアルタイムの災害・地震情報フィード
- 効率的な閲覧のためのページング対応リスト表示
- 外部リンク付き詳細情報ビュー
- マテリアルデザインを採用したユーザーフレンドリーなインターフェース
- Firebaseとの連携によるリアルタイムデータ同期
- アプリ評価機能
- お問い合わせサポート

## 技術スタック

- **言語**: Java
- **ビルドシステム**: Gradle
- **最小SDK**: Android APIレベル（app/build.gradleに記載）
- **アーキテクチャ**: Android SDK + Firebaseバックエンド

### 主要な依存ライブラリ

- **Firebase**:
  - Firestore（リアルタイムデータベース）
  - Analytics（使用状況の追跡）
  - Crashlytics（クラッシュレポート）
- **Firebase UI**: Firestoreページングアダプター
- **AndroidX**: AppCompat、RecyclerView、Paging
- **ButterKnife**: ビューバインディング
- **Timber**: ログ出力
- **AppRate**: アプリ内評価ダイアログ

## プロジェクト構成

```
DisasterApp/
├── app/
│   └── src/
│       ├── main/
│       │   └── java/me/cutmail/disasterapp/
│       │       ├── activity/         # UIアクティビティ
│       │       │   ├── MainActivity.java
│       │       │   ├── EntryDetailActivity.java
│       │       │   └── AboutActivity.java
│       │       ├── model/            # データモデル
│       │       │   └── Entry.java
│       │       └── DisasterApplication.java
│       └── androidTest/              # インストルメンテーションテスト
├── fastlane/                         # Fastlane設定
├── .ci/                              # CI設定
├── build.gradle                      # ルートビルド設定
└── settings.gradle                   # Gradle設定
```

## セットアップとビルド

### 前提条件

- Android Studio Arctic Fox以降
- JDK 8以上
- Android SDK
- Firebaseプロジェクトの設定

### ビルド手順

1. リポジトリをクローン:
```bash
git clone https://github.com/cutmail/DisasterApp.git
cd DisasterApp
```

2. Firebase設定ファイルを追加:
   - Firebaseコンソールから`google-services.json`をダウンロード
   - `app/`ディレクトリに配置

3. プロジェクトをビルド:
```bash
./gradlew build
```

4. アプリを実行:
```bash
./gradlew installDebug
```

または、Android Studioでプロジェクトを開いて直接実行することもできます。

### Fastlaneの使用

このプロジェクトには自動ビルドとデプロイのためのFastlaneが含まれています:

```bash
bundle install
bundle exec fastlane [lane_name]
```

## 開発

### テストの実行

```bash
./gradlew test           # ユニットテストを実行
./gradlew connectedTest  # インストルメンテーションテストを実行
```

### CI/CD

このプロジェクトは継続的インテグレーションにGitHub Actionsを使用しています。ビルドステータスはこのREADMEの上部に表示されます。

ワークフローには以下が含まれます:
- Gradleビルド
- ユニットテストの実行
- Lintチェック
- ビルド成果物（APK）のアップロード

ワークフローは`.github/workflows/build-and-test.yml`に定義されています。

## コントリビューション

コントリビューションを歓迎します！お気軽にプルリクエストを提出してください。

1. リポジトリをフォーク
2. フィーチャーブランチを作成 (`git checkout -b feature/amazing-feature`)
3. 変更をコミット (`git commit -m 'Add some amazing feature'`)
4. ブランチにプッシュ (`git push origin feature/amazing-feature`)
5. プルリクエストを作成

## お問い合わせ

お問い合わせやサポートについては、こちらまでご連絡ください: cutmailapp@gmail.com

## ライセンス

使用条件については、プロジェクトのライセンスファイルをご参照ください。

---

防災と災害への備えのために ❤️
