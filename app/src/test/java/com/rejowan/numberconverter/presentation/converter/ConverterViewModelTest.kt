package com.rejowan.numberconverter.presentation.converter

import app.cash.turbine.test
import com.rejowan.numberconverter.domain.model.ConversionResult
import com.rejowan.numberconverter.domain.model.Explanation
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.repository.ConverterRepository
import com.rejowan.numberconverter.domain.usecase.converter.ConvertNumberUseCase
import com.rejowan.numberconverter.domain.usecase.converter.FormatOutputUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidateInputUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidationResult
import com.rejowan.numberconverter.domain.usecase.history.DeleteHistoryUseCase
import com.rejowan.numberconverter.domain.usecase.history.GetHistoryUseCase
import com.rejowan.numberconverter.domain.usecase.history.SaveConversionUseCase
import com.rejowan.numberconverter.domain.usecase.history.ToggleBookmarkUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    private lateinit var convertNumberUseCase: ConvertNumberUseCase
    private lateinit var validateInputUseCase: ValidateInputUseCase
    private lateinit var formatOutputUseCase: FormatOutputUseCase
    private lateinit var saveConversionUseCase: SaveConversionUseCase
    private lateinit var converterRepository: ConverterRepository
    private lateinit var getHistoryUseCase: GetHistoryUseCase
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase
    private lateinit var deleteHistoryUseCase: DeleteHistoryUseCase
    private lateinit var viewModel: ConverterViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        convertNumberUseCase = mockk()
        validateInputUseCase = mockk()
        formatOutputUseCase = mockk()
        saveConversionUseCase = mockk(relaxed = true)
        converterRepository = mockk()
        getHistoryUseCase = mockk()
        toggleBookmarkUseCase = mockk(relaxed = true)
        deleteHistoryUseCase = mockk(relaxed = true)

        // Default mocks
        every { getHistoryUseCase.invoke() } returns flowOf(emptyList())
        every { getHistoryUseCase.getBookmarked() } returns flowOf(emptyList())

        viewModel = ConverterViewModel(
            convertNumberUseCase,
            validateInputUseCase,
            formatOutputUseCase,
            saveConversionUseCase,
            converterRepository,
            getHistoryUseCase,
            toggleBookmarkUseCase,
            deleteHistoryUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Initial state tests
    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.value

        assertEquals("", state.input)
        assertEquals("", state.output)
        assertEquals(NumberBase.DECIMAL, state.fromBase)
        assertEquals(NumberBase.BINARY, state.toBase)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNull(state.validationError)
        assertNull(state.explanation)
    }

    // State management tests
    @Test
    fun `onFromBaseChanged updates state`() = runTest {
        viewModel.onFromBaseChanged(NumberBase.HEXADECIMAL)

        val state = viewModel.uiState.value
        assertEquals(NumberBase.HEXADECIMAL, state.fromBase)
    }

    @Test
    fun `onToBaseChanged updates state`() = runTest {
        viewModel.onToBaseChanged(NumberBase.OCTAL)

        val state = viewModel.uiState.value
        assertEquals(NumberBase.OCTAL, state.toBase)
    }

    // Conversion flow tests
    @Test
    fun `valid input triggers conversion after debounce`() = runTest {
        val input = "10"
        val expected = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("1010") } returns "1010"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged(input)
        advanceTimeBy(350) // Debounce delay
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("1010", state.output)
        assertNull(state.errorMessage)
        assertNull(state.validationError)
    }

    @Test
    fun `invalid input shows validation error`() = runTest {
        val input = "ABC"
        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(false, "Invalid input for Decimal")

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.output)
        assertEquals("Invalid input for Decimal", state.validationError)
        assertNull(state.errorMessage)
    }

    @Test
    fun `empty input clears output`() = runTest {
        // First set some input
        val input = "10"
        val expected = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)
        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("1010") } returns "1010"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then clear it
        viewModel.onInputChanged("")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.output)
        assertNull(state.errorMessage)
        assertNull(state.validationError)
    }

    @Test
    fun `conversion success updates output`() = runTest {
        val input = "FF"
        val expected = ConversionResult(input, "255", NumberBase.HEXADECIMAL, NumberBase.DECIMAL)

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("255") } returns "255"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("255", state.output)
        assertFalse(state.isLoading)
    }

    @Test
    fun `conversion failure shows error message`() = runTest {
        val input = "10"
        val error = Exception("Conversion failed")

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.failure(error)

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.output)
        assertEquals("Conversion failed", state.errorMessage)
        assertFalse(state.isLoading)
    }

    // Base swapping tests
    @Test
    fun `swapBases swaps fromBase and toBase`() = runTest {
        viewModel.onFromBaseChanged(NumberBase.DECIMAL)
        viewModel.onToBaseChanged(NumberBase.BINARY)

        viewModel.swapBases()

        val state = viewModel.uiState.value
        assertEquals(NumberBase.BINARY, state.fromBase)
        assertEquals(NumberBase.DECIMAL, state.toBase)
    }

    @Test
    fun `swapBases swaps input and output`() = runTest {
        val input = "10"
        val expected = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("1010") } returns "1010"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.swapBases()

        val state = viewModel.uiState.value
        assertEquals("1010", state.input)
        assertEquals("10", state.output)
    }

    // Clear functionality tests
    @Test
    fun `clearInput clears all fields`() = runTest {
        val input = "10"
        val expected = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("1010") } returns "1010"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearInput()

        val state = viewModel.uiState.value
        assertEquals("", state.input)
        assertEquals("", state.output)
        assertNull(state.errorMessage)
        assertNull(state.validationError)
        assertNull(state.explanation)
    }

    // Explanation handling tests
    @Test
    fun `successful conversion fetches explanation`() = runTest {
        val input = "10"
        val expected = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)
        val explanation = mockk<Explanation>()

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("1010") } returns "1010"
        coEvery { converterRepository.explain(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(explanation)

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(explanation, state.explanation)
    }

    @Test
    fun `explanation failure does not break conversion flow`() = runTest {
        val input = "10"
        val expected = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("1010") } returns "1010"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception("Explanation failed"))

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("1010", state.output)
        assertNull(state.explanation)
        assertNull(state.errorMessage) // Should not show explanation error
    }

    // History integration tests
    @Test
    fun `successful conversion saves to history`() = runTest {
        val input = "10"
        val expected = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)

        every { validateInputUseCase.invoke(input, NumberBase.DECIMAL) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)
        every { formatOutputUseCase.invoke("1010") } returns "1010"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            saveConversionUseCase.invoke(match {
                it.input == input && it.output == "1010" &&
                it.fromBase == NumberBase.DECIMAL && it.toBase == NumberBase.BINARY
            })
        }
    }

    @Test
    fun `restoreFromHistory updates state correctly`() = runTest {
        every { validateInputUseCase.invoke(any(), any()) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(any(), any(), any()) } returns Result.success(
            ConversionResult("FF", "255", NumberBase.HEXADECIMAL, NumberBase.DECIMAL)
        )
        every { formatOutputUseCase.invoke(any()) } answers { firstArg() }
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        val historyItem = HistoryItem(
            id = 1,
            input = "FF",
            output = "255",
            fromBase = NumberBase.HEXADECIMAL,
            toBase = NumberBase.DECIMAL,
            timestamp = System.currentTimeMillis()
        )

        viewModel.restoreFromHistory(historyItem)

        val state = viewModel.uiState.value
        assertEquals("FF", state.input)
        assertEquals("255", state.output)
        assertEquals(NumberBase.HEXADECIMAL, state.fromBase)
        assertEquals(NumberBase.DECIMAL, state.toBase)
    }

    @Test
    fun `toggleBookmark calls use case`() = runTest {
        val id = 123L

        viewModel.toggleBookmark(id)
        advanceUntilIdle()

        coVerify { toggleBookmarkUseCase.invoke(id) }
    }

    @Test
    fun `deleteHistoryItem calls use case`() = runTest {
        val item = HistoryItem(
            id = 1,
            input = "10",
            output = "1010",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY,
            timestamp = System.currentTimeMillis()
        )

        viewModel.deleteHistoryItem(item)
        advanceUntilIdle()

        coVerify { deleteHistoryUseCase.invoke(item) }
    }

    @Test
    fun `clearAllHistory calls use case`() = runTest {
        viewModel.clearAllHistory()
        advanceUntilIdle()

        coVerify { deleteHistoryUseCase.deleteAll() }
    }

    // History state flows tests
    // Note: StateIn with WhileSubscribed is difficult to test in unit tests
    // These flows are tested indirectly through integration tests

    // Debouncing tests
    @Test
    fun `input changes debounced by 300ms`() = runTest {
        every { validateInputUseCase.invoke(any(), any()) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(any(), any(), any()) } returns Result.success(
            ConversionResult("1", "1", NumberBase.DECIMAL, NumberBase.BINARY)
        )
        every { formatOutputUseCase.invoke(any()) } returns "1"
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged("1")
        advanceTimeBy(100)
        viewModel.onInputChanged("12")
        advanceTimeBy(100)
        viewModel.onInputChanged("123")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        // Should only call convert once with final value
        coVerify(exactly = 1) { convertNumberUseCase.invoke(any(), any(), any()) }
    }

    @Test
    fun `base change triggers immediate conversion if input exists`() = runTest {
        val input = "10"
        val expected1 = ConversionResult(input, "1010", NumberBase.DECIMAL, NumberBase.BINARY)
        val expected2 = ConversionResult(input, "A", NumberBase.DECIMAL, NumberBase.HEXADECIMAL)

        every { validateInputUseCase.invoke(input, any()) } returns ValidationResult(true)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected1)
        coEvery { convertNumberUseCase.invoke(input, NumberBase.DECIMAL, NumberBase.HEXADECIMAL) } returns Result.success(expected2)
        every { formatOutputUseCase.invoke(any()) } answers { firstArg() }
        coEvery { converterRepository.explain(any(), any(), any()) } returns Result.failure(Exception())

        viewModel.onInputChanged(input)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onToBaseChanged(NumberBase.HEXADECIMAL)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("A", state.output)
    }
}
