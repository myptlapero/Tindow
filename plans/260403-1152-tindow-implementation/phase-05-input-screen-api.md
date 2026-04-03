# Phase 5: Input Screen & API Integration

## Context
- [plan.md](plan.md) | Depends on: Phase 2, 3
- [API Research](../reports/researcher-260403-1152-free-api-research.md)

## Overview
- **Priority:** P0
- **Status:** complete
- **Effort:** 1 hr

## Requirements
- Manual option input: text field + optional image from gallery
- TheMealDB integration: fetch random meals as suggestions
- Option list showing all added options before starting
- Minimum 2 options required to start

## Related Code Files
- **Create:** `app/src/main/java/com/vjpro/tindow/data/api/MealApiService.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/data/api/MealDto.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/data/api/RetrofitClient.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/input/InputScreen.kt` (overwrite scaffold)

## Implementation Steps

### 1. TheMealDB API service
```kotlin
// data/api/MealApiService.kt
interface MealApiService {
    @GET("api/json/v1/1/random.php")
    suspend fun getRandomMeal(): MealResponse

    @GET("api/json/v1/1/filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealsListResponse

    @GET("api/json/v1/1/categories.php")
    suspend fun getCategories(): CategoriesResponse
}
```

Base URL: `https://www.themealdb.com/`

### 2. DTO models
```kotlin
// data/api/MealDto.kt
data class MealResponse(val meals: List<MealDto>?)
data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String?,
    val strCategory: String?,
    val strArea: String?
)
// Extension: MealDto.toOption() -> Option
```

### 3. Retrofit client (singleton object)
```kotlin
// data/api/RetrofitClient.kt
object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    val mealApi: MealApiService = retrofit.create(MealApiService::class.java)
}
```

### 4. InputScreen UI
Layout:
```
┌────────────────────────────────┐
│  "Add Your Options"            │
│                                │
│  [Text Field] [📷] [+ Add]    │
│                                │
│  ── or get suggestions ──      │
│  [🍕 Random Meal] [🎲 x5]     │
│                                │
│  ── Your Options (4) ──        │
│  ┌──────┐ ┌──────┐            │
│  │ Pho  │ │Burger│ ...        │
│  └──────┘ └──────┘            │
│                                │
│  [Start Swiping! →]           │
└────────────────────────────────┘
```

- Text input + add button
- Image picker button (ActivityResultContracts.PickVisualMedia)
- "Random Meal" button → fetch from TheMealDB → add to list
- "x5" button → fetch 5 random meals at once
- Horizontal scrollable list of added options (card preview)
- Each card: tap to remove
- "Start" button enabled when ≥2 options

### 5. Image picker
```kotlin
val pickMedia = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia()
) { uri -> /* save uri for option */ }
```

## Todo
- [x] Create Retrofit client + MealApiService
- [x] Create MealDto + toOption() mapper
- [x] Build InputScreen with manual text+image input
- [x] Add "Random Meal" and "x5" fetch buttons
- [x] Show scrollable option cards list
- [x] Implement remove option on tap
- [x] "Start Swiping" button with ≥2 validation
- [x] Handle API errors gracefully (toast/snackbar)

## Success Criteria
- Can add options manually with text
- Can add options with gallery images
- Can fetch random meals from TheMealDB with images
- Option list displays correctly
- Cannot start with <2 options
