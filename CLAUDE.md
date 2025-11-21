# CLAUDE.md - AI Assistant Guide for DisasterApp

> **Last Updated:** 2025-11-20
> **Version:** 1.9.3 (versionCode: 22)

This document provides comprehensive guidance for AI assistants working on the DisasterApp codebase. It covers project structure, conventions, workflows, and best practices.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Codebase Structure](#codebase-structure)
3. [Key Technologies](#key-technologies)
4. [Development Setup](#development-setup)
5. [Code Conventions](#code-conventions)
6. [Testing Strategy](#testing-strategy)
7. [Firebase Integration](#firebase-integration)
8. [Build & Deployment](#build--deployment)
9. [Git Workflow](#git-workflow)
10. [Common Tasks](#common-tasks)
11. [Troubleshooting](#troubleshooting)

---

## Project Overview

**DisasterApp (地震・災害情報)** is a Japanese Android application that provides real-time earthquake and disaster information to users.

### Key Features
- Real-time disaster/earthquake information feed from Firebase Firestore
- Paginated list display with efficient loading (20 items/page)
- WebView-based detail views with external links
- Material Design UI with Japanese localization
- Firebase Analytics tracking
- In-app review prompts (AppRate library)
- User inquiry support via email

### Target Audience
Japanese users seeking timely disaster information.

### Business Model
Free app available on Google Play Store.

---

## Codebase Structure

```
DisasterApp/
├── .ci/
│   └── google-services.json              # Mock Firebase config for CI
├── .github/
│   └── workflows/
│       └── build-and-test.yml            # GitHub Actions CI/CD
├── app/
│   ├── build.gradle                       # App build configuration
│   ├── proguard-rules.pro                 # ProGuard rules
│   └── src/
│       ├── androidTest/
│       │   └── java/me/cutmail/disasterapp/
│       │       └── ApplicationTest.java   # Instrumentation tests
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/me/cutmail/disasterapp/
│           │   ├── DisasterApplication.java        # Application class
│           │   ├── activity/
│           │   │   ├── MainActivity.java           # Main list screen
│           │   │   ├── EntryDetailActivity.java    # Detail WebView
│           │   │   └── AboutActivity.java          # About screen
│           │   └── model/
│           │       └── Entry.java                  # Data model
│           └── res/
│               ├── layout/                         # XML layouts
│               ├── menu/                           # Menu definitions
│               ├── values/                         # Strings, styles, dimensions
│               └── xml/
│                   └── global_tracker.xml          # Analytics config
├── fastlane/
│   ├── Appfile                            # Fastlane configuration
│   ├── Fastfile                           # Deployment lanes
│   └── metadata/android/ja-JP/            # Play Store metadata
├── build.gradle                           # Root build configuration
├── settings.gradle                        # Project settings
├── gradle.properties                      # Gradle properties
├── Gemfile                                # Ruby dependencies (Fastlane)
└── README.md                              # Project documentation
```

### Java Package Structure

**Package:** `me.cutmail.disasterapp`

- **Root:** `DisasterApplication.java` (Application class)
- **activity/:** All Activity classes
  - `MainActivity.java` (199 LOC) - Main screen with paged list
  - `EntryDetailActivity.java` (110 LOC) - WebView detail screen
  - `AboutActivity.java` (75 LOC) - About/license screen
- **model/:** Data models
  - `Entry.java` (15 LOC) - Firestore entry model (title, url)

**Total Java Code:** ~443 lines across 6 files

---

## Key Technologies

### Language & Build System
- **Language:** Java 8
- **Build Tool:** Gradle 6.5
- **Android Gradle Plugin:** 4.1.1
- **Build Tools:** 30.0.3

### Android Configuration
- **compileSdkVersion:** 30
- **minSdkVersion:** 26 (Android 8.0 Oreo)
- **targetSdkVersion:** 30 (Android 11)
- **AndroidX:** Enabled

### Core Libraries

#### Firebase Stack
```gradle
firebase-ui-firestore: 7.1.1          // Paging adapter for Firestore
firebase-firestore: 22.0.1            // Cloud Firestore database
firebase-crashlytics: 17.3.0          // Crash reporting
firebase-analytics: 18.0.0            // Analytics tracking
firebase-database: 19.6.0             // Realtime Database
firebase-core: 18.0.0                 // Core Firebase SDK
```

#### AndroidX Libraries
```gradle
appcompat: 1.2.0                      // Backward compatibility
paging-runtime: 2.1.2                 // Paging library
```

#### UI & Utilities
```gradle
butterknife: 10.2.3                   // View binding (@BindView)
timber: 4.7.1                         // Logging framework
android-rate: 1.0.1                   // In-app review prompts
play-services-oss-licenses: 17.0.0    // OSS license screen
```

### Architecture Pattern
**Traditional Android (No MVVM/MVP/MVI)**
- Activities directly interact with Firebase
- No ViewModel, Repository, or UseCase layers
- No Dependency Injection framework

---

## Development Setup

### Prerequisites
1. **Android Studio Arctic Fox or later**
2. **JDK 8 or higher**
3. **Android SDK with API 30**
4. **Firebase project setup** (required for local development)

### Initial Setup Steps

#### 1. Clone Repository
```bash
git clone https://github.com/cutmail/DisasterApp.git
cd DisasterApp
```

#### 2. Configure Firebase (CRITICAL)
**The app will NOT build without this step.**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select the DisasterApp project (or create a new one for testing)
3. Download `google-services.json` from Project Settings
4. Place it in `app/google-services.json`

**Note:** This file is gitignored for security. CI builds use a mock version from `.ci/google-services.json`.

#### 3. Build the Project
```bash
./gradlew build
```

#### 4. Run Tests
```bash
./gradlew test           # Unit tests (currently minimal)
./gradlew connectedTest  # Instrumentation tests
```

#### 5. Install Debug APK
```bash
./gradlew installDebug
```

Or run directly from Android Studio.

### Fastlane Setup (Optional)
For automated deployments:

```bash
bundle install
bundle exec fastlane test    # Run tests
bundle exec fastlane beta    # Deploy to Crashlytics Beta
bundle exec fastlane deploy  # Deploy to Play Store
```

**Note:** Requires `fastlane/service-account.json` (gitignored) and signing credentials.

---

## Code Conventions

### Java Style

#### 1. View Binding with ButterKnife
**Pattern:**
```java
@BindView(R.id.view_id) ViewType mViewName;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_name);
    ButterKnife.bind(this);
}
```

**Examples:**
- `MainActivity.java:39-43` - RecyclerView and ProgressBar binding
- `AboutActivity.java:21-22` - Version TextView binding
- `EntryDetailActivity.java:29` - WebView binding

**Click Listeners:**
```java
@OnClick(R.id.button_id)
void onButtonClick() {
    // Handle click
}
```

#### 2. Logging with Timber
**Setup:** `DisasterApplication.setupTimber()` (line 22)

**Usage:**
```java
Timber.d("Debug message");
Timber.i("Info message");
Timber.w("Warning message");
Timber.e(exception, "Error message");
Timber.e(exception);  // Just log exception
```

**Production Behavior:**
- Debug builds: Logs to Logcat (all levels)
- Release builds: Logs WARN+ to Firebase Crashlytics

**See:** `DisasterApplication.java:30-43` for CrashReportingTree implementation

#### 3. Intent Factory Pattern
**Pattern:**
```java
public static Intent createIntent(Context context, String param1, String param2) {
    Intent intent = new Intent(context, TargetActivity.class);
    intent.putExtra(EXTRA_PARAM1, param1);
    intent.putExtra(EXTRA_PARAM2, param2);
    return intent;
}
```

**Example:** `EntryDetailActivity.createIntent()` at line 32-37

#### 4. Firebase Paging Pattern
**See:** `MainActivity.java:91-102`

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

#### 5. Error Handling
**Pattern:**
```java
try {
    // Operation that might fail
} catch (Exception e) {
    Timber.e(e);
    // No user-facing error messages (current pattern)
}
```

**Note:** This app silently handles errors. Consider adding user feedback for better UX.

### Resource Naming Conventions

#### Layout Files
- `activity_*.xml` - Activity layouts
- `*_list_item.xml` - RecyclerView item layouts
- `fragment_*.xml` - Fragment layouts (if any added)

#### View IDs
- Lowercase with underscores: `@id/paging_recycler`, `@id/paging_loading`
- Descriptive names indicating view type

#### Strings
- All strings in Japanese (primary language)
- Keys in English: `action_inquiry`, `action_review`, `title_activity_about`

#### Dimensions
- Standard margins: `activity_horizontal_margin` (16dp), `activity_vertical_margin` (16dp)
- Tablet overrides in `values-w820dp/` (64dp horizontal margin)

---

## Testing Strategy

### Current State
**Testing coverage is minimal** and needs significant improvement.

### Existing Tests
**File:** `app/src/androidTest/java/me/cutmail/disasterapp/ApplicationTest.java`
- Uses deprecated `ApplicationTestCase<Application>`
- Only 13 lines, no actual test methods
- Needs modernization

### Missing Test Infrastructure
- No `app/src/test/` directory (no unit tests)
- No modern testing frameworks (JUnit4, Espresso, Mockito, Robolectric)
- No test coverage measurement

### Recommended Testing Approach

#### 1. Unit Tests (To Be Added)
**Directory:** `app/src/test/java/me/cutmail/disasterapp/`

**Frameworks to add:**
```gradle
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito:mockito-core:4.0.0'
testImplementation 'androidx.arch.core:core-testing:2.1.0'
```

**Test targets:**
- `Entry.java` - Model validation
- Utility methods
- Intent creation methods

#### 2. Instrumentation Tests (To Be Modernized)
**Directory:** `app/src/androidTest/java/me/cutmail/disasterapp/`

**Frameworks to add:**
```gradle
androidTestImplementation 'androidx.test.ext:junit:1.1.3'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
androidTestImplementation 'androidx.test:runner:1.4.0'
androidTestImplementation 'androidx.test:rules:1.4.0'
```

**Test targets:**
- MainActivity UI interactions
- Navigation flows
- Firebase Firestore queries (use emulator)

#### 3. Running Tests

**Unit tests:**
```bash
./gradlew test --stacktrace
```

**Instrumentation tests:**
```bash
./gradlew connectedTest --stacktrace
```

**CI/CD Integration:**
Tests run automatically on GitHub Actions for all PRs and pushes to `main`, `master`, `develop`.

---

## Firebase Integration

### Services Used

#### 1. Firestore (Primary Database)
**Collection:** `entries`

**Schema:**
```javascript
{
  title: String,    // Disaster entry title
  url: String       // External link URL
}
```

**Query:** `orderBy("title")` (see `MainActivity.java:91`)

**Access pattern:**
- Read-only from app
- Paging: 20 items per page, 10 prefetch distance
- Lifecycle-aware adapter handles loading/error states

**Code reference:** `MainActivity.java:91-129`

#### 2. Realtime Database
**Usage:** Persistence enabled (`DisasterApplication.java:18`)

**Purpose:** Offline data caching (if used)

**Note:** No explicit Realtime Database references in code; may be legacy or for future use.

#### 3. Crashlytics
**Integration:** Custom `CrashReportingTree` logs WARN+ messages and exceptions

**Automatic reporting:**
- All caught exceptions logged with `Timber.e(exception)`
- Uncaught exceptions automatically reported

**Code reference:** `DisasterApplication.java:30-43`

#### 4. Analytics
**Events tracked:**
- `open_about` - User opens About screen
- `open_inquiry` - User initiates inquiry email
- `open_playstore` - User opens app in Play Store
- `SELECT_CONTENT` - User views entry detail (with item_name and url)

**Code references:**
- `MainActivity.java:143, 148, 162`
- `EntryDetailActivity.java:71-76`

**Legacy:** Google Analytics config (`res/xml/global_tracker.xml`) with tracking ID `UA-3314949-13`

### Configuration Files

#### Production Configuration
**File:** `app/google-services.json`
- **GITIGNORED** (see `.gitignore:45`)
- Must be obtained from Firebase Console
- Required for local builds

#### CI Mock Configuration
**File:** `.ci/google-services.json`
- Committed to repo for reference
- Used as template in GitHub Actions workflow

**GitHub Actions auto-generates mock config:**
- See `.github/workflows/build-and-test.yml:26-69`
- Mock project: `mock-project`
- Mock API key: `AIzaSyDummyKeyForCIBuildOnly123456789`

### Firebase Console Access
**Contact:** cutmailapp@gmail.com (project owner)

---

## Build & Deployment

### Build Variants
- **debug** - Development builds (no ProGuard)
- **release** - Production builds (ProGuard enabled)

### Build Commands

#### Debug Build
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

#### Release Build
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
# Requires signing credentials
```

#### Install Debug to Device
```bash
./gradlew installDebug
```

### ProGuard Configuration
**File:** `app/proguard-rules.pro`

**Current state:** Minimal rules (18 lines)

**Note:** If adding libraries with reflection or JNI, add keep rules here.

### Signing Configuration
**For release builds:**

Set environment variables:
```bash
export SIGNING_STORE_FILE=/path/to/releasekey.keystore
export SIGNING_STORE_PASSWORD=store_password
export SIGNING_KEY_ALIAS=key_alias
export SIGNING_KEY_PASSWORD=key_password
```

Or configure in `local.properties` (gitignored):
```properties
signing.storeFile=/path/to/releasekey.keystore
signing.storePassword=store_password
signing.keyAlias=key_alias
signing.keyPassword=key_password
```

**Note:** `releasekey.keystore` is gitignored (`.gitignore:18`).

### Fastlane Deployment

#### Test Lane
```bash
bundle exec fastlane test
```
Runs: `./gradlew test`

#### Beta Lane (Crashlytics Beta)
```bash
bundle exec fastlane beta
```
- Builds release APK
- Uploads to Crashlytics Beta distribution

**Requires:** `fastlane/service-account.json` (gitignored)

#### Deploy Lane (Google Play)
```bash
bundle exec fastlane deploy
```
- Builds signed release APK
- Uploads to Google Play Store

**Requires:**
- Service account JSON
- Signing credentials (see above)

### CI/CD Pipeline

#### GitHub Actions Workflow
**File:** `.github/workflows/build-and-test.yml`

**Triggers:**
- Push to: `main`, `master`, `develop`
- Pull requests to: `main`, `master`, `develop`

**Steps:**
1. Checkout code
2. Set up JDK 11
3. Grant execute permission to `gradlew`
4. **Create mock `google-services.json`** (critical for CI)
5. Cache Gradle packages
6. Build with Gradle
7. Run unit tests
8. Run lint checks
9. Upload build reports (on failure)
10. Upload debug APK (on success, retained 7 days)

**Badge:** [![Build and Test](https://github.com/cutmail/DisasterApp/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/cutmail/DisasterApp/actions/workflows/build-and-test.yml)

**Artifacts:**
- Build reports (on failure): `app/build/reports/`, `app/build/test-results/`
- Debug APK (on success): `app/build/outputs/apk/debug/*.apk`

---

## Git Workflow

### Branch Naming Conventions

#### For AI Assistants (Claude)
**Pattern:** `claude/<task-description>-<unique-session-id>`

**Examples:**
- `claude/update-readme-011CV2vJirVvQ5kUvcjpZMqV`
- `claude/add-github-actions-workflow-011CV1p38as5EaPUbcnJhmPg`
- `claude/claude-md-mi83c5kntvqxskzy-0147XmQSYNyWVHPozAsMZ9UR`

**IMPORTANT:** Branches must:
- Start with `claude/`
- End with session ID matching the current session
- Use `-u` flag when pushing: `git push -u origin <branch-name>`
- Pushing to incorrectly named branches will fail with 403

#### For Human Contributors
**Patterns:**
- `feature/<feature-name>` - New features
- `fix/<bug-description>` - Bug fixes
- `update-<component>` - Dependency/component updates
- `dependabot/<package>-<version>` - Dependabot PRs

**Examples:**
- `update-libraries`
- `update-butterknife`
- `dependabot/bundler/fastlane-2.156.1`

### Main Branches
- **develop** - Staging branch (per `.git-pr-release`)
- **main/master** - Production branch

**Note:** Current checkout may not have main/master locally.

### Commit Message Conventions

#### Style
- **Imperative mood** (Add, Update, Fix, Delete, Migrate)
- Concise and descriptive
- No trailing periods
- Lowercase start (except proper nouns)

#### Patterns

**Feature additions:**
```
Add GitHub Actions workflow for build and test
Add missing Firebase Firestore dependency
```

**Updates:**
```
Update firebase libs
Update buildToolsVersion 30.0.3
Update README to reflect GitHub Actions workflow
```

**Fixes:**
```
Fix CI build by adding mock google-services.json
```

**Version bumps:**
```
Bump up 1.9.3 (22)
Bump addressable from 2.7.0 to 2.8.1
```

**Removals:**
```
Delete fabric script
```

**Migrations:**
```
Migrate to Firebase from Fabric
```

**Merge commits:**
```
Merge pull request #79 from cutmail/claude/update-readme-<id>
```

#### Language
- **Mixed:** English and Japanese
- **Example:** `README を日本語化` (Japanize README)

### Pull Request Workflow

1. **Create feature branch** from `develop`
2. **Make changes** and commit regularly
3. **Push to origin:** `git push -u origin <branch-name>`
4. **Create Pull Request** to `develop` (or `main` for hotfixes)
5. **CI checks** run automatically
6. **Review and merge** once checks pass

**Automated tooling:**
- `.git-pr-release` config for automated PR creation from develop to main

### Git Operations Best Practices

#### Push with Retry Logic
```bash
# Initial push (with -u for new branches)
git push -u origin <branch-name>

# If network errors occur, retry up to 4 times with exponential backoff:
# Wait 2s, retry
# Wait 4s, retry
# Wait 8s, retry
# Wait 16s, retry
```

#### Fetch Specific Branches
```bash
# Prefer specific branch fetches
git fetch origin <branch-name>

# Pull with retry on network failures
git pull origin <branch-name>
```

---

## Common Tasks

### Task 1: Add a New Activity

#### Steps:
1. **Create activity class** in `app/src/main/java/me/cutmail/disasterapp/activity/`
2. **Create layout XML** in `app/src/main/res/layout/`
3. **Add to AndroidManifest.xml**
4. **Add strings** to `res/values/strings.xml`
5. **Use ButterKnife** for view binding
6. **Setup Timber** for logging

#### Example:
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

### Task 2: Add Firebase Analytics Event

#### Steps:
1. **Get FirebaseAnalytics instance** in activity
2. **Create Bundle** with event parameters
3. **Log event** with descriptive name

#### Example:
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

**See:** `MainActivity.java:141-144, 146-150, 160-164` for examples.

### Task 3: Update Dependencies

#### Steps:
1. **Edit `app/build.gradle`**
2. **Update version numbers**
3. **Sync Gradle** (`./gradlew --refresh-dependencies`)
4. **Test build** (`./gradlew build`)
5. **Run tests** (`./gradlew test`)
6. **Commit with message:** `Update <library-name> to <version>`

#### Check for outdated dependencies:
```bash
./gradlew dependencyUpdates
```

**Note:** Consider impact on minSdkVersion and AndroidX compatibility.

### Task 4: Add a Firestore Query

#### Steps:
1. **Get Firestore instance**
2. **Build query** with collection, where, orderBy
3. **Use FirestorePagingAdapter** for RecyclerView
4. **Handle loading/error states**

#### Example (see MainActivity.java:91-129):
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
            // Bind data to view holder
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                            int viewType) {
            // Create view holder
        }

        @Override
        protected void onLoadingStateChanged(@NonNull LoadingState state) {
            // Handle loading state (LOADING_INITIAL, LOADING_MORE, LOADED, ERROR, FINISHED)
        }
    };

recyclerView.setAdapter(adapter);
```

### Task 5: Add Menu Items

#### Steps:
1. **Create/edit menu XML** in `res/menu/`
2. **Add string resources** in `res/values/strings.xml`
3. **Inflate menu** in `onCreateOptionsMenu()`
4. **Handle clicks** in `onOptionsItemSelected()`
5. **Optional:** Track with Firebase Analytics

#### Example (see MainActivity.java:71-89, 131-172):
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
        // Track with analytics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "action_item");
        mFirebaseAnalytics.logEvent("menu_action", bundle);

        // Handle action
        Intent intent = new Intent(this, TargetActivity.class);
        startActivity(intent);
        return true;
    }

    return super.onOptionsItemSelected(item);
}
```

### Task 6: Update Play Store Metadata

#### Location:
`fastlane/metadata/android/ja-JP/`

#### Files:
- `title.txt` - App title (30 chars max)
- `short_description.txt` - Short description (80 chars max)
- `full_description.txt` - Full description (4000 chars max)
- `changelogs/<version-code>.txt` - Release notes (500 chars max)

#### Images:
- `images/icon.png` - 512x512 PNG
- `images/featureGraphic.png` - 1024x500 PNG
- `images/phoneScreenshots/<N>_ja-JP.png` - Phone screenshots

#### Deploy to Play Store:
```bash
bundle exec fastlane deploy
```

**Note:** All text is in Japanese. Use appropriate business tone.

---

## Troubleshooting

### Issue 1: Build Fails with "google-services.json Missing"

**Symptom:**
```
File google-services.json is missing. The Google Services Plugin cannot function without it.
```

**Solution:**
1. Download `google-services.json` from Firebase Console
2. Place in `app/google-services.json`
3. Ensure it's NOT committed (check `.gitignore`)

**For CI:**
- Mock config is auto-generated in GitHub Actions
- See `.github/workflows/build-and-test.yml:26-69`

### Issue 2: ButterKnife Binding Fails

**Symptom:**
```
NullPointerException when accessing @BindView annotated fields
```

**Solution:**
1. Ensure `ButterKnife.bind(this)` is called in `onCreate()` AFTER `setContentView()`
2. Check view IDs match between Java and XML
3. Verify annotation processor is in `build.gradle`:
   ```gradle
   annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
   ```
4. Rebuild project: `./gradlew clean build`

### Issue 3: Firebase Queries Return Empty

**Symptom:**
- RecyclerView shows no data
- No errors in logs

**Solution:**
1. Check Firestore collection name matches query (`"entries"`)
2. Verify Firestore Rules allow read access
3. Check field names match model (`title`, `url`)
4. Enable Firebase debug logging:
   ```java
   FirebaseFirestore.setLoggingEnabled(true);
   ```
5. Check network connectivity
6. Verify `google-services.json` is correct

### Issue 4: Git Push Fails with 403

**Symptom:**
```
error: failed to push some refs (403 Forbidden)
```

**Solution for Claude branches:**
- Ensure branch name starts with `claude/` and ends with session ID
- Use `-u` flag: `git push -u origin claude/<task>-<session-id>`
- Verify session ID matches current session

**Solution for human contributors:**
- Check repository permissions
- Verify authentication credentials
- Try re-authenticating: `git config --global credential.helper cache`

### Issue 5: ProGuard Breaks Release Build

**Symptom:**
- Release APK crashes on startup
- ClassNotFoundException or MethodNotFoundException

**Solution:**
1. Add keep rules to `app/proguard-rules.pro`:
   ```proguard
   -keep class me.cutmail.disasterapp.model.** { *; }
   -keepclassmembers class * {
       @butterknife.* <methods>;
   }
   ```
2. Check library documentation for required ProGuard rules
3. Test release builds frequently: `./gradlew assembleRelease`
4. Use ProGuard mapping file to deobfuscate crashes: `app/build/outputs/mapping/release/mapping.txt`

### Issue 6: Fastlane Deployment Fails

**Symptom:**
```
Google Api Error: Forbidden - The current user has insufficient permissions
```

**Solution:**
1. Verify `fastlane/service-account.json` exists and is valid
2. Check service account has required Play Console permissions:
   - Release Manager or Admin role
3. Ensure API access is enabled in Play Console settings
4. Verify package name matches: `me.cutmail.disasterapp`

### Issue 7: Gradle Build Slow

**Solutions:**
1. **Enable Gradle daemon** (usually enabled by default)
   ```properties
   # gradle.properties
   org.gradle.daemon=true
   ```

2. **Increase heap size:**
   ```properties
   # gradle.properties
   org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
   ```

3. **Use build cache:**
   ```properties
   # gradle.properties
   org.gradle.caching=true
   ```

4. **Clean Gradle cache:**
   ```bash
   ./gradlew clean
   rm -rf ~/.gradle/caches/
   ```

5. **Upgrade Gradle wrapper:**
   ```bash
   ./gradlew wrapper --gradle-version=7.0
   ```

### Issue 8: AndroidX Migration Issues

**Symptom:**
- Duplicate class errors
- NoClassDefFoundError for support library classes

**Solution:**
1. Verify `gradle.properties` has:
   ```properties
   android.useAndroidX=true
   android.enableJetifier=true
   ```
2. Ensure NO legacy support library dependencies in `build.gradle`
3. Run Jetifier manually if needed:
   ```bash
   ./gradlew clean
   ./gradlew build --refresh-dependencies
   ```

---

## Security Considerations

### Current Security Notes

#### 1. Cleartext Traffic Enabled
**Location:** `AndroidManifest.xml:20`
```xml
android:usesCleartextTraffic="true"
```

**Risk:** Allows unencrypted HTTP connections

**Recommendation:** Disable unless required; use HTTPS only

#### 2. Gitignored Secrets
**Important files NOT in version control:**
- `app/google-services.json` - Firebase configuration
- `fastlane/service-account.json` - Play Store deployment credentials
- `releasekey.keystore` - APK signing key
- `local.properties` - Local configuration
- `.envrc` - Environment variables

**Always verify these are gitignored before committing.**

#### 3. ProGuard in Production
- Enabled for release builds
- Obfuscates code
- Minimal configuration (may need enhancement)

#### 4. Permission Usage
**Declared permissions:**
- `INTERNET` - Network access (required)
- `ACCESS_NETWORK_STATE` - Network state checking (required)
- Custom `C2D_MESSAGE` signature permission (legacy, may be removable)

**No dangerous permissions required** - good security posture.

---

## Performance Optimization

### Current Optimizations
1. **Firebase Firestore paging** - Efficient data loading (20 items/page)
2. **Firebase Realtime Database persistence** - Offline caching
3. **RecyclerView** - Efficient list rendering
4. **ProGuard** - Code shrinking and obfuscation in release

### Potential Improvements
1. **Image loading** - Add Glide or Picasso if images are added
2. **Network caching** - Implement HTTP cache for WebViews
3. **Database indexing** - Ensure Firestore indexes for queries
4. **Lazy loading** - Already implemented via paging
5. **Memory leaks** - Consider using LeakCanary for detection

---

## Future Enhancements

### Recommended Modernizations
1. **Architecture:** Migrate to MVVM with Jetpack ViewModel and LiveData
2. **View Binding:** Replace ButterKnife with ViewBinding (official Android solution)
3. **Dependency Injection:** Add Hilt or Koin
4. **Testing:** Comprehensive unit and UI test coverage
5. **Kotlin:** Consider Kotlin migration for modern Android development
6. **Jetpack Compose:** Consider UI modernization (long-term)
7. **Room Database:** Add local caching layer for offline-first architecture
8. **WorkManager:** Replace any background services (if added)

### Feature Ideas
1. Push notifications for urgent disaster alerts
2. Map view showing disaster locations
3. User customization (filter by region, disaster type)
4. Offline mode with local storage
5. Widget for home screen quick access
6. Dark mode support

---

## Resources

### Documentation
- **README.md** - Project overview and setup (Japanese)
- **This file (CLAUDE.md)** - AI assistant guide
- **Fastlane README** - `fastlane/README.md`

### External Links
- [Google Play Store Listing](https://play.google.com/store/apps/details?id=me.cutmail.disasterapp)
- [GitHub Repository](https://github.com/cutmail/DisasterApp)
- [GitHub Actions Workflows](https://github.com/cutmail/DisasterApp/actions)

### Key Documentation Sites
- [Android Developer Docs](https://developer.android.com/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [ButterKnife Documentation](https://jakewharton.github.io/butterknife/)
- [Timber Documentation](https://github.com/JakeWharton/timber)
- [Fastlane Documentation](https://docs.fastlane.tools/)

### Contact
**Support Email:** cutmailapp@gmail.com

---

## Changelog

### Version 1.9.3 (versionCode: 22)
- Latest stable release
- Firebase integration fully functional
- GitHub Actions CI/CD implemented

### Recent Major Changes
- **2024:** Migrated from Fabric to Firebase Crashlytics
- **2024:** Added GitHub Actions workflow for CI/CD
- **2024:** README japanized and documentation improved
- **2024:** Updated to AndroidX and latest Firebase SDKs

---

## Contributing Guidelines

### For AI Assistants
1. **Read this entire document** before making changes
2. **Follow existing code conventions** (ButterKnife, Timber, patterns)
3. **Update tests** if test infrastructure is added
4. **Run lint checks** before committing: `./gradlew lint`
5. **Test locally** before pushing
6. **Write clear commit messages** following established patterns
7. **Update this document** if making architectural changes

### For Human Contributors
1. **Fork the repository**
2. **Create feature branch** from `develop`
3. **Follow Java code style** (Android Studio default)
4. **Add tests** for new features (once test infrastructure exists)
5. **Update README.md** if user-facing changes
6. **Submit pull request** to `develop` branch
7. **Wait for CI checks** to pass

### Code Review Checklist
- [ ] Builds successfully (`./gradlew build`)
- [ ] Tests pass (`./gradlew test`)
- [ ] Lint checks pass (`./gradlew lint`)
- [ ] No new warnings introduced
- [ ] Follows existing code patterns
- [ ] Firebase Analytics events added (if new user actions)
- [ ] Timber logging used appropriately
- [ ] No hardcoded strings (use `strings.xml`)
- [ ] No committed secrets or credentials
- [ ] ProGuard rules added (if new reflection/JNI usage)

---

**Last updated by:** Claude AI Assistant
**Date:** 2025-11-20
**Contact:** cutmailapp@gmail.com

---

*This document is intended for AI assistants and human developers working on DisasterApp. Keep it updated as the codebase evolves.*
