package com.habittracker.app.usage

import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.Instant
import java.time.ZoneId

class UsageStatsHelper(private val context: Context) {

    fun getTodayForegroundMinutes(packageNames: List<String>): Map<String, Int> {
        if (packageNames.isEmpty()) return emptyMap()
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val startOfDay = Instant.now()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val now = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDay,
            now
        ) ?: return emptyMap()

        val totalsByPackage = mutableMapOf<String, Long>()
        for (stat in stats) {
            if (stat.packageName in packageNames) {
                totalsByPackage[stat.packageName] =
                    (totalsByPackage[stat.packageName] ?: 0L) + stat.totalTimeInForeground
            }
        }
        return totalsByPackage.mapValues { (_, millis) -> (millis / 60000L).toInt() }
    }
}
