package com.vjpro.tindow.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vjpro.tindow.BuildConfig
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.domain.model.WeeklyPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "MyPTL"

@Singleton
class GeminiService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    companion object {
        private const val GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"
        private const val MODEL = "llama-3.3-70b-versatile"
        private const val TIMEOUT_MS = 30_000L
        private const val WEEKLY_PLAN_TIMEOUT_MS = 60_000L

        private const val MEAL_SUGGEST_PROMPT = """
You are a nutrition expert. Suggest 3 dishes based on the following request.
Return JSON array only (no markdown, no explanation):
[{
  "name": "Dish name",
  "calories": 350,
  "difficulty": "Easy",
  "cookingTime": 30,
  "nutrition": {"calories": 350, "protein": 25, "carbs": 40, "fat": 12},
  "ingredients": [{"name": "Ingredient", "amount": "200g"}],
  "cookingSteps": [{"stepNumber": 1, "title": "Step 1", "description": "Description", "duration": 5}]
}]
IMPORTANT: cookingSteps must NOT contain an "ingredients" field.
Request: """

        private const val WEEKLY_PLAN_PROMPT = """
You are a nutrition expert. Create a 7-day meal plan (Mon-Sun), 3 meals per day, 1800-2200 kcal/day.
Return COMPACT JSON only (no markdown, no explanation). Keep it SHORT to avoid truncation.
{"days":[{"dayName":"Mon","breakfast":{"name":"","calories":0,"difficulty":"Easy","cookingTime":10,"nutrition":{"calories":0,"protein":0,"carbs":0,"fat":0},"ingredients":[],"cookingSteps":[]},"lunch":{"name":"","calories":0,"difficulty":"Easy","cookingTime":20,"nutrition":{"calories":0,"protein":0,"carbs":0,"fat":0},"ingredients":[],"cookingSteps":[]},"dinner":{"name":"","calories":0,"difficulty":"Easy","cookingTime":30,"nutrition":{"calories":0,"protein":0,"carbs":0,"fat":0},"ingredients":[],"cookingSteps":[]},"totalCalories":2000}]}
IMPORTANT RULES:
- Return ONLY meal names, calories, difficulty, cookingTime, and nutrition values
- Set ingredients to empty array []
- Set cookingSteps to empty array []
- This keeps the response small enough to avoid truncation
- Use diverse, balanced, nutritious meals"""

        fun getUserFriendlyError(e: Throwable): String = when (e) {
            is UnknownHostException -> "No internet connection"
            is kotlinx.coroutines.TimeoutCancellationException -> "Request timed out, please try again"
            else -> {
                val msg = e.message?.lowercase() ?: ""
                when {
                    msg.contains("api key") || msg.contains("401") -> "Invalid API key"
                    msg.contains("quota") || msg.contains("rate") || msg.contains("429") ->
                        "AI is busy, please try again later"
                    msg.contains("safety") -> "Content not suitable, please try a different request"
                    else -> "Error: ${e.message ?: "Unknown error"}"
                }
            }
        }
    }

    suspend fun suggestMeals(input: String): List<Meal> {
        Log.d(TAG, "GeminiService.suggestMeals() called with input: $input")
        return withTimeout(TIMEOUT_MS) {
            val rawText = callGroq(MEAL_SUGGEST_PROMPT + input, maxTokens = 4096)
            Log.d(TAG, "GeminiService.suggestMeals() raw response: ${rawText.take(500)}")
            val meals = parseMealList(rawText)
            Log.d(TAG, "GeminiService.suggestMeals() parsed ${meals.size} meals")
            meals
        }
    }

    suspend fun generateWeeklyPlan(): WeeklyPlan {
        Log.d(TAG, "GeminiService.generateWeeklyPlan() called")
        return withTimeout(WEEKLY_PLAN_TIMEOUT_MS) {
            val rawText = callGroq(WEEKLY_PLAN_PROMPT, maxTokens = 8192)
            Log.d(TAG, "GeminiService.generateWeeklyPlan() raw response: ${rawText.take(500)}")
            val plan = parseWeeklyPlan(rawText)
            Log.d(TAG, "GeminiService.generateWeeklyPlan() parsed ${plan.days.size} days")
            plan
        }
    }

    private suspend fun callGroq(prompt: String, maxTokens: Int = 4096): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "GeminiService.callGroq() sending request...")
        val requestBody = gson.toJson(
            mapOf(
                "model" to MODEL,
                "messages" to listOf(
                    mapOf("role" to "user", "content" to prompt)
                ),
                "temperature" to 0.7,
                "max_tokens" to maxTokens
            )
        )

        val request = Request.Builder()
            .url(GROQ_URL)
            .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val body = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            Log.e(TAG, "GeminiService.callGroq() HTTP ${response.code}: $body")
            throw RuntimeException("Groq API error ${response.code}: $body")
        }

        val jsonResponse = gson.fromJson(body, Map::class.java)
        val choices = jsonResponse["choices"] as? List<*>
        val firstChoice = choices?.firstOrNull() as? Map<*, *>
        val message = firstChoice?.get("message") as? Map<*, *>
        val content = message?.get("content") as? String ?: ""
        Log.d(TAG, "GeminiService.callGroq() extracted content length: ${content.length}")
        content
    }

    private fun parseMealList(json: String): List<Meal> {
        val cleaned = cleanJson(json)
        return try {
            val type = object : TypeToken<List<Meal>>() {}.type
            val result: List<Meal> = gson.fromJson(cleaned, type) ?: emptyList()
            result
        } catch (e: Exception) {
            Log.e(TAG, "GeminiService.parseMealList() PARSE ERROR: ${e.message}")
            Log.e(TAG, "GeminiService.parseMealList() cleaned json: ${cleaned.take(300)}")
            emptyList()
        }
    }

    private fun parseWeeklyPlan(json: String): WeeklyPlan {
        val cleaned = cleanJson(json)
        return try {
            gson.fromJson(cleaned, WeeklyPlan::class.java) ?: WeeklyPlan()
        } catch (e: Exception) {
            Log.e(TAG, "GeminiService.parseWeeklyPlan() PARSE ERROR: ${e.message}")
            Log.e(TAG, "GeminiService.parseWeeklyPlan() cleaned json: ${cleaned.take(300)}")
            WeeklyPlan()
        }
    }

    private fun cleanJson(json: String): String = json.trim()
        .removePrefix("```json").removePrefix("```")
        .removeSuffix("```").trim()
}
