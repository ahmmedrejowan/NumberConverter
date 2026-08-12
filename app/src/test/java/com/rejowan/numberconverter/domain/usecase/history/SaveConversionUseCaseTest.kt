package com.rejowan.numberconverter.domain.usecase.history

import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.repository.HistoryRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveConversionUseCaseTest {

    private lateinit var repository: HistoryRepository
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var useCase: SaveConversionUseCase

    private val item = HistoryItem(
        input = "255",
        output = "FF",
        fromBase = NumberBase.DECIMAL,
        toBase = NumberBase.HEXADECIMAL,
        timestamp = 1_000L
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        preferencesManager = mockk()
        useCase = SaveConversionUseCase(repository, preferencesManager)
    }

    @Test
    fun `saves the conversion when auto-save is on`() = runTest {
        every { preferencesManager.autoSaveHistory } returns flowOf(true)

        val saved = useCase(item)

        assertTrue(saved)
        coVerify(exactly = 1) { repository.saveConversion(item) }
    }

    @Test
    fun `does not save when auto-save is off`() = runTest {
        every { preferencesManager.autoSaveHistory } returns flowOf(false)

        val saved = useCase(item)

        assertFalse(saved)
        coVerify(exactly = 0) { repository.saveConversion(any()) }
    }

    @Test
    fun `reads the preference on every call so toggling takes effect immediately`() = runTest {
        // A cached value would keep writing history after the user turns it off.
        every { preferencesManager.autoSaveHistory } returns flowOf(true)
        useCase(item)

        every { preferencesManager.autoSaveHistory } returns flowOf(false)
        useCase(item)

        coVerify(exactly = 1) { repository.saveConversion(item) }
    }
}
