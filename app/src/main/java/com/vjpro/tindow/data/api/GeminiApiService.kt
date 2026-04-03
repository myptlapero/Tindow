package com.vjpro.tindow.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.data.model.OptionSource
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/** Gemini API — free 250-1K req/day. Get key at https://aistudio.google.com/apikey */
interface GeminiApiService {

    @POST("v1beta/models/gemini-2.5-flash-lite:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String = GeminiConfig.API_KEY,
        @Body request: GeminiRequest
    ): GeminiResponse
}

/** Replace with your real key from https://aistudio.google.com/apikey */
object GeminiConfig {
    const val API_KEY = "AIzaSyDiZp2DrF0hynXRJQtdy3EvktFph-7vjUU"
    const val BASE_URL = "https://generativelanguage.googleapis.com/"

    fun isConfigured(): Boolean = API_KEY != "YOUR_GEMINI_API_KEY_HERE"
}

// Request models
@JsonClass(generateAdapter = false)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = false)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = false)
data class GeminiPart(
    @Json(name = "text") val text: String
)

// Response models
@JsonClass(generateAdapter = false)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = false)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

/** Build a suggestion prompt for food or places */
fun buildSuggestionPrompt(category: String, count: Int = 5, extraDescription: String? = null): GeminiRequest {
    val descHint = if (!extraDescription.isNullOrBlank()) {
        "\nUser note: \"$extraDescription\". Use this to refine suggestions."
    } else ""
    val prompt = """
        Suggest exactly $count options for "$category".$descHint
        IMPORTANT: All titles and descriptions MUST be in Vietnamese.
        Return ONLY a JSON array of objects with "title" and "description" fields.
        Keep descriptions under 10 words. No markdown, no explanation.
        Example: [{"title":"Phở bò","description":"Món phở truyền thống Việt Nam"}]
    """.trimIndent()

    return GeminiRequest(
        contents = listOf(
            GeminiContent(parts = listOf(GeminiPart(text = prompt)))
        )
    )
}

/** Parse Gemini text response into Options */
fun parseGeminiSuggestions(response: GeminiResponse): List<Option> {
    val text = response.candidates?.firstOrNull()
        ?.content?.parts?.firstOrNull()?.text ?: return emptyList()

    // Extract JSON array from response (may contain markdown backticks)
    val jsonText = text
        .replace("```json", "").replace("```", "")
        .trim()

    return try {
        // Simple manual parsing — avoid adding extra Moshi adapters
        val items = mutableListOf<Option>()
        val regex = Regex("""\{\s*"title"\s*:\s*"([^"]+)"\s*,\s*"description"\s*:\s*"([^"]*)"[^}]*\}""")
        regex.findAll(jsonText).forEach { match ->
            val title = match.groupValues[1]
            val desc = match.groupValues[2]
            items.add(
                Option(
                    id = "ai_${title.hashCode()}",
                    title = title,
                    description = desc.ifEmpty { null },
                    source = OptionSource.AI
                )
            )
        }
        items
    } catch (_: Exception) {
        emptyList()
    }
}
