package com.rejowan.numberconverter.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rejowan.numberconverter.data.local.database.entity.HistoryEntity
import com.rejowan.numberconverter.domain.model.NumberBase
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

    /** Exact same conversion, regardless of when it was made. */
    @Query(
        """
        SELECT * FROM conversion_history
        WHERE input = :input AND fromBase = :fromBase AND toBase = :toBase
        LIMIT 1
        """
    )
    suspend fun findIdentical(input: String, fromBase: NumberBase, toBase: NumberBase): HistoryEntity?

    /** Newest row, used to detect a still-being-typed value it supersedes. */
    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC LIMIT 1")
    suspend fun getNewest(): HistoryEntity?

    /**
     * Caps unbookmarked history at [limit] newest rows. Bookmarked entries are
     * never trimmed — the user asked to keep those.
     */
    @Query(
        """
        DELETE FROM conversion_history
        WHERE isBookmarked = 0 AND id NOT IN (
            SELECT id FROM conversion_history
            WHERE isBookmarked = 0
            ORDER BY timestamp DESC
            LIMIT :limit
        )
        """
    )
    suspend fun trimUnbookmarkedTo(limit: Int)

    @Query("DELETE FROM conversion_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun delete(history: HistoryEntity)

    @Query("DELETE FROM conversion_history")
    suspend fun deleteAll()

    @Query("DELETE FROM conversion_history WHERE isBookmarked = 0")
    suspend fun deleteAllUnbookmarked()

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
