package com.vjpro.tindow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vjpro.tindow.data.local.converter.DatabaseConverters
import com.vjpro.tindow.data.local.dao.SuggestionHistoryDao
import com.vjpro.tindow.data.local.dao.WeeklyPlanDao
import com.vjpro.tindow.data.local.entity.SuggestionHistoryEntity
import com.vjpro.tindow.data.local.entity.WeeklyPlanEntity

@Database(
    entities = [SuggestionHistoryEntity::class, WeeklyPlanEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun suggestionHistoryDao(): SuggestionHistoryDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao
}
