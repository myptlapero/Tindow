package com.vjpro.tindow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_plan")
data class WeeklyPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planJson: String,
    val weekStartDate: String,
    val createdAt: Long = System.currentTimeMillis()
)
