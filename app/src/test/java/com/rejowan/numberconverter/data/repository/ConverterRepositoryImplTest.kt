package com.rejowan.numberconverter.data.repository

import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.domain.model.NumberBase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConverterRepositoryImplTest {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var repository: ConverterRepositoryImpl

    @Before
    fun setup() {
        preferencesManager = mockk()
        coEvery { preferencesManager.decimalPlaces } returns flowOf(10)
        repository = ConverterRepositoryImpl(preferencesManager)
    }

    // Decimal to Binary Tests
    @Test
    fun `convert decimal 10 to binary returns 1010`() = runTest {
        val result = repository.convert("10", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertEquals("1010", result.getOrNull()?.output)
    }

    @Test
    fun `convert decimal 255 to binary returns 11111111`() = runTest {
        val result = repository.convert("255", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertEquals("11111111", result.getOrNull()?.output)
    }

    // Binary to Decimal Tests
    @Test
    fun `convert binary 1010 to decimal returns 10`() = runTest {
        val result = repository.convert("1010", NumberBase.BINARY, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("10", result.getOrNull()?.output)
    }

    @Test
    fun `convert binary 11111111 to decimal returns 255`() = runTest {
        val result = repository.convert("11111111", NumberBase.BINARY, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("255", result.getOrNull()?.output)
    }

    // Decimal to Hexadecimal Tests
    @Test
    fun `convert decimal 255 to hexadecimal returns FF`() = runTest {
        val result = repository.convert("255", NumberBase.DECIMAL, NumberBase.HEXADECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("FF", result.getOrNull()?.output)
    }

    @Test
    fun `convert decimal 15 to hexadecimal returns F`() = runTest {
        val result = repository.convert("15", NumberBase.DECIMAL, NumberBase.HEXADECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("F", result.getOrNull()?.output)
    }

    // Hexadecimal to Decimal Tests
    @Test
    fun `convert hexadecimal FF to decimal returns 255`() = runTest {
        val result = repository.convert("FF", NumberBase.HEXADECIMAL, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("255", result.getOrNull()?.output)
    }

    @Test
    fun `convert hexadecimal F to decimal returns 15`() = runTest {
        val result = repository.convert("F", NumberBase.HEXADECIMAL, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("15", result.getOrNull()?.output)
    }

    // Decimal to Octal Tests
    @Test
    fun `convert decimal 8 to octal returns 10`() = runTest {
        val result = repository.convert("8", NumberBase.DECIMAL, NumberBase.OCTAL)

        assertTrue(result.isSuccess)
        assertEquals("10", result.getOrNull()?.output)
    }

    @Test
    fun `convert decimal 64 to octal returns 100`() = runTest {
        val result = repository.convert("64", NumberBase.DECIMAL, NumberBase.OCTAL)

        assertTrue(result.isSuccess)
        assertEquals("100", result.getOrNull()?.output)
    }

    // Octal to Decimal Tests
    @Test
    fun `convert octal 10 to decimal returns 8`() = runTest {
        val result = repository.convert("10", NumberBase.OCTAL, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("8", result.getOrNull()?.output)
    }

    @Test
    fun `convert octal 100 to decimal returns 64`() = runTest {
        val result = repository.convert("100", NumberBase.OCTAL, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("64", result.getOrNull()?.output)
    }

    // Fractional Number Tests
    @Test
    fun `convert decimal 10 point 5 to binary`() = runTest {
        val result = repository.convert("10.5", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.output?.startsWith("1010.") == true)
    }

    @Test
    fun `convert binary 1010 point 1 to decimal`() = runTest {
        val result = repository.convert("1010.1", NumberBase.BINARY, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        val output = result.getOrNull()?.output
        assertNotNull(output)
        // Output should be "10.5" or similar depending on decimal places
        assertTrue(output?.contains("10.") == true || output == "10.5")
    }

    // Same Base Conversion
    @Test
    fun `convert same base returns input`() = runTest {
        val result = repository.convert("1010", NumberBase.BINARY, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertEquals("1010", result.getOrNull()?.output)
    }

    // Zero Conversion
    @Test
    fun `convert zero between bases`() = runTest {
        val result = repository.convert("0", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertEquals("0", result.getOrNull()?.output)
    }

    // Invalid Input Tests
    @Test
    fun `convert invalid binary input fails`() = runTest {
        val result = repository.convert("102", NumberBase.BINARY, NumberBase.DECIMAL)

        assertTrue(result.isFailure)
    }

    @Test
    fun `convert invalid octal input fails`() = runTest {
        val result = repository.convert("89", NumberBase.OCTAL, NumberBase.DECIMAL)

        assertTrue(result.isFailure)
    }

    @Test
    fun `convert invalid hexadecimal input fails`() = runTest {
        val result = repository.convert("XYZ", NumberBase.HEXADECIMAL, NumberBase.DECIMAL)

        assertTrue(result.isFailure)
    }

    // Explanation Tests
    @Test
    fun `generate explanation for decimal to binary conversion`() = runTest {
        val result = repository.explain("10", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        val explanation = result.getOrNull()
        assertNotNull(explanation)
        assertNotNull(explanation?.integralPart)
        // Check that summary exists and has content
        assertNotNull(explanation?.summary)
        assertTrue(explanation?.summary?.text?.isNotEmpty() == true)
    }

    @Test
    fun `generate explanation for binary to decimal conversion`() = runTest {
        val result = repository.explain("1010", NumberBase.BINARY, NumberBase.DECIMAL)

        assertTrue(result.isSuccess)
        val explanation = result.getOrNull()
        assertNotNull(explanation)
        assertNotNull(explanation?.integralPart)
    }

    @Test
    fun `generate explanation with fractional part`() = runTest {
        val result = repository.explain("10.5", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        val explanation = result.getOrNull()
        assertNotNull(explanation?.integralPart)
        assertNotNull(explanation?.fractionalPart)
    }

    @Test
    fun `generate explanation for invalid input fails`() = runTest {
        val result = repository.explain("102", NumberBase.BINARY, NumberBase.DECIMAL)

        assertTrue(result.isFailure)
    }
}
