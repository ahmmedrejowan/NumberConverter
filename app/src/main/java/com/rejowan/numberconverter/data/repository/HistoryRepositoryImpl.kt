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

    override suspend fun updateHistory(item: HistoryItem) {
        historyDao.update(item.toEntity())
    }

    override suspend fun deleteHistory(item: HistoryItem) {
        historyDao.delete(item.toEntity())
    }

    override suspend fun deleteAllHistory() {
        historyDao.deleteAll()
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
