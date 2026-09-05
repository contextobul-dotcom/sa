package com.habittracker.app.usage

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habittracker.app.HabitTrackerApplication

class UsageSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!UsagePermissionHelper.hasUsageAccess(applicationContext)) {
            return Result.success()
        }
        val container = (applicationContext as HabitTrackerApplication).container
        container.usageRepository.syncTodayUsage()
        return Result.success()
    }
}
