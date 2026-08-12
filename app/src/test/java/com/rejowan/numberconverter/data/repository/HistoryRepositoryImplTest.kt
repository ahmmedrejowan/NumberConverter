package com.rejowan.numberconverter.data.repository

import com.rejowan.numberconverter.data.local.database.dao.HistoryDao
import com.rejowan.numberconverter.data.local.database.entity.HistoryEntity
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.model.NumberBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Covers the de-duplication rules in [HistoryRepositoryImpl.saveConversion].
 *
 * Conversion runs on every debounced keystroke, so without these rules typing a
 * value leaves a row for each of its prefixes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HistoryRepositoryImplTest {

    private lateinit var dao: HistoryDao
    private lateinit var repository: HistoryRepositoryImpl

    private val now = 1_000_000L

    private fun entity(
        id: Long = 1,
        input: String,
        output: String = "out",
        from: NumberBase = NumberBase.DECIMAL,
        to: NumberBase = NumberBase.BINARY,
        timestamp: Long = now,
        bookmarked: Boolean = false
    ) = HistoryEntity(id, input, output, from, to, timestamp, bookmarked)

    private fun item(
        input: String,
        output: String = "out",
        from: NumberBase = NumberBase.DECIMAL,
        to: NumberBase = NumberBase.BINARY,
        timestamp: Long = now
    ) = HistoryItem(0, input, output, from, to, timestamp, false)

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = HistoryRepositoryImpl(dao)
        coEvery { dao.findIdentical(any(), any(), any()) } returns null
        coEvery { dao.getNewest() } returns null
    }

    @Test
    fun `an identical conversion is moved to the top instead of duplicated`() = runTest {
        val existing = entity(id = 7, input = "1010", timestamp = now - 60_000)
        coEvery { dao.findIdentical("1010", NumberBase.DECIMAL, NumberBase.BINARY) } returns existing

        repository.saveConversion(item("1010"))

        coVerify(exactly = 1) { dao.update(existing.copy(timestamp = now)) }
        coVerify(exactly = 0) { dao.insert(any()) }
    }

    @Test
    fun `a value still being typed replaces the prefix it supersedes`() = runTest {
        // "101" was saved a moment ago; the user has now typed "1010".
        coEvery { dao.getNewest() } returns entity(id = 3, input = "101", timestamp = now - 500)

        repository.saveConversion(item("1010"))

        coVerify(exactly = 1) { dao.deleteById(3) }
        coVerify(exactly = 1) { dao.insert(any()) }
    }

    @Test
    fun `a bookmarked prefix is never replaced`() = runTest {
        coEvery { dao.getNewest() } returns
            entity(id = 3, input = "101", timestamp = now - 500, bookmarked = true)

        repository.saveConversion(item("1010"))

        coVerify(exactly = 0) { dao.deleteById(any()) }
        coVerify(exactly = 1) { dao.insert(any()) }
    }

    @Test
    fun `an older conversion is kept even when the new value extends it`() = runTest {
        // Same prefix relationship, but far outside the typing window — this was
        // a deliberate earlier conversion, not an in-progress one.
        coEvery { dao.getNewest() } returns entity(id = 3, input = "101", timestamp = now - 120_000)

        repository.saveConversion(item("1010"))

        coVerify(exactly = 0) { dao.deleteById(any()) }
        coVerify(exactly = 1) { dao.insert(any()) }
    }

    @Test
    fun `a prefix converted between different bases is kept`() = runTest {
        coEvery { dao.getNewest() } returns
            entity(id = 3, input = "101", to = NumberBase.HEXADECIMAL, timestamp = now - 500)

        repository.saveConversion(item("1010", to = NumberBase.BINARY))

        coVerify(exactly = 0) { dao.deleteById(any()) }
    }

    @Test
    fun `an unrelated value does not replace the previous one`() = runTest {
        coEvery { dao.getNewest() } returns entity(id = 3, input = "777", timestamp = now - 500)

        repository.saveConversion(item("1010"))

        coVerify(exactly = 0) { dao.deleteById(any()) }
        coVerify(exactly = 1) { dao.insert(any()) }
    }

    @Test
    fun `history is capped after every save`() = runTest {
        repository.saveConversion(item("1010"))

        coVerify(exactly = 1) {
            dao.trimUnbookmarkedTo(HistoryRepositoryImpl.MAX_UNBOOKMARKED_HISTORY)
        }
    }

    @Test
    fun `typing a value one digit at a time leaves a single row`() = runTest {
        // Walk the real sequence: 1 -> 10 -> 101 -> 1010, each replacing the last.
        val inserted = mutableListOf<HistoryEntity>()
        coEvery { dao.insert(capture(inserted)) } returns 1L
        var newest: HistoryEntity? = null
        coEvery { dao.getNewest() } answers { newest }

        listOf("1", "10", "101", "1010").forEachIndexed { i, value ->
            repository.saveConversion(item(value, timestamp = now + i * 200L))
            newest = entity(id = i + 1L, input = value, timestamp = now + i * 200L)
        }

        // Every step after the first superseded its predecessor.
        coVerify(exactly = 3) { dao.deleteById(any()) }
        assert(inserted.map { it.input } == listOf("1", "10", "101", "1010"))
    }
}
