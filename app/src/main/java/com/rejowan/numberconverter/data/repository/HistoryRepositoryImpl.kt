package com.rejowan.numberconverter.data.repository

import com.rejowan.numberconverter.data.local.database.dao.HistoryDao
import com.rejowan.numberconverter.data.local.database.entity.HistoryEntity
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getAllHistory(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistory().map { entities ->
            entities.map { it.toHistoryItem() }
        }
    }

    override fun getRecentHistory(limit: Int): Flow<List<HistoryItem>> {
        return historyDao.getRecentHistory(limit).map { entities ->
            entities.map { it.toHistoryItem() }
        }
    }

    override fun getBookmarkedHistory(): Flow<List<HistoryItem>> {
        return historyDao.getBookmarkedHistory().map { entities ->
            entities.map { it.toHistoryItem() }
        }
    }

    override fun searchHistory(query: String): Flow<List<HistoryItem>> {
        return historyDao.searchHistory(query).map { entities ->
            entities.map { it.toHistoryItem() }
        }
    }

    override suspend fun insertHistory(item: HistoryItem) {
        historyDao.insert(item.toEntity())
    }

    /**
     * Conversion runs on every debounced keystroke, so a naive insert turns
     * typing "1010" into four rows: 1, 10, 101, 1010. Three rules fix that:
     *
     *  1. An identical conversion already in history is moved back to the top
     *     rather than duplicated.
     *  2. If the newest row looks like the same value mid-typing — same bases,
     *     not bookmarked, saved moments ago, and a prefix of what was just
     *     converted — it is replaced instead of kept alongside.
     *  3. Unbookmarked history is capped so it cannot grow without bound.
     *
     * Rule 2 is deliberately narrow. The time window keeps a conversion the user
     * made earlier from being swallowed by a later, longer one, and bookmarked
     * rows are never touched.
     */
    override suspend fun saveConversion(item: HistoryItem) {
        val identical = historyDao.findIdentical(item.input, item.fromBase, item.toBase)
        if (identical != null) {
            historyDao.update(identical.copy(timestamp = item.timestamp))
            return
        }

        val newest = historyDao.getNewest()
        if (newest != null && newest.supersededBy(item)) {
            historyDao.deleteById(newest.id)
        }

        historyDao.insert(item.toEntity())
        historyDao.trimUnbookmarkedTo(MAX_UNBOOKMARKED_HISTORY)
    }

    private fun HistoryEntity.supersededBy(next: HistoryItem): Boolean =
        !isBookmarked &&
            fromBase == next.fromBase &&
            toBase == next.toBase &&
            next.input != input &&
            next.input.startsWith(input) &&
            next.timestamp - timestamp <= TYPING_WINDOW_MS

    companion object {
        /** Newest unbookmarked rows kept; bookmarks are always kept. */
        const val MAX_UNBOOKMARKED_HISTORY = 200

        /**
         * How recently the previous row must have been saved for a longer value
         * to count as "still typing the same number" rather than a new conversion.
         */
        const val TYPING_WINDOW_MS = 30_000L
    }

    override suspend fun updateHistory(item: HistoryItem) {
        historyDao.update(item.toEntity())
    }

    override suspend fun deleteHistory(item: HistoryItem) {
        historyDao.delete(item.toEntity())
    }

    override suspend fun deleteAllHistory() {
        historyDao.deleteAll()
    }

    override suspend fun deleteUnbookmarkedHistory() {
        historyDao.deleteAllUnbookmarked()
    }

    override suspend fun toggleBookmark(id: Long) {
        historyDao.toggleBookmark(id)
    }

    private fun HistoryEntity.toHistoryItem(): HistoryItem {
        return HistoryItem(
            id = id,
            input = input,
            output = output,
            fromBase = fromBase,
            toBase = toBase,
            timestamp = timestamp,
            isBookmarked = isBookmarked
        )
    }

    private fun HistoryItem.toEntity(): HistoryEntity {
        return HistoryEntity(
            id = id,
            input = input,
            output = output,
            fromBase = fromBase,
            toBase = toBase,
            timestamp = timestamp,
            isBookmarked = isBookmarked
        )
    }
}
