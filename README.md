# ![](app/src/main/res/drawable-xhdpi/ic_launcher.png) 地震・災害情報 (Earthquake & Disaster Information)

[![Build Status](https://app.bitrise.io/app/768666f9cbfb0cdb/status.svg?token=7UAkhytMM8p3ww6AdhidPw&branch=master)](https://app.bitrise.io/app/768666f9cbfb0cdb)

<a href="https://play.google.com/store/apps/details?id=me.cutmail.disasterapp"><img width="200" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/ja-play-badge.png" /></a>

## Overview

DisasterApp is an Android application that provides real-time earthquake and disaster information. The app aggregates and displays critical disaster-related entries to help users stay informed about emergency situations.

## Features

- Real-time disaster and earthquake information feed
- Paginated list view for efficient browsing
- Detailed entry view with external links
- User-friendly interface with material design
- Firebase integration for real-time data synchronization
- Rate the app functionality
- Contact/inquiry support

## Tech Stack

- **Language**: Java
- **Build System**: Gradle
- **Minimum SDK**: Android API level (as specified in app/build.gradle)
- **Architecture**: Android SDK with Firebase backend

### Key Dependencies

- **Firebase**:
  - Firestore for real-time database
  - Analytics for usage tracking
  - Crashlytics for crash reporting
- **Firebase UI**: Firestore paging adapter
- **AndroidX**: AppCompat, RecyclerView, Paging
- **ButterKnife**: View binding
- **Timber**: Logging
- **AppRate**: In-app rating dialog

## Project Structure

```
DisasterApp/
├── app/
│   └── src/
│       ├── main/
│       │   └── java/me/cutmail/disasterapp/
│       │       ├── activity/         # UI Activities
│       │       │   ├── MainActivity.java
│       │       │   ├── EntryDetailActivity.java
│       │       │   └── AboutActivity.java
│       │       ├── model/            # Data models
│       │       │   └── Entry.java
│       │       └── DisasterApplication.java
│       └── androidTest/              # Instrumented tests
├── fastlane/                         # Fastlane configuration
├── .ci/                              # CI configuration
├── build.gradle                      # Root build configuration
└── settings.gradle                   # Gradle settings
```

## Setup & Build

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK
- Firebase project configuration

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/cutmail/DisasterApp.git
cd DisasterApp
```

2. Add your Firebase configuration:
   - Download `google-services.json` from your Firebase console
   - Place it in the `app/` directory

3. Build the project:
```bash
./gradlew build
```

4. Run the app:
```bash
./gradlew installDebug
```

Or open the project in Android Studio and run it directly.

### Using Fastlane

The project includes Fastlane for automated builds and deployments:

```bash
bundle install
bundle exec fastlane [lane_name]
```

## Development

### Running Tests

```bash
./gradlew test           # Run unit tests
./gradlew connectedTest  # Run instrumented tests
```

### CI/CD

This project uses Bitrise for continuous integration. Build status is displayed at the top of this README.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Contact

For inquiries or support, please contact: cutmailapp@gmail.com

## License

Please refer to the project's license file for usage terms and conditions.

---

Made with ❤️ for disaster preparedness and awareness
