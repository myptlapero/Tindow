# Phase 2: Data Layer + API Services

## Overview
- **Priority:** P0
- **Status:** Complete
- **Effort:** 4h
- **Description:** Domain models, Room DB, Gemini API service, image search service, repositories

## Requirements
- Domain models matching Gemini JSON response structure
- Room database with entities for suggestion history + weekly plan
- Gemini API integration (text + vision) using Google Generative AI SDK
- Image search via Unsplash API
- Repository layer abstracting data sources

## Related Code Files

### Create
- `app/src/main/java/com/vjpro/tindow/domain/model/Meal.kt`
- `app/src/main/java/com/vjpro/tindow/domain/model/Ingredient.kt`
- `app/src/main/java/com/vjpro/tindow/domain/model/CookingStep.kt`
- `app/src/main/java/com/vjpro/tindow/domain/model/NutritionInfo.kt`
- `app/src/main/java/com/vjpro/tindow/domain/model/WeeklyPlan.kt`
- `app/src/main/java/com/vjpro/tindow/domain/model/SuggestionHistory.kt`
- `app/src/main/java/com/vjpro/tindow/data/local/AppDatabase.kt`
- `app/src/main/java/com/vjpro/tindow/data/local/dao/SuggestionHistoryDao.kt`
- `app/src/main/java/com/vjpro/tindow/data/local/dao/WeeklyPlanDao.kt`
- `app/src/main/java/com/vjpro/tindow/data/local/entity/SuggestionHistoryEntity.kt`
- `app/src/main/java/com/vjpro/tindow/data/local/entity/WeeklyPlanEntity.kt`
- `app/src/main/java/com/vjpro/tindow/data/remote/GeminiService.kt`
- `app/src/main/java/com/vjpro/tindow/data/remote/PexelsService.kt`
- `app/src/main/java/com/vjpro/tindow/data/repository/MealRepository.kt`
- `app/src/main/java/com/vjpro/tindow/data/repository/WeeklyPlanRepository.kt`

### Modify
- `app/src/main/java/com/vjpro/tindow/core/di/AppModule.kt` — provide DB, API, repos

## Implementation Steps

### 1. Domain Models

```kotlin
data class Meal(
    val name: String,
    val imageUrl: String = "",
    val calories: Int,
    val difficulty: String,      // "Dễ", "Trung bình", "Khó"
    val cookingTime: Int,        // minutes
    val nutrition: NutritionInfo,
    val ingredients: List<Ingredient>,
    val cookingSteps: List<CookingStep>
)

data class NutritionInfo(
    val calories: Int, val protein: Int, val carbs: Int, val fat: Int
)

data class Ingredient(val name: String, val amount: String)

data class CookingStep(
    val stepNumber: Int, val title: String,
    val description: String, val duration: Int = 0,
    val ingredients: List<Ingredient> = emptyList()
)

data class WeeklyPlan(
    val days: List<DayPlan> // 7 days
)
data class DayPlan(
    val dayName: String,  // "T2", "T3", etc.
    val breakfast: Meal, val lunch: Meal, val dinner: Meal,
    val totalCalories: Int
)

data class SuggestionHistory(
    val id: Long = 0, val query: String,
    val mealNames: List<String>, val timestamp: Long,
    val meals: List<Meal>
)
```

### 2. Room Entities + DAOs

SuggestionHistoryEntity: store query, meal names (JSON string), timestamp, full meals JSON
WeeklyPlanEntity: store full plan as JSON string, week start date

DAOs:
- `SuggestionHistoryDao`: insert, getAll (ordered by timestamp DESC), deleteOld
- `WeeklyPlanDao`: insert/replace, getLatest, delete

Use Gson TypeConverters for List<Meal> ↔ JSON string

### 3. Gemini Service

Use Google Generative AI SDK (`com.google.ai.client.generativeai`):

```kotlin
class GeminiService(private val apiKey: String) {
    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey
    )

    suspend fun suggestMeals(input: String): List<Meal>
    suspend fun suggestMealsFromImage(image: Bitmap, text: String?): List<Meal>
    suspend fun generateWeeklyPlan(): WeeklyPlan
}
```

**Prompt engineering critical:**
- Request JSON response with strict schema
- Vietnamese food context
- Nutrition data must be realistic
- Weekly plan: 7 days × 3 meals, 1800-2200 kcal/day

### 4. Image Search Service

Pexels API via Retrofit (key in `local.properties` as `PEXELS_API_KEY`):
```kotlin
interface PexelsApi {
    @GET("v1/search")
    suspend fun searchPhotos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 1
    ): PexelsResponse
}
```
Base URL: `https://api.pexels.com/`
Fallback: if no results, use placeholder drawable

### 5. Repositories

```kotlin
class MealRepository(
    private val geminiService: GeminiService,
    private val imageSearchService: ImageSearchService,
    private val historyDao: SuggestionHistoryDao
) {
    suspend fun suggestMeals(input: String): Result<List<Meal>>
    suspend fun suggestMealsFromImage(bitmap: Bitmap): Result<List<Meal>>
    suspend fun getHistory(): List<SuggestionHistory>
    // After getting meals from Gemini, fetch images from Unsplash per meal
}
```

### 6. Hilt Module (AppModule)

Provide: Room database, DAOs, GeminiService, UnsplashApi, Repositories

## Todo List
- [x] Create domain models (Meal, Ingredient, CookingStep, NutritionInfo, WeeklyPlan)
- [x] Create Room entities + TypeConverters
- [x] Create AppDatabase with entities
- [x] Create SuggestionHistoryDao
- [x] Create WeeklyPlanDao
- [x] Create GeminiService with prompt engineering
- [x] Create ImageSearchService (Unsplash)
- [x] Create MealRepository
- [x] Create WeeklyPlanRepository
- [x] Update AppModule with all providers
- [x] Verify build compiles

## Success Criteria
- Gemini returns valid JSON parsed into Meal objects
- Room stores/retrieves suggestion history
- Image search returns food photos by meal name
- Repository layer abstracts all data operations

## Risk Assessment
- **Gemini JSON parsing:** Use strict prompt with example JSON, wrap in try-catch with fallback
- **Pexels rate limit:** 200 req/hr on free tier. Cache results, batch requests
- **Gemini Vision bitmap:** Compress before sending to avoid timeout

## Security
- API keys via BuildConfig (not hardcoded)
- Pexels key in local.properties as `PEXELS_API_KEY`
