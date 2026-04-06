# Phase 1: Foundation + Dependencies

## Overview
- **Priority:** P0
- **Status:** Complete
- **Effort:** 3h
- **Description:** Setup all dependencies, Hilt DI, theme colors, navigation scaffold, base models

## Context Links
- [Brainstorm Report](../reports/brainstorm-260406-1435-nutrimeal-ai-implementation.md)
- [Pencil UI](file:///Users/linhmyx/Downloads/meal_ai.pen)

## Requirements
- Add all Gradle dependencies (Hilt, Room, Retrofit, Coil, Navigation, Gemini SDK)
- Configure Hilt application class
- Update theme colors to match Pencil design (green palette)
- Setup Compose Navigation with 2-tab bottom bar (Home, Thực đơn)
- Read Gemini API key from local.properties via BuildConfig

## Related Code Files

### Modify
- `gradle/libs.versions.toml` — add version entries
- `build.gradle.kts` (root) — add Hilt/KSP plugins
- `app/build.gradle.kts` — add dependencies + BuildConfig for API key
- `app/src/main/java/com/vjpro/tindow/ui/theme/Color.kt` — green palette
- `app/src/main/java/com/vjpro/tindow/ui/theme/Theme.kt` — NutriMeal color scheme
- `app/src/main/java/com/vjpro/tindow/MainActivity.kt` — navigation host + bottom bar
- `app/src/main/AndroidManifest.xml` — add INTERNET, CAMERA permissions + Hilt app

### Create
- `app/src/main/java/com/vjpro/tindow/TindowApplication.kt` — @HiltAndroidApp
- `app/src/main/java/com/vjpro/tindow/core/di/AppModule.kt` — Hilt module (Retrofit, Room, repos)
- `app/src/main/java/com/vjpro/tindow/ui/navigation/AppNavigation.kt` — NavHost + routes
- `app/src/main/java/com/vjpro/tindow/ui/navigation/BottomNavBar.kt` — Home/Thực đơn tabs
- `app/src/main/java/com/vjpro/tindow/ui/navigation/Screen.kt` — sealed class routes

## Implementation Steps

### 1. Gradle Dependencies
Add to `libs.versions.toml`:
```toml
# New versions
hilt = "2.51.1"
ksp = "2.1.0-1.0.29"
room = "2.6.1"
retrofit = "2.11.0"
okhttp = "4.12.0"
coil = "2.7.0"
navigationCompose = "2.8.5"
hiltNavigationCompose = "1.2.0"
gson = "2.11.0"
generativeai = "0.9.0"
```

Add plugins: KSP, Hilt

### 2. App build.gradle.kts
- Apply KSP + Hilt plugins
- Add `buildConfigField` for GEMINI_API_KEY from `local.properties`
- Enable `buildConfig = true` in buildFeatures
- Add all library dependencies

### 3. Theme Colors (match Pencil)
```kotlin
// Primary green palette
val GreenPrimary = Color(0xFF4A7C59)
val GreenDark = Color(0xFF3D6B4A)
val GreenLight = Color(0xFF6B9F7C)
val GreenSurface = Color(0xFFE8F0EA)

// Background
val BeigeBackground = Color(0xFFF5F0E8)
val WhiteCard = Color(0xFFFFFFFF)

// Text
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B6B6B)

// Nutrition chips
val CalorieOrange = Color(0xFFE8913A)
val ProteinBlue = Color(0xFF4A90D9)
val CarbYellow = Color(0xFFD4A843)
val FatRed = Color(0xFFD94A4A)
```

### 4. TindowApplication + Hilt
```kotlin
@HiltAndroidApp
class TindowApplication : Application()
```

### 5. Navigation Setup
- Screen sealed class: Home, Suggest, MealDetail, WeeklyPlan, CookingStep, History
- NavHost in MainActivity with bottom bar (Home, WeeklyPlan tabs)
- Bottom bar matches Pencil: green selected, gray unselected, icons + labels

### 6. AndroidManifest Updates
- `android:name=".TindowApplication"`
- Permissions: `INTERNET`, `CAMERA`

## Todo List
- [x] Update libs.versions.toml with all dependencies
- [x] Update root build.gradle.kts with Hilt/KSP plugins
- [x] Update app build.gradle.kts with deps + BuildConfig
- [x] Create TindowApplication.kt
- [x] Update Color.kt with green palette
- [x] Update Theme.kt with NutriMeal color scheme
- [x] Create Screen.kt (navigation routes)
- [x] Create AppNavigation.kt (NavHost)
- [x] Create BottomNavBar.kt
- [x] Update MainActivity.kt with navigation
- [x] Update AndroidManifest.xml
- [x] Create AppModule.kt (empty Hilt module skeleton)
- [x] Verify build compiles successfully

## Success Criteria
- App compiles with all new dependencies
- Bottom nav shows 2 tabs (Home, Thực đơn)
- Navigation between empty screen placeholders works
- Theme colors match Pencil design
- Gemini API key accessible via BuildConfig

## Risk Assessment
- Hilt + KSP version compatibility — use tested combo (Hilt 2.51.1 + KSP 2.1.0-1.0.29)
- Gemini SDK vs raw Retrofit — use google `generativeai` SDK for simplicity
