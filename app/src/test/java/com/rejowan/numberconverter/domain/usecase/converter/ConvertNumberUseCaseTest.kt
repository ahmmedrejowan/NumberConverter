package com.rejowan.numberconverter.domain.usecase.converter

import com.rejowan.numberconverter.domain.model.ConversionResult
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.repository.ConverterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConvertNumberUseCaseTest {

    private lateinit var repository: ConverterRepository
    private lateinit var useCase: ConvertNumberUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ConvertNumberUseCase(repository)
    }

    // Successful conversion tests
    @Test
    fun `successful conversion returns success result`() = runTest {
        val input = "1010"
        val fromBase = NumberBase.BINARY
        val toBase = NumberBase.DECIMAL
        val expected = ConversionResult(input = input, output = "10", fromBase = fromBase, toBase = toBase)

        coEvery { repository.convert(input, fromBase, toBase) } returns Result.success(expected)

        val result = useCase(input, fromBase, toBase)

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `calls repository with correct parameters`() = runTest {
        val input = "FF"
        val fromBase = NumberBase.HEXADECIMAL
        val toBase = NumberBase.DECIMAL
        val expected = ConversionResult(input = input, output = "255", fromBase = fromBase, toBase = toBase)

        coEvery { repository.convert(input, fromBase, toBase) } returns Result.success(expected)

        useCase(input, fromBase, toBase)

        coVerify { repository.convert(input, fromBase, toBase) }
    }

    @Test
    fun `successful conversion with fractional number`() = runTest {
        val input = "10.5"
        val fromBase = NumberBase.DECIMAL
        val toBase = NumberBase.BINARY
        val expected = ConversionResult(input = input, output = "1010.1", fromBase = fromBase, toBase = toBase)

        coEvery { repository.convert(input, fromBase, toBase) } returns Result.success(expected)

        val result = useCase(input, fromBase, toBase)

        assertTrue(result.isSuccess)
        assertEquals("1010.1", result.getOrNull()?.output)
    }

    // Empty input handling tests
    @Test
    fun `empty string returns failure`() = runTest {
        val result = useCase("", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isFailure)
        assertEquals("Input cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `blank string with spaces returns failure`() = runTest {
        val result = useCase("   ", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isFailure)
        assertEquals("Input cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `blank string with tabs returns failure`() = runTest {
        val result = useCase("\t\t", NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isFailure)
        assertEquals("Input cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `empty input does not call repository`() = runTest {
        useCase("", NumberBase.DECIMAL, NumberBase.BINARY)

        coVerify(exactly = 0) { repository.convert(any(), any(), any()) }
    }

    // Repository error propagation tests
    @Test
    fun `repository failure is propagated`() = runTest {
        val input = "invalid"
        val fromBase = NumberBase.BINARY
        val toBase = NumberBase.DECIMAL
        val error = IllegalArgumentException("Invalid input")

        coEvery { repository.convert(input, fromBase, toBase) } returns Result.failure(error)

        val result = useCase(input, fromBase, toBase)

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `repository exception is propagated`() = runTest {
        val input = "123"
        val fromBase = NumberBase.DECIMAL
        val toBase = NumberBase.BINARY
        val exception = RuntimeException("Repository error")

        coEvery { repository.convert(input, fromBase, toBase) } returns Result.failure(exception)

        val result = useCase(input, fromBase, toBase)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    // Different base combinations
    @Test
    fun `converts decimal to hexadecimal`() = runTest {
        val input = "255"
        val expected = ConversionResult(input = input, output = "FF", fromBase = NumberBase.DECIMAL, toBase = NumberBase.HEXADECIMAL)

        coEvery { repository.convert(input, NumberBase.DECIMAL, NumberBase.HEXADECIMAL) } returns Result.success(expected)

        val result = useCase(input, NumberBase.DECIMAL, NumberBase.HEXADECIMAL)

        assertTrue(result.isSuccess)
        assertEquals("FF", result.getOrNull()?.output)
    }

    @Test
    fun `converts octal to binary`() = runTest {
        val input = "77"
        val expected = ConversionResult(input = input, output = "111111", fromBase = NumberBase.OCTAL, toBase = NumberBase.BINARY)

        coEvery { repository.convert(input, NumberBase.OCTAL, NumberBase.BINARY) } returns Result.success(expected)

        val result = useCase(input, NumberBase.OCTAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertEquals("111111", result.getOrNull()?.output)
    }

    @Test
    fun `same base conversion`() = runTest {
        val input = "123"
        val base = NumberBase.DECIMAL
        val expected = ConversionResult(input = input, output = input, fromBase = base, toBase = base)

        coEvery { repository.convert(input, base, base) } returns Result.success(expected)

        val result = useCase(input, base, base)

        assertTrue(result.isSuccess)
        assertEquals(input, result.getOrNull()?.output)
    }

    // Edge cases
    @Test
    fun `converts zero`() = runTest {
        val input = "0"
        val expected = ConversionResult(input = input, output = "0", fromBase = NumberBase.DECIMAL, toBase = NumberBase.BINARY)

        coEvery { repository.convert(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)

        val result = useCase(input, NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertEquals("0", result.getOrNull()?.output)
    }

    @Test
    fun `converts large number`() = runTest {
        val input = "999999"
        val expected = ConversionResult(input = input, output = "11110100001000111111", fromBase = NumberBase.DECIMAL, toBase = NumberBase.BINARY)

        coEvery { repository.convert(input, NumberBase.DECIMAL, NumberBase.BINARY) } returns Result.success(expected)

        val result = useCase(input, NumberBase.DECIMAL, NumberBase.BINARY)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }
}
