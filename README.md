# beTime

A minimalist, offline-first time-tracking and focus timer Android application.

## Features

- **Background-Persistent Timers:** Reliable countdown and focus timers that continue running accurately in the background.
- **Labels:** Organize time entries with color-coded labels, sub-labels, and support for uncertain/unclassified records.
- **Customizable Dashboard:** Reorderable drag-and-drop dashboard widgets to keep your most-used timers and controls accessible.
- **Activity Statistics:** View daily, weekly, and monthly breakdowns with interactive histograms.
- **Data Portability:** Full local backup and restore via CSV import and export.
- **Private by Design:** 100% offline. No accounts, no cloud sync, no ads, and no analytics or tracking.

[//]: # (- **Configurable Alerts:** Customize timer completion notifications with sound, ringtone selection, vibration, and banners.)

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose & Material 3
- **Persistence:** Room & Jetpack DataStore
- **Min SDK:** Android 8.0 (API 26)

## Building from Source

1. Clone the repository:
    ```bash
    git clone https://github.com/literandltx/beTime.git
    cd beTime
    ```
2. Build the debug APK using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
   The generated APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0-only)**. See the [LICENSE](LICENSE) file for details.