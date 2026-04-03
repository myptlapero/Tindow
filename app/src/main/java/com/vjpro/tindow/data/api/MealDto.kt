package com.vjpro.tindow.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.data.model.OptionSource

@JsonClass(generateAdapter = false)
data class MealResponse(
    @Json(name = "meals") val meals: List<MealDto>?
)

@JsonClass(generateAdapter = false)
data class MealDto(
    @Json(name = "idMeal") val idMeal: String,
    @Json(name = "strMeal") val strMeal: String,
    @Json(name = "strMealThumb") val strMealThumb: String?,
    @Json(name = "strCategory") val strCategory: String?,
    @Json(name = "strArea") val strArea: String?
)

/** Convert API meal to app Option */
fun MealDto.toOption(): Option = Option(
    id = "meal_$idMeal",
    title = strMeal,
    imageUri = strMealThumb,
    description = listOfNotNull(strCategory, strArea).joinToString(" • "),
    source = OptionSource.MEAL_API
)
