package com.vjpro.tindow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vjpro.tindow.data.local.entity.SuggestionHistoryEntity

@Dao
interface SuggestionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: SuggestionHistoryEntity)

    @Query("SELECT * FROM suggestion_history ORDER BY timestamp DESC")
    suspend fun getAll(): List<SuggestionHistoryEntity>

    @Query("DELETE FROM suggestion_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM suggestion_history")
    suspend fun deleteAll()
}
