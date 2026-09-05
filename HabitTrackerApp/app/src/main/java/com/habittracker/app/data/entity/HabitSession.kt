package com.habittracker.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_sessions")
data class HabitSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val startTime: Long,
    val endTime: Long,
    val minutes: Int,
    val points: Int,
    val dateEpochDay: Long
)
