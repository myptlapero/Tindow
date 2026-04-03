package com.vjpro.tindow.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/** Singleton Retrofit clients for all external APIs */
object RetrofitClient {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private fun buildRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    /** TheMealDB — free, no auth */
    val mealApi: MealApiService = buildRetrofit("https://www.themealdb.com/")
        .create(MealApiService::class.java)

    /** Pexels — free, unlimited, needs API key */
    val pexelsApi: PexelsApiService = buildRetrofit(PexelsConfig.BASE_URL)
        .create(PexelsApiService::class.java)

    /** Geoapify — free 3K credits/day, needs API key */
    val geoapifyApi: GeoapifyApiService = buildRetrofit(GeoapifyConfig.BASE_URL)
        .create(GeoapifyApiService::class.java)

    /** Gemini AI — free 250-1K req/day, needs API key */
    val geminiApi: GeminiApiService = buildRetrofit(GeminiConfig.BASE_URL)
        .create(GeminiApiService::class.java)
}
