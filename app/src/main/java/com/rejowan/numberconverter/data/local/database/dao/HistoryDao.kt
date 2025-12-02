package com.rejowan.numberconverter.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rejowan.numberconverter.data.local.database.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity): Long

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM conversion_history WHERE id = :id")
    suspend fun getHistoryById(id: Long): HistoryEntity?

    @Query("DELETE FROM conversion_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun delete(history: HistoryEntity)

    @Query("DELETE FROM conversion_history")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM conversion_history")
    fun getHistoryCount(): Flow<Int>

    @Query("SELECT * FROM conversion_history WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM conversion_history WHERE input LIKE '%' || :query || '%' OR output LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<HistoryEntity>>

    @Update
    suspend fun update(history: HistoryEntity)

    @Query("UPDATE conversion_history SET isBookmarked = NOT isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Long)
}
