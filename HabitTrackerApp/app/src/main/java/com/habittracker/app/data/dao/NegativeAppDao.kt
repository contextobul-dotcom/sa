package com.habittracker.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.habittracker.app.data.entity.NegativeApp
import kotlinx.coroutines.flow.Flow

@Dao
interface NegativeAppDao {
    @Query("SELECT * FROM negative_apps ORDER BY appLabel")
    fun observeAll(): Flow<List<NegativeApp>>

    @Query("SELECT * FROM negative_apps")
    suspend fun getAllOnce(): List<NegativeApp>

    @Upsert
    suspend fun upsert(app: NegativeApp)

    @Delete
    suspend fun delete(app: NegativeApp)
}
