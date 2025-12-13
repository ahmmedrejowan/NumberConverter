package com.rejowan.numberconverter.domain.usecase.calculator

import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculateUseCaseTest {

    private lateinit var calculateUseCase: CalculateUseCase

    @Before
    fun setUp() {
        calculateUseCase = CalculateUseCase()
    }

    // ==================== Addition Tests ====================

    @Test
    fun `addition of two decimal numbers`() {
        val result = calculateUseCase(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("15", result.getOrNull())
    }

    @Test
    fun `addition of binary numbers with decimal output`() {
        val result = calculateUseCase(
            input1 = "1010",  // 10 in decimal
            input1Base = NumberBase.BINARY,
            input2 = "101",   // 5 in decimal
            input2Base = NumberBase.BINARY,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("15", result.getOrNull())
    }

    @Test
    fun `addition of binary numbers with binary output`() {
        val result = calculateUseCase(
            input1 = "1010",  // 10 in decimal
            input1Base = NumberBase.BINARY,
            input2 = "101",   // 5 in decimal
            input2Base = NumberBase.BINARY,
            operation = Operation.ADD,
            outputBase = NumberBase.BINARY
        )

        assertTrue(result.isSuccess)
        assertEquals("1111", result.getOrNull())  // 15 in binary
    }

    @Test
    fun `addition of different bases - binary plus hexadecimal`() {
        val result = calculateUseCase(
            input1 = "1010",  // 10 in decimal
            input1Base = NumberBase.BINARY,
            input2 = "F",     // 15 in decimal
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("25", result.getOrNull())
    }

    @Test
    fun `addition with fractional values`() {
        val result = calculateUseCase(
            input1 = "10.5",
            input1Base = NumberBase.DECIMAL,
            input2 = "5.5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("16", result.getOrNull())
    }

    @Test
    fun `addition of zeros`() {
        val result = calculateUseCase(
            input1 = "0",
            input1Base = NumberBase.DECIMAL,
            input2 = "0",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("0", result.getOrNull())
    }

    // ==================== Subtraction Tests ====================

    @Test
    fun `subtraction of two decimal numbers`() {
        val result = calculateUseCase(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("5", result.getOrNull())
    }

    @Test
    fun `subtraction resulting in negative number`() {
        val result = calculateUseCase(
            input1 = "5",
            input1Base = NumberBase.DECIMAL,
            input2 = "10",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("-5", result.getOrNull())
    }

    @Test
    fun `subtraction of binary numbers with negative result`() {
        val result = calculateUseCase(
            input1 = "101",   // 5 in decimal
            input1Base = NumberBase.BINARY,
            input2 = "1010",  // 10 in decimal
            input2Base = NumberBase.BINARY,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("-5", result.getOrNull())
    }

    @Test
    fun `subtraction resulting in zero`() {
        val result = calculateUseCase(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "10",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("0", result.getOrNull())
    }

    // ==================== Multiplication Tests ====================

    @Test
    fun `multiplication of two decimal numbers`() {
        val result = calculateUseCase(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("50", result.getOrNull())
    }

    @Test
    fun `multiplication of binary numbers`() {
        val result = calculateUseCase(
            input1 = "1010",  // 10 in decimal
            input1Base = NumberBase.BINARY,
            input2 = "11",    // 3 in decimal
            input2Base = NumberBase.BINARY,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("30", result.getOrNull())
    }

    @Test
    fun `multiplication by zero`() {
        val result = calculateUseCase(
            input1 = "100",
            input1Base = NumberBase.DECIMAL,
            input2 = "0",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("0", result.getOrNull())
    }

    @Test
    fun `multiplication with fractional values`() {
        val result = calculateUseCase(
            input1 = "2.5",
            input1Base = NumberBase.DECIMAL,
            input2 = "4",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("10", result.getOrNull())
    }

    @Test
    fun `multiplication of hex numbers with hex output`() {
        val result = calculateUseCase(
            input1 = "A",     // 10 in decimal
            input1Base = NumberBase.HEXADECIMAL,
            input2 = "2",
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.HEXADECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("14", result.getOrNull())  // 20 in hex is 14
    }

    // ==================== Division Tests ====================

    @Test
    fun `division of two decimal numbers`() {
        val result = calculateUseCase(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "2",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.DIVIDE,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("5", result.getOrNull())
    }

    @Test
    fun `division resulting in fractional value`() {
        val result = calculateUseCase(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "4",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.DIVIDE,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("2.5", result.getOrNull())
    }

    @Test
    fun `division by zero returns error`() {
        val result = calculateUseCase(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "0",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.DIVIDE,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ArithmeticException)
    }

    @Test
    fun `division of binary zero by non-zero`() {
        val result = calculateUseCase(
            input1 = "0",
            input1Base = NumberBase.BINARY,
            input2 = "1010",
            input2Base = NumberBase.BINARY,
            operation = Operation.DIVIDE,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("0", result.getOrNull())
    }

    // ==================== Cross-Base Tests ====================

    @Test
    fun `all different bases - binary input, octal input, hex output`() {
        val result = calculateUseCase(
            input1 = "1000",  // 8 in decimal
            input1Base = NumberBase.BINARY,
            input2 = "10",    // 8 in decimal (octal 10)
            input2Base = NumberBase.OCTAL,
            operation = Operation.ADD,
            outputBase = NumberBase.HEXADECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("10", result.getOrNull())  // 16 in hex is 10
    }

    @Test
    fun `octal subtraction with binary output`() {
        val result = calculateUseCase(
            input1 = "20",    // 16 in decimal
            input1Base = NumberBase.OCTAL,
            input2 = "10",    // 8 in decimal
            input2Base = NumberBase.OCTAL,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.BINARY
        )

        assertTrue(result.isSuccess)
        assertEquals("1000", result.getOrNull())  // 8 in binary
    }

    @Test
    fun `hex multiplication with octal output`() {
        val result = calculateUseCase(
            input1 = "8",
            input1Base = NumberBase.HEXADECIMAL,
            input2 = "2",
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.OCTAL
        )

        assertTrue(result.isSuccess)
        assertEquals("20", result.getOrNull())  // 16 in octal
    }

    // ==================== Edge Cases ====================

    @Test
    fun `large number addition`() {
        val result = calculateUseCase(
            input1 = "999999999",
            input1Base = NumberBase.DECIMAL,
            input2 = "1",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("1000000000", result.getOrNull())
    }

    @Test
    fun `fractional binary addition`() {
        val result = calculateUseCase(
            input1 = "1.1",   // 1.5 in decimal
            input1Base = NumberBase.BINARY,
            input2 = "0.1",   // 0.5 in decimal
            input2Base = NumberBase.BINARY,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("2", result.getOrNull())
    }

    @Test
    fun `lowercase hex input`() {
        val result = calculateUseCase(
            input1 = "ff",
            input1Base = NumberBase.HEXADECIMAL,
            input2 = "1",
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("256", result.getOrNull())
    }

    @Test
    fun `mixed case hex input`() {
        val result = calculateUseCase(
            input1 = "Ff",
            input1Base = NumberBase.HEXADECIMAL,
            input2 = "aA",
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL
        )

        assertTrue(result.isSuccess)
        assertEquals("425", result.getOrNull())  // 255 + 170 = 425
    }

    @Test
    fun `negative result in binary output`() {
        val result = calculateUseCase(
            input1 = "5",
            input1Base = NumberBase.DECIMAL,
            input2 = "10",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.BINARY
        )

        assertTrue(result.isSuccess)
        assertEquals("-101", result.getOrNull())
    }

    @Test
    fun `decimal places are respected in division`() {
        val result = calculateUseCase(
            input1 = "1",
            input1Base = NumberBase.DECIMAL,
            input2 = "3",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.DIVIDE,
            outputBase = NumberBase.DECIMAL,
            decimalPlaces = 5
        )

        assertTrue(result.isSuccess)
        val output = result.getOrNull()!!
        // Should have fractional part with limited precision
        assertTrue(output.startsWith("0.3"))
    }
}
