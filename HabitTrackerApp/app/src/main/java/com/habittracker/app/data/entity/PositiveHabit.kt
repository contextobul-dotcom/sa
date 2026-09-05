package com.habittracker.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "positive_habits")
data class PositiveHabit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val pointsPerMinute: Int,
    val activeSessionStart: Long? = null
)
