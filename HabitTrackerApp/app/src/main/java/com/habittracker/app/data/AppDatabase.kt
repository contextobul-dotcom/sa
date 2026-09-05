package com.habittracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habittracker.app.data.dao.HabitSessionDao
import com.habittracker.app.data.dao.NegativeAppDao
import com.habittracker.app.data.dao.NegativeAppUsageDao
import com.habittracker.app.data.dao.PositiveHabitDao
import com.habittracker.app.data.entity.HabitSession
import com.habittracker.app.data.entity.NegativeApp
import com.habittracker.app.data.entity.NegativeAppUsage
import com.habittracker.app.data.entity.PositiveHabit

@Database(
    entities = [PositiveHabit::class, HabitSession::class, NegativeApp::class, NegativeAppUsage::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun positiveHabitDao(): PositiveHabitDao
    abstract fun habitSessionDao(): HabitSessionDao
    abstract fun negativeAppDao(): NegativeAppDao
    abstract fun negativeAppUsageDao(): NegativeAppUsageDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habit_tracker.db"
                ).build().also { instance = it }
            }
    }
}
