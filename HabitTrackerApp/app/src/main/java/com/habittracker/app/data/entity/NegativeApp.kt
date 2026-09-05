package com.habittracker.app.data.entity

import androidx.room.Entity

@Entity(tableName = "negative_apps", primaryKeys = ["packageName"])
data class NegativeApp(
    val packageName: String,
    val appLabel: String,
    val pointsPerMinute: Int
)
