package com.rejowan.numberconverter.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rejowan.numberconverter.data.local.database.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: ProgressEntity)

    @Update
    suspend fun update(progress: ProgressEntity)

    @Query("SELECT * FROM lesson_progress")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    fun getProgressByLessonId(lessonId: String): Flow<ProgressEntity?>

    @Query("SELECT COUNT(*) FROM lesson_progress WHERE status = 'COMPLETED'")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT * FROM lesson_progress WHERE status = 'COMPLETED'")
    fun getCompletedProgress(): Flow<List<ProgressEntity>>

    @Query("DELETE FROM lesson_progress WHERE lessonId = :lessonId")
    suspend fun deleteByLessonId(lessonId: String)

    @Query("DELETE FROM lesson_progress")
    suspend fun deleteAll()
}
