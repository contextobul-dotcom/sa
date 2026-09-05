package com.habittracker.app

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.habittracker.app.data.AppDatabase
import com.habittracker.app.data.repository.HabitRepository
import com.habittracker.app.data.repository.UsageRepository
import com.habittracker.app.usage.UsageStatsHelper
import com.habittracker.app.usage.UsageSyncWorker
import java.util.concurrent.TimeUnit

class AppContainer(app: Application) {
    private val database = AppDatabase.getInstance(app)

    val habitRepository = HabitRepository(database.positiveHabitDao(), database.habitSessionDao())

    val usageRepository = UsageRepository(
        database.negativeAppDao(),
        database.negativeAppUsageDao(),
        UsageStatsHelper(app)
    )
}

class HabitTrackerApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        scheduleUsageSync()
    }

    private fun scheduleUsageSync() {
        val request = PeriodicWorkRequestBuilder<UsageSyncWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "usage_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
