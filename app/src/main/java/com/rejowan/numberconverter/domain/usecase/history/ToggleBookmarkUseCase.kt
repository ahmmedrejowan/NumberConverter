package com.rejowan.numberconverter.domain.usecase.history

import com.rejowan.numberconverter.domain.repository.HistoryRepository

class ToggleBookmarkUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.toggleBookmark(id)
    }
}
