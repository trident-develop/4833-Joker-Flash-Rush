# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Joker Flash Rush is a Quizlet-inspired flashcard / study app built with Kotlin Multiplatform (KMP) + Compose Multiplatform, targeting Android and iOS. It uses a warm premium dark theme (brown/red/amber/orange).

## Build Commands

```shell
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Compile Android (fast check)
./gradlew :composeApp:compileDebugKotlinAndroid

# Compile iOS simulator
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# Run common tests
./gradlew :composeApp:allTests

# iOS: open iosApp/ in Xcode and run from there
```

## Architecture

**Single module:** `composeApp` — all code lives here.

**App entry flow:** `MainActivity`/`MainViewController` -> `App()` -> `JokerFlashTheme` -> `Gray()` -> 2s loading -> `AppNavGraph()`

**Source set layout (`org.example.project`):**
- `theme/` — `AppColors`, `JokerFlashTheme` (Material3 dark color scheme)
- `models/` — `Deck`, `FlashCard`, `UserProfile`, `Achievement`, `StudyMode`
- `data/` — `SampleData` with 16 mock decks and realistic flashcards
- `navigation/` — `BottomTab` enum (Home, Study, Create, Collections, Profile)
- `components/` — reusable composables: `AnimatedBottomBar`, `DeckCard*`, `FlashCardView` (3D flip), `SearchField`, `ChipRow`, `StatsCard`, `SegmentedControl`, `GradientButton`, `ProgressBar`, `Badge`, `ProfileAvatar`, `SettingsRow`, `WarmSwitch`
- `screens/` — `LoadingScreen` (animated), `HomeScreen`, `StudyScreen`, `CreateScreen`, `CollectionsScreen`, `ProfileScreen`, `NoInternetScreen`
- `platform/` — `LegalSection` and `ProfileImagePickerDialog` (expect/actual)

**Key `expect`/`actual` declarations:**
- `Gray` — loading-to-content transition (2s Crossfade); both platforms identical
- `PlatformWebView` — Android `WebView` / iOS `WKWebView`
- `LegalSection` — Android: Privacy Policy only; iOS: Privacy Policy + Terms of Use
- `ProfileImagePickerDialog` — Android: PickVisualMedia + FileProvider camera; iOS: PHPickerViewController + UIImagePickerController via ViewControllerHolder

**iOS-specific patterns:**
- `ViewControllerHolder` singleton stores `ComposeUIViewController` reference (set in `MainViewController.kt`)
- All VC presentation traverses `presentedViewController` chain to find topmost
- ObjC delegates stored in strong top-level vars to prevent GC
- NSData→ByteArray uses `usePinned` + `memcpy`

## Dependencies

- Compose Multiplatform 1.10.3, Material 3, Material Icons Extended
- Kotlin 2.3.20, AGP 8.11.2
- Ktor client (core in common, Darwin on iOS, OkHttp on Android)
- AndroidX Lifecycle ViewModel + Runtime for Compose
- Version catalog at `gradle/libs.versions.toml`

## Notes

- Android namespace/applicationId: `org.example.project`
- Gradle configuration cache and build caching enabled
- JVM target 11, minSdk 24, targetSdk/compileSdk 36
- iOS framework: `ComposeApp` (static)
- FileProvider configured at `${applicationId}.fileprovider` for camera capture
- Info.plist has `NSCameraUsageDescription` and `NSPhotoLibraryUsageDescription`
- `NoInternetScreen` must not be modified — it is part of the Gray contract
- Legal URLs are test placeholders: `https://example.com/privacy`, `https://example.com/terms`
