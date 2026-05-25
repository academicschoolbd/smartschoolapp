# School Nav

A modern Android app for a school's mobile dashboard, built with **Kotlin + Jetpack Compose + Material 3**.

## Features

- Splash screen (using `androidx.core.splashscreen`, displays before the activity is fully ready)
- Modern **home screen** with:
  - Auto-scrolling **top banner** (infinite `HorizontalPager`, 4-second auto-advance, animated indicator)
  - **Academic** section grid — Result, Student List, Class Details, Latest Activity, Class Routine, Exam Routine
  - **Teacher** section grid — Teacher List, Mark Entry
  - **Important** section grid — Notice, Events, Contact, About
- **Demo screen** wired up for every grid button via Navigation Compose — easy to swap in real implementations one at a time
- **Firebase Cloud Messaging** push notifications (system tray + heads-up)
- **Floating in-app notification banner** that slides in from the top while the app is foregrounded — fed by both FCM pushes and any in-app `FloatingNotificationCenter.post(...)` call
- **Tiro Bangla** typography via Compose's downloadable Google Fonts provider — no font files to ship; cached automatically by Google Play Services after first download
- Material 3 dynamic color (Android 12+) with a custom blue/green brand fallback for older devices and edge-to-edge layout

## Tech stack

| Concern            | Library                                                    |
| ------------------ | ---------------------------------------------------------- |
| Language           | Kotlin 2.0.21                                              |
| UI                 | Jetpack Compose (BOM 2024.12.01), Material 3               |
| Navigation         | `androidx.navigation:navigation-compose` 2.8.4             |
| Splash screen      | `androidx.core:core-splashscreen` 1.0.1                    |
| Images             | Coil 2.7.0                                                 |
| Push notifications | Firebase Cloud Messaging (Firebase BOM 33.6.0)             |
| Typography         | Compose `ui-text-google-fonts` (Tiro Bangla)               |
| Build              | Android Gradle Plugin 8.7.3, Gradle 8.10.2, JDK 17         |

## Getting started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK 17** (Android Studio bundles a compatible JBR)
- **Android SDK 35** + build-tools 35.0.0 (Android Studio will offer to install these on first sync)
- A device or emulator running **Android 7.0 (API 24)** or newer

### Clone & open

```bash
git clone https://github.com/academicschoolbd/smartschoolapp.git
cd smartschoolapp
```

Then in Android Studio: **File → Open…** and select the cloned folder. Wait for Gradle sync to finish (it will download dependencies and the Gradle 8.10.2 distribution on first run).

### Run

- Pick a device/emulator in the toolbar.
- Press the green ▶ Run button (or `Shift+F10`).

### Build from the command line

```bash
./gradlew assembleDebug          # build the debug APK
./gradlew installDebug           # build + install on a connected device
./gradlew lint                   # static analysis
```

## Firebase / push notifications setup

The repo ships with a **placeholder** `app/google-services.json`. The app will compile and run, but real push notifications won't work until you replace it with your own config.

1. Go to the [Firebase console](https://console.firebase.google.com/) and create a project (or pick an existing one).
2. **Add app → Android**, using:
   - **Release** applicationId: `com.schoolnav.app`
   - **Debug** applicationId: `com.schoolnav.app.debug` (add this as a second app under the same project so debug builds also receive pushes)
3. Download the generated `google-services.json` and replace `app/google-services.json` in this repo with it.
4. Re-run `./gradlew assembleDebug`. That's it — `SchoolFirebaseMessagingService` is already registered in `AndroidManifest.xml`.

### Sending a test notification

From the Firebase console: **Messaging → New campaign → Notification**, target your app and send. The user will see:
- The system notification in the tray.
- The **floating in-app banner** (when the app is in the foreground), which is also triggered by `FloatingNotificationCenter.post(...)` from anywhere in the app — try tapping the bell icon in the home screen header to see it.

## Project structure

```
app/src/main/java/com/schoolnav/app/
├── SchoolNavApplication.kt          # Creates the default notification channel
├── MainActivity.kt                  # Installs system splash + hosts SchoolNavApp
├── data/HomeData.kt                 # Static section/banner data shown on the home screen
├── notifications/
│   ├── FloatingNotificationCenter.kt    # In-app pub-sub for the floating banner
│   └── SchoolFirebaseMessagingService.kt
└── ui/
    ├── SchoolNavApp.kt              # Root composable: theme + nav graph + floating overlay
    ├── theme/                       # Material 3 colors, typography (Tiro Bangla)
    ├── navigation/                  # Destination enum + NavHost
    ├── components/                  # TopBanner, GridSection, FloatingNotification
    └── screens/                     # HomeScreen, DemoScreen
```

## Next steps

Each grid button currently opens a shared `DemoScreen`. To wire in real UI for, say, **Result**, just replace its branch inside `SchoolNavGraph.kt`:

```kotlin
composable(Destination.Result.route) { ResultScreen(onBack = { navController.popBackStack() }) }
```

— and add `ResultScreen.kt` under `ui/screens/`.

## License

This project is provided as-is for use as a starter template.
