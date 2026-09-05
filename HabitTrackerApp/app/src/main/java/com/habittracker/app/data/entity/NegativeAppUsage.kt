package com.habittracker.app.data.entity

import androidx.room.Entity

@Entity(tableName = "negative_app_usage", primaryKeys = ["packageName", "dateEpochDay"])
data class NegativeAppUsage(
    val packageName: String,
    val dateEpochDay: Long,
    val minutes: Int,
    val points: Int
)
