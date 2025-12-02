package com.rejowan.numberconverter.domain.repository

import com.rejowan.numberconverter.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryItem>>
    fun getRecentHistory(limit: Int): Flow<List<HistoryItem>>
    fun getBookmarkedHistory(): Flow<List<HistoryItem>>
    fun searchHistory(query: String): Flow<List<HistoryItem>>
    suspend fun insertHistory(item: HistoryItem)
    suspend fun updateHistory(item: HistoryItem)
    suspend fun deleteHistory(item: HistoryItem)
    suspend fun deleteAllHistory()
    suspend fun toggleBookmark(id: Long)
}
