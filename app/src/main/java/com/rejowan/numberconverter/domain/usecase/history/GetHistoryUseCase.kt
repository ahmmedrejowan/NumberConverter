package com.rejowan.numberconverter.domain.usecase.history

import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(
    private val repository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryItem>> {
        return repository.getAllHistory()
    }

    fun getRecent(limit: Int): Flow<List<HistoryItem>> {
        return repository.getRecentHistory(limit)
    }

    fun getBookmarked(): Flow<List<HistoryItem>> {
        return repository.getBookmarkedHistory()
    }

    fun search(query: String): Flow<List<HistoryItem>> {
        return repository.searchHistory(query)
    }
}
