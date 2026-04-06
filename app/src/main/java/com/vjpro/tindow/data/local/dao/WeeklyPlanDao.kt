package com.vjpro.tindow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vjpro.tindow.data.local.entity.WeeklyPlanEntity

@Dao
interface WeeklyPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: WeeklyPlanEntity)

    @Query("SELECT * FROM weekly_plan ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): WeeklyPlanEntity?

    @Query("DELETE FROM weekly_plan")
    suspend fun deleteAll()
}
