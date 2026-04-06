package com.vjpro.tindow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.domain.model.SuggestionHistory

@Entity(tableName = "suggestion_history")
data class SuggestionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val mealNames: List<String>,
    val timestamp: Long,
    val meals: List<Meal>
) {
    fun toDomain() = SuggestionHistory(
        id = id, query = query,
        mealNames = mealNames, timestamp = timestamp, meals = meals
    )

    companion object {
        fun fromDomain(domain: SuggestionHistory) = SuggestionHistoryEntity(
            id = domain.id, query = domain.query,
            mealNames = domain.mealNames, timestamp = domain.timestamp,
            meals = domain.meals
        )
    }
}
