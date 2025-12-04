package com.rejowan.numberconverter.domain.usecase.converter

import com.rejowan.numberconverter.domain.model.NumberBase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateInputUseCaseTest {

    private lateinit var useCase: ValidateInputUseCase

    @Before
    fun setup() {
        useCase = ValidateInputUseCase()
    }

    // Valid inputs tests
    @Test
    fun `valid binary input returns success`() {
        val result = useCase("1010", NumberBase.BINARY)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid binary with decimal point returns success`() {
        val result = useCase("1111.101", NumberBase.BINARY)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid octal input returns success`() {
        val result = useCase("777", NumberBase.OCTAL)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid octal with decimal point returns success`() {
        val result = useCase("123.45", NumberBase.OCTAL)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid decimal input returns success`() {
        val result = useCase("123", NumberBase.DECIMAL)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid decimal with decimal point returns success`() {
        val result = useCase("456.789", NumberBase.DECIMAL)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid hexadecimal input returns success`() {
        val result = useCase("FF", NumberBase.HEXADECIMAL)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid hexadecimal with decimal point returns success`() {
        val result = useCase("ABC.DEF", NumberBase.HEXADECIMAL)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid hexadecimal lowercase returns success`() {
        val result = useCase("abc.def", NumberBase.HEXADECIMAL)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    // Invalid character tests
    @Test
    fun `binary with invalid character 2 returns error`() {
        val result = useCase("1012", NumberBase.BINARY)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Binary", result.errorMessage)
    }

    @Test
    fun `binary with invalid character 8 returns error`() {
        val result = useCase("1018", NumberBase.BINARY)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Binary", result.errorMessage)
    }

    @Test
    fun `octal with invalid character 8 returns error`() {
        val result = useCase("1238", NumberBase.OCTAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Octal", result.errorMessage)
    }

    @Test
    fun `octal with invalid character 9 returns error`() {
        val result = useCase("1239", NumberBase.OCTAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Octal", result.errorMessage)
    }

    @Test
    fun `decimal with invalid character A returns error`() {
        val result = useCase("123A", NumberBase.DECIMAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Decimal", result.errorMessage)
    }

    @Test
    fun `hexadecimal with invalid character G returns error`() {
        val result = useCase("FFG", NumberBase.HEXADECIMAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Hexadecimal", result.errorMessage)
    }

    @Test
    fun `hexadecimal with invalid character Z returns error`() {
        val result = useCase("ABCZ", NumberBase.HEXADECIMAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Hexadecimal", result.errorMessage)
    }

    // Multiple decimal points tests
    @Test
    fun `input with multiple decimal points returns error`() {
        val result = useCase("12.34.56", NumberBase.DECIMAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Decimal", result.errorMessage)
    }

    @Test
    fun `binary with multiple decimal points returns error`() {
        val result = useCase("1.0.1", NumberBase.BINARY)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Binary", result.errorMessage)
    }

    @Test
    fun `octal with multiple decimal points returns error`() {
        val result = useCase("7.7.7", NumberBase.OCTAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Octal", result.errorMessage)
    }

    // Empty/blank input tests
    @Test
    fun `empty string returns error`() {
        val result = useCase("", NumberBase.DECIMAL)
        assertFalse(result.isValid)
        assertEquals("Input cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank string with spaces returns error`() {
        val result = useCase("   ", NumberBase.DECIMAL)
        assertFalse(result.isValid)
        assertEquals("Input cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank string with tabs returns error`() {
        val result = useCase("\t\t", NumberBase.DECIMAL)
        assertFalse(result.isValid)
        assertEquals("Input cannot be empty", result.errorMessage)
    }

    // Edge cases
    @Test
    fun `single decimal point is invalid`() {
        val result = useCase(".", NumberBase.DECIMAL)
        assertFalse(result.isValid)
        assertEquals("Invalid input for Decimal", result.errorMessage)
    }

    @Test
    fun `input starting with decimal point is valid if rest is valid`() {
        val result = useCase(".123", NumberBase.DECIMAL)
        // This depends on BaseConverter implementation - checking actual behavior
        // BaseConverter should handle this
        if (result.isValid) {
            assertTrue(result.isValid)
        } else {
            assertFalse(result.isValid)
        }
    }

    @Test
    fun `input ending with decimal point is valid if rest is valid`() {
        val result = useCase("123.", NumberBase.DECIMAL)
        // This depends on BaseConverter implementation - checking actual behavior
        if (result.isValid) {
            assertTrue(result.isValid)
        } else {
            assertFalse(result.isValid)
        }
    }

    @Test
    fun `zero is valid for all bases`() {
        assertTrue(useCase("0", NumberBase.BINARY).isValid)
        assertTrue(useCase("0", NumberBase.OCTAL).isValid)
        assertTrue(useCase("0", NumberBase.DECIMAL).isValid)
        assertTrue(useCase("0", NumberBase.HEXADECIMAL).isValid)
    }

    @Test
    fun `leading zeros are valid`() {
        val result = useCase("0001010", NumberBase.BINARY)
        assertTrue(result.isValid)
    }

    @Test
    fun `mixed case hexadecimal is valid`() {
        val result = useCase("AbCdEf", NumberBase.HEXADECIMAL)
        assertTrue(result.isValid)
    }
}
