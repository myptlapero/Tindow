package com.vjpro.tindow.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vjpro.tindow.domain.model.Meal

class DatabaseConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromMealList(meals: List<Meal>): String = gson.toJson(meals)

    @TypeConverter
    fun toMealList(json: String): List<Meal> {
        val type = object : TypeToken<List<Meal>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun toStringList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
