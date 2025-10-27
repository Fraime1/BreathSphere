# BreathSphere 🫧

A beautiful and fully functional breathing practice and relaxation app for Android.

## Features ✨

### 🎯 Core Functionality
- **Breathing Animation**: A live, animated pink sphere that expands on inhale and contracts on exhale
- **Multiple Modes**: 
  - 🌙 **Sleep** - Long breaths (4-7-8 pattern) for deep relaxation
  - ⚡ **Focus** - Balanced rhythm (3-3-3-3 pattern) for concentration
  - 😌 **Relax** - Gentle breathing (5-5 pattern) for calm
- **Session Tracking**: Save and track your breathing sessions
- **Statistics**: View your practice history with an interactive calendar and bubble chart
- **Customization**: Choose from 3 beautiful themes (Pink, Blue, Green)

### 🎨 Beautiful UI
- Dark cosmic gradient background (#0D0D1A → #1A0D2E → #2B0D4F)
- Glowing animated sphere with gradient colors
- Floating particle effects in the background
- Smooth animations and transitions
- Material Design 3 components

### 📱 Screens
1. **Home Screen**: Main breathing session with animated sphere
2. **Modes Screen**: Select your preferred breathing pattern
3. **Statistics Screen**: View your practice history with visual calendar
4. **Settings Screen**: Customize theme and preferences

## Technical Details 🔧

### Requirements
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose

### Dependencies
- **Navigation**: AndroidX Navigation Compose
- **State Management**: ViewModel & StateFlow
- **Data Storage**: DataStore Preferences
- **UI**: Material3, Compose Animation

### Architecture
- **MVVM Pattern**: Separation of concerns with ViewModel
- **Repository Pattern**: Abstracted data access
- **Jetpack Compose**: Modern declarative UI
- **State Management**: Reactive state with Kotlin Flows

## Project Structure 📂

```
app/src/main/java/com/breaswl/spexerutil/
├── data/
│   ├── BreathingMode.kt          # Breathing mode data model
│   ├── BreathingSession.kt       # Session data model
│   ├── SessionRepository.kt      # Session data repository
│   └── UserPreferences.kt        # User preferences repository
├── ui/
│   ├── components/
│   │   ├── BackgroundParticles.kt    # Animated particle background
│   │   └── BreathingSphere.kt        # Main animated breathing sphere
│   ├── screens/
│   │   ├── HomeScreen.kt             # Main breathing session screen
│   │   ├── ModesScreen.kt            # Mode selection screen
│   │   ├── StatisticsScreen.kt       # Statistics and history screen
│   │   └── SettingsScreen.kt         # Settings and profile screen
│   └── theme/
│       ├── Color.kt                  # Color palette
│       ├── Theme.kt                  # Material theme configuration
│       └── Type.kt                   # Typography
├── viewmodel/
│   └── BreathingViewModel.kt     # Main ViewModel
├── navigation/
│   └── Navigation.kt             # Navigation setup
└── MainActivity.kt               # App entry point
```

## How to Build 🔨

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

## Usage 💡

1. **Start the App**: Launch BreathSphere
2. **Select a Mode**: Tap the menu icon to choose Sleep, Focus, or Relax
3. **Choose Duration**: Select 1, 3, 5, 10, or 20 minutes
4. **Start Breathing**: Tap the play button to begin your session
5. **Follow the Sphere**: Breathe in as it expands, breathe out as it contracts
6. **Track Progress**: View your statistics and history in the Settings screen

## Breathing Patterns 🫁

- **Sleep (4-7-8)**: Inhale for 4s → Hold for 7s → Exhale for 8s
- **Focus (3-3-3-3)**: Inhale for 3s → Hold for 3s → Exhale for 3s → Hold for 3s
- **Relax (5-5)**: Inhale for 5s → Exhale for 5s

## Color Themes 🎨

- **Pink**: #FF4FBF → #B84FFF (Default)
- **Blue**: #4F9FFF → #4FBFFF
- **Green**: #4FFF9F → #4FFFE0

## Optimization 🚀

The app is optimized for minimal disk space:
- Uses only essential dependencies
- DataStore for efficient local storage
- No large media files or unnecessary resources
- Optimized ProGuard rules for release builds

## Future Enhancements 🌟

- Background music/sounds (waves, forest, white noise)
- Notification reminders
- Export statistics to PDF
- More breathing patterns
- Widget support
- Achievement system

## License

Copyright © 2024 BreathSphere. All rights reserved.

---

Made with ❤️ using Kotlin and Jetpack Compose

