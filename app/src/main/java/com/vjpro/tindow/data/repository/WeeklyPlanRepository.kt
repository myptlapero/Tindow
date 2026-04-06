package com.vjpro.tindow.data.repository

import android.util.Log
import com.google.gson.Gson
import com.vjpro.tindow.BuildConfig
import com.vjpro.tindow.data.local.dao.WeeklyPlanDao
import com.vjpro.tindow.data.local.entity.WeeklyPlanEntity
import com.vjpro.tindow.data.remote.GeminiService
import com.vjpro.tindow.data.remote.PexelsApi
import com.vjpro.tindow.domain.model.DayPlan
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.domain.model.WeeklyPlan
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "MyPTL"

@Singleton
class WeeklyPlanRepository @Inject constructor(
    private val geminiService: GeminiService,
    private val weeklyPlanDao: WeeklyPlanDao,
    private val pexelsApi: PexelsApi,
    private val gson: Gson
) {
    suspend fun generateWeeklyPlan(): Result<WeeklyPlan> = try {
        Log.d(TAG, "WeeklyPlanRepository.generateWeeklyPlan() called")
        val plan = geminiService.generateWeeklyPlan()
        Log.d(TAG, "WeeklyPlanRepository.generateWeeklyPlan() got ${plan.days.size} days")
        if (plan.days.isNotEmpty()) {
            val planWithImages = fetchImagesForPlan(plan)
            savePlan(planWithImages)
            Log.d(TAG, "WeeklyPlanRepository.generateWeeklyPlan() saved to DB with images")
            Result.success(planWithImages)
        } else {
            Log.w(TAG, "WeeklyPlanRepository.generateWeeklyPlan() skipping save - empty plan")
            Result.success(plan)
        }
    } catch (e: Exception) {
        Log.e(TAG, "WeeklyPlanRepository.generateWeeklyPlan() ERROR: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun getLatestPlan(): WeeklyPlan? {
        Log.d(TAG, "WeeklyPlanRepository.getLatestPlan() called")
        val entity = weeklyPlanDao.getLatest()
        if (entity == null) {
            Log.d(TAG, "WeeklyPlanRepository.getLatestPlan() no plan found in DB")
            return null
        }
        return try {
            val plan = gson.fromJson(entity.planJson, WeeklyPlan::class.java)
            Log.d(TAG, "WeeklyPlanRepository.getLatestPlan() loaded plan with ${plan.days.size} days")
            plan
        } catch (e: Exception) {
            Log.e(TAG, "WeeklyPlanRepository.getLatestPlan() PARSE ERROR: ${e.message}")
            null
        }
    }

    private suspend fun fetchImagesForPlan(plan: WeeklyPlan): WeeklyPlan {
        Log.d(TAG, "WeeklyPlanRepository.fetchImagesForPlan() fetching images for ${plan.days.size * 3} meals")
        val updatedDays = plan.days.map { day ->
            coroutineScope {
                val b = async { fetchImage(day.breakfast) }
                val l = async { fetchImage(day.lunch) }
                val d = async { fetchImage(day.dinner) }
                DayPlan(
                    dayName = day.dayName,
                    breakfast = b.await(),
                    lunch = l.await(),
                    dinner = d.await(),
                    totalCalories = day.totalCalories
                )
            }
        }
        return WeeklyPlan(days = updatedDays)
    }

    private suspend fun fetchImage(meal: Meal): Meal {
        if (meal.imageUrl.isNotBlank()) return meal
        return try {
            val response = pexelsApi.searchPhotos(
                apiKey = BuildConfig.PEXELS_API_KEY,
                query = "${meal.name} food"
            )
            val url = response.photos.firstOrNull()?.src?.medium ?: ""
            meal.copy(imageUrl = url)
        } catch (e: Exception) {
            Log.e(TAG, "WeeklyPlanRepository.fetchImage() error for ${meal.name}: ${e.message}")
            meal
        }
    }

    private suspend fun savePlan(plan: WeeklyPlan) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val entity = WeeklyPlanEntity(
            planJson = gson.toJson(plan),
            weekStartDate = dateFormat.format(Date())
        )
        weeklyPlanDao.insert(entity)
    }
}
