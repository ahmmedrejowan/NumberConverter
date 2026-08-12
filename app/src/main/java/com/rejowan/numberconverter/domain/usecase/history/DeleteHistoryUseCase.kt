package com.rejowan.numberconverter.domain.usecase.history

import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.repository.HistoryRepository

class DeleteHistoryUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(item: HistoryItem) {
        repository.deleteHistory(item)
    }

    /** Clears everything, bookmarks included. */
    suspend fun deleteAll() {
        repository.deleteAllHistory()
    }

    /**
     * Clears history but keeps bookmarked entries — what the history sheet's
     * Clear action promises the user.
     */
    suspend fun deleteUnbookmarked() {
        repository.deleteUnbookmarkedHistory()
    }
}
