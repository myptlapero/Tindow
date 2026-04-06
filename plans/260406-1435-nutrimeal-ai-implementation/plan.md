---
title: "NutriMeal AI Implementation"
description: "Android app using Gemini AI for meal suggestions, nutrition info, weekly meal planning"
status: in-review
priority: P1
effort: 20h
branch: feature/ai-meal-planner
tags: [feature, android, ai, compose]
created: 2026-04-06
completed: 2026-04-06
---

# NutriMeal AI Implementation Plan

## Overview

Build NutriMeal AI Android app in 2-3 days (hackathon). Gemini AI powers meal suggestions from image/text input, nutrition details, 7-day meal planning, step-by-step cooking guides. UI matches Pencil designs (meal_ai.pen).

## Tech Stack

- Kotlin + Jetpack Compose + Material 3
- Compose Navigation (2 tabs + nested flows)
- Hilt DI
- Room DB (history + weekly plan)
- Retrofit + OkHttp (Gemini API + image search)
- Coil (image loading)
- ViewModel + StateFlow

## Design Reference

Pencil file: `/Users/linhmyx/Downloads/meal_ai.pen`
- Home (`HxG26`), Suggest (`MaZEj`), Meal Detail (`Nye5A`)
- Weekly Plan (`H0oE1`), Cooking Step (`rvCDD`), History (`cE8sL`)

Colors: Green primary (#4A7C59), beige bg (#F5F0E8), white cards

## Phases

| # | Phase | Status | Effort | Link |
|---|-------|--------|--------|------|
| 1 | Foundation + Dependencies | Complete | 3h | [phase-01](./phase-01-foundation-dependencies.md) |
| 2 | Data Layer + API Services | Complete | 4h | [phase-02](./phase-02-data-layer-api.md) |
| 3 | Home + Suggest Screens | Complete | 4h | [phase-03](./phase-03-home-suggest-screens.md) |
| 4 | Meal Detail + Cooking Steps | Complete | 3h | [phase-04](./phase-04-meal-detail-cooking.md) |
| 5 | Weekly Plan Screen | Complete | 3h | [phase-05](./phase-05-weekly-plan.md) |
| 6 | History + Polish + Export | Complete | 3h | [phase-06](./phase-06-history-polish.md) |

## Dependencies

- Phase 1 → Phase 2 (foundation needed for data layer)
- Phase 2 → Phase 3, 4, 5 (API services needed for screens)
- Phase 3 → Phase 4 (suggest → detail navigation)
- Phase 3, 5 → Phase 6 (history depends on suggestion flow + weekly plan)

## Package Structure

```
com.vjpro.tindow/
├── MainActivity.kt
├── TindowApplication.kt (Hilt)
├── core/
│   ├── base/view/BaseComposeActivity.kt (existing)
│   ├── extension/WindowExt.kt (existing)
│   └── di/AppModule.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/SuggestionHistoryDao.kt
│   │   ├── dao/WeeklyPlanDao.kt
│   │   └── entity/ (Room entities)
│   ├── remote/
│   │   ├── GeminiApiService.kt
│   │   └── ImageSearchService.kt
│   └── repository/
│       ├── MealRepository.kt
│       └── WeeklyPlanRepository.kt
├── domain/
│   └── model/ (Meal, Ingredient, WeeklyPlan, CookingStep, etc.)
└── ui/
    ├── navigation/AppNavigation.kt
    ├── theme/ (existing, update colors)
    ├── components/ (shared composables)
    ├── home/HomeScreen.kt, HomeViewModel.kt
    ├── suggest/SuggestScreen.kt, SuggestViewModel.kt
    ├── detail/MealDetailScreen.kt, MealDetailViewModel.kt
    ├── cooking/CookingStepScreen.kt, CookingStepViewModel.kt
    ├── weeklyplan/WeeklyPlanScreen.kt, WeeklyPlanViewModel.kt
    └── history/HistoryScreen.kt, HistoryViewModel.kt
```
