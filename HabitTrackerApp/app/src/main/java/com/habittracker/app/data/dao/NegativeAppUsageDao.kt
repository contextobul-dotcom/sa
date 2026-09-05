package com.habittracker.app.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.habittracker.app.data.entity.NegativeAppUsage
import kotlinx.coroutines.flow.Flow

@Dao
interface NegativeAppUsageDao {
    @Upsert
    suspend fun upsert(usage: NegativeAppUsage)

    @Query("SELECT COALESCE(SUM(points), 0) FROM negative_app_usage WHERE dateEpochDay = :dateEpochDay")
    fun observeTotalPointsForDay(dateEpochDay: Long): Flow<Int>

    @Query("SELECT * FROM negative_app_usage WHERE dateEpochDay = :dateEpochDay")
    fun observeForDay(dateEpochDay: Long): Flow<List<NegativeAppUsage>>
}
