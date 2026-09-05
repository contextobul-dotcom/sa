package com.habittracker.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.habittracker.app.data.entity.PositiveHabit
import kotlinx.coroutines.flow.Flow

@Dao
interface PositiveHabitDao {
    @Query("SELECT * FROM positive_habits ORDER BY name")
    fun observeAll(): Flow<List<PositiveHabit>>

    @Insert
    suspend fun insert(habit: PositiveHabit): Long

    @Update
    suspend fun update(habit: PositiveHabit)

    @Delete
    suspend fun delete(habit: PositiveHabit)

    @Query("UPDATE positive_habits SET activeSessionStart = :startTime WHERE id = :habitId")
    suspend fun setActiveSessionStart(habitId: Long, startTime: Long?)

    @Query("SELECT * FROM positive_habits WHERE id = :habitId")
    suspend fun getById(habitId: Long): PositiveHabit?
}
