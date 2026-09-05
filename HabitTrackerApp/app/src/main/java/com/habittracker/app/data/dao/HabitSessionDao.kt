package com.habittracker.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.habittracker.app.data.entity.HabitSession
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitSessionDao {
    @Insert
    suspend fun insert(session: HabitSession)

    @Query("SELECT COALESCE(SUM(points), 0) FROM habit_sessions WHERE dateEpochDay = :dateEpochDay")
    fun observeTotalPointsForDay(dateEpochDay: Long): Flow<Int>
}
