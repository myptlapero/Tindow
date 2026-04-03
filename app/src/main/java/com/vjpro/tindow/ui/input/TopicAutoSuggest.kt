package com.vjpro.tindow.ui.input

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.vjpro.tindow.data.api.GeoapifyConfig
import com.vjpro.tindow.data.api.GeminiConfig
import com.vjpro.tindow.data.api.PexelsConfig
import com.vjpro.tindow.data.api.RetrofitClient
import com.vjpro.tindow.data.api.buildSuggestionPrompt
import com.vjpro.tindow.data.api.getBestImageUrl
import com.vjpro.tindow.data.api.parseGeminiSuggestions
import com.vjpro.tindow.data.api.toOption
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.data.model.Topic
import com.vjpro.tindow.data.model.TopicApiSource

/**
 * Auto-fetch initial suggestions based on topic selection.
 * Returns a list of Options with images where possible.
 */
suspend fun fetchTopicSuggestions(
    topic: Topic?,
    customTopic: String?,
    context: Context?,
    extraDescription: String? = null
): List<Option> {
    return try {
        val rawOptions = when {
            topic != null -> fetchByApiSource(topic, context, extraDescription)
            customTopic != null -> fetchByGemini(customTopic, extraDescription)
            else -> emptyList()
        }
        // Enrich with Pexels images for options without images
        enrichWithImages(rawOptions)
    } catch (_: Exception) {
        emptyList()
    }
}

private suspend fun fetchByApiSource(topic: Topic, context: Context?, extraDescription: String? = null): List<Option> {
    return when (topic.apiSource) {
        TopicApiSource.MEAL_DB -> fetchMeals()
        TopicApiSource.GEOAPIFY -> fetchPlaces(context)
        TopicApiSource.GEMINI -> fetchByGemini(topic.aiPrompt, extraDescription)
    }
}

private suspend fun fetchMeals(): List<Option> {
    return (1..5).mapNotNull {
        try {
            RetrofitClient.mealApi.getRandomMeal().meals?.firstOrNull()?.toOption()
        } catch (_: Exception) { null }
    }
}

@SuppressLint("MissingPermission")
private suspend fun fetchPlaces(context: Context?): List<Option> {
    if (!GeoapifyConfig.isConfigured() || context == null) {
        return fetchByGemini("fun places to visit nearby")
    }
    return try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: return fetchByGemini("fun places to visit nearby")

        val filter = GeoapifyConfig.circleFilter(location.latitude, location.longitude)
        val response = RetrofitClient.geoapifyApi.getNearbyPlaces(filter = filter, limit = 5)
        response.features?.mapNotNull { it.toOption() } ?: emptyList()
    } catch (_: Exception) {
        fetchByGemini("fun places to visit nearby")
    }
}

private suspend fun fetchByGemini(prompt: String, extraDescription: String? = null): List<Option> {
    if (!GeminiConfig.isConfigured()) return emptyList()
    return try {
        val request = buildSuggestionPrompt(prompt, count = 5, extraDescription = extraDescription)
        val response = RetrofitClient.geminiApi.generateContent(request = request)
        parseGeminiSuggestions(response)
    } catch (_: Exception) {
        emptyList()
    }
}

/** Add Pexels images to options that don't have one */
private suspend fun enrichWithImages(options: List<Option>): List<Option> {
    if (!PexelsConfig.isConfigured()) return options
    return options.map { option ->
        if (option.imageUri != null) return@map option
        try {
            val img = RetrofitClient.pexelsApi
                .searchPhotos(query = option.title)
                .photos?.firstOrNull()?.getBestImageUrl()
            option.copy(imageUri = img)
        } catch (_: Exception) { option }
    }
}
