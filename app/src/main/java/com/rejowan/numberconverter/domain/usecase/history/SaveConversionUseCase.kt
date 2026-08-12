package com.rejowan.numberconverter.domain.usecase.history

import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.first

class SaveConversionUseCase(
    private val repository: HistoryRepository,
    private val preferencesManager: PreferencesManager
) {
    /**
     * Saves a conversion to history, honouring the "Auto-save History" setting.
     *
     * The check lives here rather than in the caller so the preference cannot be
     * bypassed by a future call site — every path into history goes through this.
     *
     * @return true if the conversion was saved, false if auto-save is off.
     */
    suspend operator fun invoke(item: HistoryItem): Boolean {
        if (!preferencesManager.autoSaveHistory.first()) return false
        repository.insertHistory(item)
        return true
    }
}
