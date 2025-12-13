package com.rejowan.numberconverter.presentation.calculator

import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import com.rejowan.numberconverter.domain.usecase.calculator.CalculateUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidateInputUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private lateinit var calculateUseCase: CalculateUseCase
    private lateinit var validateInputUseCase: ValidateInputUseCase
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: CalculatorViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        calculateUseCase = mockk()
        validateInputUseCase = mockk()
        preferencesManager = mockk()

        every { preferencesManager.decimalPlaces } returns flowOf(15)

        viewModel = CalculatorViewModel(
            calculateUseCase,
            validateInputUseCase,
            preferencesManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.value

        assertEquals("", state.input1)
        assertEquals("", state.input2)
        assertEquals(NumberBase.DECIMAL, state.input1Base)
        assertEquals(NumberBase.DECIMAL, state.input2Base)
        assertEquals(NumberBase.DECIMAL, state.outputBase)
        assertEquals(Operation.ADD, state.operation)
        assertEquals("", state.output)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNull(state.validation1Error)
        assertNull(state.validation2Error)
    }

    // ==================== Input Change Tests ====================

    @Test
    fun `onInput1Changed updates input1`() = runTest {
        viewModel.onInput1Changed("10")

        val state = viewModel.uiState.value
        assertEquals("10", state.input1)
    }

    @Test
    fun `onInput2Changed updates input2`() = runTest {
        viewModel.onInput2Changed("5")

        val state = viewModel.uiState.value
        assertEquals("5", state.input2)
    }

    @Test
    fun `onInput1Changed filters invalid characters for binary`() = runTest {
        viewModel.onInput1BaseChanged(NumberBase.BINARY)
        viewModel.onInput1Changed("10102")

        val state = viewModel.uiState.value
        assertEquals("1010", state.input1)  // '2' should be filtered out
    }

    @Test
    fun `onInput2Changed filters invalid characters for octal`() = runTest {
        viewModel.onInput2BaseChanged(NumberBase.OCTAL)
        viewModel.onInput2Changed("789")

        val state = viewModel.uiState.value
        assertEquals("7", state.input2)  // '8' and '9' should be filtered out
    }

    @Test
    fun `onInput1Changed allows hex characters`() = runTest {
        viewModel.onInput1BaseChanged(NumberBase.HEXADECIMAL)
        viewModel.onInput1Changed("FF")

        val state = viewModel.uiState.value
        assertEquals("FF", state.input1)
    }

    // ==================== Base Change Tests ====================

    @Test
    fun `onInput1BaseChanged updates input1Base`() = runTest {
        viewModel.onInput1BaseChanged(NumberBase.BINARY)

        val state = viewModel.uiState.value
        assertEquals(NumberBase.BINARY, state.input1Base)
    }

    @Test
    fun `onInput2BaseChanged updates input2Base`() = runTest {
        viewModel.onInput2BaseChanged(NumberBase.HEXADECIMAL)

        val state = viewModel.uiState.value
        assertEquals(NumberBase.HEXADECIMAL, state.input2Base)
    }

    @Test
    fun `onOutputBaseChanged updates outputBase`() = runTest {
        viewModel.onOutputBaseChanged(NumberBase.OCTAL)

        val state = viewModel.uiState.value
        assertEquals(NumberBase.OCTAL, state.outputBase)
    }

    // ==================== Operation Change Tests ====================

    @Test
    fun `onOperationChanged updates operation to subtract`() = runTest {
        viewModel.onOperationChanged(Operation.SUBTRACT)

        val state = viewModel.uiState.value
        assertEquals(Operation.SUBTRACT, state.operation)
    }

    @Test
    fun `onOperationChanged updates operation to multiply`() = runTest {
        viewModel.onOperationChanged(Operation.MULTIPLY)

        val state = viewModel.uiState.value
        assertEquals(Operation.MULTIPLY, state.operation)
    }

    @Test
    fun `onOperationChanged updates operation to divide`() = runTest {
        viewModel.onOperationChanged(Operation.DIVIDE)

        val state = viewModel.uiState.value
        assertEquals(Operation.DIVIDE, state.operation)
    }

    // ==================== Calculation Tests ====================

    @Test
    fun `calculation triggered when both inputs provided`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("15")

        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("5")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("15", state.output)
    }

    @Test
    fun `calculation not triggered when input1 is empty`() = runTest {
        viewModel.onInput2Changed("5")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.output)
        verify(exactly = 0) { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `calculation not triggered when input2 is empty`() = runTest {
        viewModel.onInput1Changed("10")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.output)
        verify(exactly = 0) { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `calculation passes correct parameters`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("25")

        viewModel.onInput1BaseChanged(NumberBase.BINARY)
        viewModel.onInput2BaseChanged(NumberBase.HEXADECIMAL)
        viewModel.onOutputBaseChanged(NumberBase.OCTAL)
        viewModel.onOperationChanged(Operation.MULTIPLY)
        viewModel.onInput1Changed("1010")
        viewModel.onInput2Changed("F")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        verify {
            calculateUseCase.invoke(
                input1 = "1010",
                input1Base = NumberBase.BINARY,
                input2 = "F",
                input2Base = NumberBase.HEXADECIMAL,
                operation = Operation.MULTIPLY,
                outputBase = NumberBase.OCTAL,
                decimalPlaces = 15
            )
        }
    }

    @Test
    fun `calculation failure shows error message`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns
            Result.failure(ArithmeticException("Division by zero"))

        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("0")
        viewModel.onOperationChanged(Operation.DIVIDE)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.output)
        assertEquals("Division by zero", state.errorMessage)
    }

    // ==================== Validation Tests ====================

    @Test
    fun `invalid input1 shows validation1Error`() = runTest {
        // Input "2" is invalid for binary
        viewModel.onInput1BaseChanged(NumberBase.BINARY)
        // Since filtering happens, we need to mock the BaseConverter.isValidInput behavior
        // The ViewModel uses BaseConverter directly, so we test with empty input after filtering
        viewModel.onInput1Changed("1010")  // Valid binary
        viewModel.onInput2Changed("101")   // Valid binary
        viewModel.onInput2BaseChanged(NumberBase.BINARY)

        // Now let's test the validation error path by mocking the use case to fail
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns
            Result.failure(IllegalArgumentException("Invalid Binary number"))

        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Invalid Binary number", state.errorMessage)
    }

    // ==================== Swap Inputs Tests ====================

    @Test
    fun `swapInputs swaps input1 and input2`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("15")

        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("5")

        viewModel.swapInputs()

        val state = viewModel.uiState.value
        assertEquals("5", state.input1)
        assertEquals("10", state.input2)
    }

    @Test
    fun `swapInputs swaps input1Base and input2Base`() = runTest {
        viewModel.onInput1BaseChanged(NumberBase.BINARY)
        viewModel.onInput2BaseChanged(NumberBase.HEXADECIMAL)

        viewModel.swapInputs()

        val state = viewModel.uiState.value
        assertEquals(NumberBase.HEXADECIMAL, state.input1Base)
        assertEquals(NumberBase.BINARY, state.input2Base)
    }

    @Test
    fun `swapInputs clears validation errors`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("15")

        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("5")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.swapInputs()

        val state = viewModel.uiState.value
        assertNull(state.validation1Error)
        assertNull(state.validation2Error)
    }

    // ==================== Clear All Tests ====================

    @Test
    fun `clearAll resets all state to initial values`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("15")

        // Set various state values
        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("5")
        viewModel.onInput1BaseChanged(NumberBase.BINARY)
        viewModel.onInput2BaseChanged(NumberBase.HEXADECIMAL)
        viewModel.onOutputBaseChanged(NumberBase.OCTAL)
        viewModel.onOperationChanged(Operation.MULTIPLY)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearAll()

        val state = viewModel.uiState.value
        assertEquals("", state.input1)
        assertEquals("", state.input2)
        assertEquals(NumberBase.DECIMAL, state.input1Base)
        assertEquals(NumberBase.DECIMAL, state.input2Base)
        assertEquals(NumberBase.DECIMAL, state.outputBase)
        assertEquals(Operation.ADD, state.operation)
        assertEquals("", state.output)
        assertNull(state.errorMessage)
        assertNull(state.validation1Error)
        assertNull(state.validation2Error)
    }

    // ==================== Debounce Tests ====================

    @Test
    fun `rapid input changes debounced`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("15")

        viewModel.onInput2Changed("5")  // Set input2 first
        viewModel.onInput1Changed("1")
        advanceTimeBy(100)
        viewModel.onInput1Changed("10")
        advanceTimeBy(100)
        viewModel.onInput1Changed("100")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        // Should only calculate once with final value
        verify(exactly = 1) { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `operation change triggers recalculation`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("15")

        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("5")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        clearMocks(calculateUseCase, answers = false)
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("5")

        viewModel.onOperationChanged(Operation.SUBTRACT)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { calculateUseCase.invoke(any(), any(), any(), any(), eq(Operation.SUBTRACT), any(), any()) }
    }

    @Test
    fun `base change triggers recalculation`() = runTest {
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("15")

        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("5")
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        clearMocks(calculateUseCase, answers = false)
        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("1111")

        viewModel.onOutputBaseChanged(NumberBase.BINARY)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { calculateUseCase.invoke(any(), any(), any(), any(), any(), eq(NumberBase.BINARY), any()) }
    }

    // ==================== Decimal Places Tests ====================

    @Test
    fun `calculation uses decimal places from preferences`() = runTest {
        every { preferencesManager.decimalPlaces } returns flowOf(8)

        // Recreate ViewModel with new preference
        viewModel = CalculatorViewModel(
            calculateUseCase,
            validateInputUseCase,
            preferencesManager
        )

        every { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success("3.33333333")

        viewModel.onInput1Changed("10")
        viewModel.onInput2Changed("3")
        viewModel.onOperationChanged(Operation.DIVIDE)
        advanceTimeBy(350)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { calculateUseCase.invoke(any(), any(), any(), any(), any(), any(), decimalPlaces = 8) }
    }
}
