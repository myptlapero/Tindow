package com.vjpro.tindow.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/** TheMealDB API — free, no auth required */
interface MealApiService {

    @GET("api/json/v1/1/random.php")
    suspend fun getRandomMeal(): MealResponse

    @GET("api/json/v1/1/filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealResponse

    @GET("api/json/v1/1/search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse
}
