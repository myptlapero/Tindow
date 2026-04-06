package com.vjpro.tindow.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class PexelsResponse(val photos: List<PexelsPhoto> = emptyList())
data class PexelsPhoto(val src: PexelsSrc = PexelsSrc())
data class PexelsSrc(val medium: String = "", val large: String = "")

interface PexelsApi {
    @GET("v1/search")
    suspend fun searchPhotos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 1
    ): PexelsResponse
}
