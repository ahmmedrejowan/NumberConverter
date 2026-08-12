package com.rejowan.numberconverter.domain.usecase.converter

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FormatOutputUseCaseTest {

    private lateinit var useCase: FormatOutputUseCase

    @Before
    fun setup() {
        useCase = FormatOutputUseCase()
    }

    // Strip trailing zeros tests
    @Test
    fun `strips trailing zeros from fractional part`() {
        val result = useCase("123.4500")
        assertEquals("123.45", result)
    }

    @Test
    fun `strips all zeros after decimal point`() {
        val result = useCase("10.0000")
        assertEquals("10", result)
    }

    @Test
    fun `strips trailing zeros but keeps significant digits`() {
        val result = useCase("0.1000")
        assertEquals("0.1", result)
    }

    @Test
    fun `strips trailing zeros from small decimal`() {
        val result = useCase("0.00100")
        assertEquals("0.001", result)
    }

    @Test
    fun `does not strip non-trailing zeros`() {
        val result = useCase("10.01")
        assertEquals("10.01", result)
    }

    @Test
    fun `handles single trailing zero`() {
        val result = useCase("123.50")
        assertEquals("123.5", result)
    }

    // Max decimal places limit tests
    @Test
    fun `limits decimal places to specified maximum`() {
        val result = useCase("123.456789", maxDecimalPlaces = 4)
        assertEquals("123.4567", result)
    }

    @Test
    fun `limits long fractional part`() {
        val result = useCase("10.123456789", maxDecimalPlaces = 6)
        assertEquals("10.123456", result)
    }

    @Test
    fun `limits and strips trailing zeros`() {
        val result = useCase("10.123000000", maxDecimalPlaces = 6)
        assertEquals("10.123", result)
    }

    @Test
    fun `does not modify if within limit`() {
        val result = useCase("123.45", maxDecimalPlaces = 5)
        assertEquals("123.45", result)
    }

    @Test
    fun `limits to zero decimal places removes fractional part`() {
        val result = useCase("123.456", maxDecimalPlaces = 0)
        assertEquals("123", result)
    }

    @Test
    fun `limits to 1 decimal place`() {
        val result = useCase("123.456", maxDecimalPlaces = 1)
        assertEquals("123.4", result)
    }

    // No decimal point tests
    @Test
    fun `leaves integer unchanged`() {
        val result = useCase("1010")
        assertEquals("1010", result)
    }

    @Test
    fun `leaves large integer unchanged`() {
        val result = useCase("255")
        assertEquals("255", result)
    }

    @Test
    fun `leaves zero unchanged`() {
        val result = useCase("0")
        assertEquals("0", result)
    }

    @Test
    fun `integer with max decimal places unchanged`() {
        val result = useCase("1010", maxDecimalPlaces = 5)
        assertEquals("1010", result)
    }

    // All zeros after decimal tests
    @Test
    fun `removes decimal point when all zeros`() {
        val result = useCase("123.000")
        assertEquals("123", result)
    }

    @Test
    fun `removes decimal point from zero`() {
        val result = useCase("0.000")
        assertEquals("0", result)
    }

    @Test
    fun `removes decimal point from large number`() {
        val result = useCase("999.0000")
        assertEquals("999", result)
    }

    // Edge cases
    @Test
    fun `handles empty fractional part`() {
        val result = useCase("123.")
        assertEquals("123", result)
    }

    @Test
    fun `handles very long fractional part`() {
        val input = "123.12345678991234567890"
        val result = useCase(input, maxDecimalPlaces = 10)
        assertEquals("123.1234567899", result)
    }

    @Test
    fun `handles single digit after decimal`() {
        val result = useCase("123.5")
        assertEquals("123.5", result)
    }

    @Test
    fun `handles zero before decimal`() {
        val result = useCase("0.5")
        assertEquals("0.5", result)
    }

    @Test
    fun `handles very small number`() {
        val result = useCase("0.0001")
        assertEquals("0.0001", result)
    }

    @Test
    fun `strips trailing zeros from very small number`() {
        val result = useCase("0.00010000")
        assertEquals("0.0001", result)
    }

    @Test
    fun `handles multiple zeros before significant digit`() {
        val result = useCase("0.000500")
        assertEquals("0.0005", result)
    }

    @Test
    fun `limits preserves precision`() {
        val result = useCase("3.141592653589793", maxDecimalPlaces = 8)
        assertEquals("3.14159265", result)
    }

    @Test
    fun `no limit with trailing zeros`() {
        val result = useCase("3.14000")
        assertEquals("3.14", result)
    }

    @Test
    fun `limit exactly matches length`() {
        val result = useCase("1.234", maxDecimalPlaces = 3)
        assertEquals("1.234", result)
    }
}
