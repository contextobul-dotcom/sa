package com.habittracker.app.data.repository

import com.habittracker.app.data.dao.HabitSessionDao
import com.habittracker.app.data.dao.PositiveHabitDao
import com.habittracker.app.data.entity.HabitSession
import com.habittracker.app.data.entity.PositiveHabit
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId

class HabitRepository(
    private val habitDao: PositiveHabitDao,
    private val sessionDao: HabitSessionDao
) {
    fun observeHabits(): Flow<List<PositiveHabit>> = habitDao.observeAll()

    fun observeTodayPositivePoints(): Flow<Int> =
        sessionDao.observeTotalPointsForDay(todayEpochDay())

    suspend fun addHabit(name: String, pointsPerMinute: Int) {
        habitDao.insert(PositiveHabit(name = name, pointsPerMinute = pointsPerMinute))
    }

    suspend fun deleteHabit(habit: PositiveHabit) {
        habitDao.delete(habit)
    }

    suspend fun startHabit(habitId: Long) {
        habitDao.setActiveSessionStart(habitId, System.currentTimeMillis())
    }

    suspend fun stopHabit(habitId: Long) {
        val habit = habitDao.getById(habitId) ?: return
        val startTime = habit.activeSessionStart ?: return
        val endTime = System.currentTimeMillis()
        val minutes = ((endTime - startTime) / 60000L).toInt().coerceAtLeast(0)
        val points = minutes * habit.pointsPerMinute
        sessionDao.insert(
            HabitSession(
                habitId = habitId,
                startTime = startTime,
                endTime = endTime,
                minutes = minutes,
                points = points,
                dateEpochDay = todayEpochDay()
            )
        )
        habitDao.setActiveSessionStart(habitId, null)
    }

    private fun todayEpochDay(): Long =
        Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
}
