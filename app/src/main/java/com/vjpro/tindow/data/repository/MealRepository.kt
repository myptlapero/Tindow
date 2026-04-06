package com.vjpro.tindow.data.repository

import android.util.Log
import com.vjpro.tindow.BuildConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import com.vjpro.tindow.data.local.dao.SuggestionHistoryDao
import com.vjpro.tindow.data.local.entity.SuggestionHistoryEntity
import com.vjpro.tindow.data.remote.GeminiService
import com.vjpro.tindow.data.remote.PexelsApi
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.domain.model.SuggestionHistory
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "MyPTL"

@Singleton
class MealRepository @Inject constructor(
    private val geminiService: GeminiService,
    private val pexelsApi: PexelsApi,
    private val historyDao: SuggestionHistoryDao
) {
    suspend fun suggestMeals(input: String): Result<List<Meal>> = try {
        Log.d(TAG, "MealRepository.suggestMeals() input: $input")
        val meals = geminiService.suggestMeals(input)
        Log.d(TAG, "MealRepository.suggestMeals() got ${meals.size} meals, fetching images...")
        val mealsWithImages = fetchImagesParallel(meals)
        Log.d(TAG, "MealRepository.suggestMeals() images fetched, saving history...")
        saveHistory(input, mealsWithImages)
        Log.d(TAG, "MealRepository.suggestMeals() SUCCESS - ${mealsWithImages.size} meals")
        Result.success(mealsWithImages)
    } catch (e: Exception) {
        Log.e(TAG, "MealRepository.suggestMeals() ERROR: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun getHistory(): List<SuggestionHistory> =
        historyDao.getAll().map { it.toDomain() }

    suspend fun deleteHistory(id: Long) = historyDao.deleteById(id)

    suspend fun clearHistory() = historyDao.deleteAll()

    private suspend fun fetchImagesParallel(meals: List<Meal>): List<Meal> = coroutineScope {
        meals.map { meal -> async { fetchImageForMeal(meal) } }.awaitAll()
    }

    private suspend fun fetchImageForMeal(meal: Meal): Meal {
        if (meal.imageUrl.isNotBlank()) return meal
        return try {
            Log.d(TAG, "MealRepository.fetchImageForMeal() searching Pexels for: ${meal.name}")
            val response = pexelsApi.searchPhotos(
                apiKey = BuildConfig.PEXELS_API_KEY,
                query = "${meal.name} food"
            )
            val imageUrl = response.photos.firstOrNull()?.src?.medium ?: ""
            Log.d(TAG, "MealRepository.fetchImageForMeal() result: ${imageUrl.take(80)}")
            meal.copy(imageUrl = imageUrl)
        } catch (e: Exception) {
            Log.e(TAG, "MealRepository.fetchImageForMeal() Pexels ERROR: ${e.message}")
            meal
        }
    }

    private suspend fun saveHistory(query: String, meals: List<Meal>) {
        val entity = SuggestionHistoryEntity(
            query = query,
            mealNames = meals.map { it.name },
            timestamp = System.currentTimeMillis(),
            meals = meals
        )
        historyDao.insert(entity)
        Log.d(TAG, "MealRepository.saveHistory() saved query: $query, meals: ${meals.size}")
    }
}
