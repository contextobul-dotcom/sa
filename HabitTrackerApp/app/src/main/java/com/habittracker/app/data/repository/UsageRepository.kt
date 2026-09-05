package com.habittracker.app.data.repository

import com.habittracker.app.data.dao.NegativeAppDao
import com.habittracker.app.data.dao.NegativeAppUsageDao
import com.habittracker.app.data.entity.NegativeApp
import com.habittracker.app.data.entity.NegativeAppUsage
import com.habittracker.app.usage.UsageStatsHelper
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId

class UsageRepository(
    private val negativeAppDao: NegativeAppDao,
    private val usageDao: NegativeAppUsageDao,
    private val usageStatsHelper: UsageStatsHelper
) {
    fun observeNegativeApps(): Flow<List<NegativeApp>> = negativeAppDao.observeAll()

    fun observeTodayNegativePoints(): Flow<Int> =
        usageDao.observeTotalPointsForDay(todayEpochDay())

    fun observeTodayUsage(): Flow<List<NegativeAppUsage>> =
        usageDao.observeForDay(todayEpochDay())

    suspend fun addNegativeApp(packageName: String, appLabel: String, pointsPerMinute: Int) {
        negativeAppDao.upsert(NegativeApp(packageName, appLabel, pointsPerMinute))
    }

    suspend fun removeNegativeApp(app: NegativeApp) {
        negativeAppDao.delete(app)
    }

    suspend fun syncTodayUsage() {
        val trackedApps = negativeAppDao.getAllOnce()
        if (trackedApps.isEmpty()) return
        val today = todayEpochDay()
        val usageMinutesByPackage = usageStatsHelper.getTodayForegroundMinutes(
            trackedApps.map { it.packageName }
        )
        trackedApps.forEach { app ->
            val minutes = usageMinutesByPackage[app.packageName] ?: 0
            usageDao.upsert(
                NegativeAppUsage(
                    packageName = app.packageName,
                    dateEpochDay = today,
                    minutes = minutes,
                    points = minutes * app.pointsPerMinute
                )
            )
        }
    }

    private fun todayEpochDay(): Long =
        Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
}
