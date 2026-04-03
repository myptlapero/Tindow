# Phase 1: Dependencies & Project Setup

## Context
- [plan.md](plan.md)
- Current: fresh Compose project with basic Material3 deps

## Overview
- **Priority:** P0
- **Status:** complete
- **Effort:** 30 min

## Requirements
- Add all necessary dependencies to version catalog + build.gradle.kts
- Add internet permission for API calls
- Verify project compiles

## Related Code Files
- **Modify:** `gradle/libs.versions.toml`
- **Modify:** `app/build.gradle.kts`
- **Modify:** `app/src/main/AndroidManifest.xml`

## Implementation Steps

### 1. Update `gradle/libs.versions.toml`
Add versions:
```toml
navigationCompose = "2.8.5"
lifecycleViewmodelCompose = "2.8.7"
coilCompose = "2.7.0"
retrofit = "2.11.0"
moshi = "1.15.1"
okhttp = "4.12.0"
```

Add libraries:
```toml
# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# ViewModel Compose
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }

# Image loading
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coilCompose" }

# Networking
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-moshi = { group = "com.squareup.retrofit2", name = "converter-moshi", version.ref = "retrofit" }
moshi-kotlin = { group = "com.squareup.moshi", name = "moshi-kotlin", version.ref = "moshi" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
```

### 2. Update `app/build.gradle.kts`
Add to dependencies block:
```kotlin
implementation(libs.androidx.navigation.compose)
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.coil.compose)
implementation(libs.retrofit)
implementation(libs.retrofit.converter.moshi)
implementation(libs.moshi.kotlin)
implementation(libs.okhttp)
```

### 3. Update `AndroidManifest.xml`
Add before `<application>`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 4. Verify
Run `./gradlew assembleDebug` to confirm everything compiles.

## Todo
- [x] Update version catalog with new dependencies
- [x] Update app build.gradle.kts
- [x] Add INTERNET permission to manifest
- [x] Verify compilation

## Success Criteria
- Project compiles with all new dependencies
- No version conflicts
