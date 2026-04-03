package com.vjpro.tindow.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/** Pexels API — free, unlimited requests. Get key at https://www.pexels.com/api/ */
interface PexelsApiService {

    @GET("v1/search")
    suspend fun searchPhotos(
        @Header("Authorization") apiKey: String = PexelsConfig.API_KEY,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 1
    ): PexelsSearchResponse
}

/** API key loaded from BuildConfig (set in local.properties) */
object PexelsConfig {
    val API_KEY: String = com.vjpro.tindow.BuildConfig.PEXELS_API_KEY
    const val BASE_URL = "https://api.pexels.com/"

    fun isConfigured(): Boolean = API_KEY.isNotBlank()
}

@JsonClass(generateAdapter = false)
data class PexelsSearchResponse(
    @Json(name = "photos") val photos: List<PexelsPhoto>?
)

@JsonClass(generateAdapter = false)
data class PexelsPhoto(
    @Json(name = "id") val id: Int,
    @Json(name = "src") val src: PexelsPhotoSrc
)

@JsonClass(generateAdapter = false)
data class PexelsPhotoSrc(
    @Json(name = "medium") val medium: String?,
    @Json(name = "small") val small: String?,
    @Json(name = "landscape") val landscape: String?
)

/** Get the best image URL for card display */
fun PexelsPhoto.getBestImageUrl(): String? = src.medium ?: src.landscape ?: src.small
