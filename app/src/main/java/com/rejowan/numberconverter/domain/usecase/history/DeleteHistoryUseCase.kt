package com.rejowan.numberconverter.domain.usecase.history

import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.repository.HistoryRepository

class DeleteHistoryUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(item: HistoryItem) {
        repository.deleteHistory(item)
    }

    suspend fun deleteAll() {
        repository.deleteAllHistory()
    }
}
