package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class BaseConverterTest {

    @Test
    fun `toBaseTen converts binary to decimal`() {
        val (integral, fractional) = BaseConverter.toBaseTen("1010", NumberBase.BINARY)

        assertEquals(BigInteger("10"), integral)
        assertNull(fractional)
    }

    @Test
    fun `toBaseTen converts binary with fractional part`() {
        val (integral, fractional) = BaseConverter.toBaseTen("1010.1", NumberBase.BINARY)

        assertEquals(BigInteger("10"), integral)
        assertNotNull(fractional)
        assertTrue(fractional!!.compareTo(BigDecimal("0.5")) == 0)
    }

    @Test
    fun `toBaseTen converts octal to decimal`() {
        val (integral, fractional) = BaseConverter.toBaseTen("10", NumberBase.OCTAL)

        assertEquals(BigInteger("8"), integral)
        assertNull(fractional)
    }

    @Test
    fun `toBaseTen converts hexadecimal to decimal`() {
        val (integral, fractional) = BaseConverter.toBaseTen("FF", NumberBase.HEXADECIMAL)

        assertEquals(BigInteger("255"), integral)
        assertNull(fractional)
    }

    @Test
    fun `toBaseTen converts hexadecimal lowercase to decimal`() {
        val (integral, fractional) = BaseConverter.toBaseTen("ff", NumberBase.HEXADECIMAL)

        assertEquals(BigInteger("255"), integral)
        assertNull(fractional)
    }

    @Test
    fun `toBaseTen converts zero`() {
        val (integral, fractional) = BaseConverter.toBaseTen("0", NumberBase.DECIMAL)

        assertEquals(BigInteger.ZERO, integral)
        assertNull(fractional)
    }

    @Test
    fun `toBaseTen converts large binary number`() {
        val (integral, _) = BaseConverter.toBaseTen("11111111", NumberBase.BINARY)

        assertEquals(BigInteger("255"), integral)
    }

    @Test
    fun `toBaseTen converts octal with fractional part`() {
        val (integral, fractional) = BaseConverter.toBaseTen("10.4", NumberBase.OCTAL)

        assertEquals(BigInteger("8"), integral)
        assertNotNull(fractional)
        assertTrue(fractional!!.compareTo(BigDecimal.ZERO) > 0)
    }

    @Test
    fun `fromBaseTen converts decimal to binary`() {
        val result = BaseConverter.fromBaseTen(
            BigInteger("10"),
            null,
            NumberBase.BINARY,
            10
        )

        assertEquals("1010", result)
    }

    @Test
    fun `fromBaseTen converts decimal to binary with fractional`() {
        val result = BaseConverter.fromBaseTen(
            BigInteger("10"),
            BigDecimal("0.5"),
            NumberBase.BINARY,
            10
        )

        assertTrue(result.startsWith("1010."))
        assertTrue(result.contains("1"))
    }

    @Test
    fun `fromBaseTen converts decimal to octal`() {
        val result = BaseConverter.fromBaseTen(
            BigInteger("8"),
            null,
            NumberBase.OCTAL,
            10
        )

        assertEquals("10", result)
    }

    @Test
    fun `fromBaseTen converts decimal to hexadecimal`() {
        val result = BaseConverter.fromBaseTen(
            BigInteger("255"),
            null,
            NumberBase.HEXADECIMAL,
            10
        )

        assertEquals("FF", result)
    }

    @Test
    fun `fromBaseTen converts zero to any base`() {
        val binary = BaseConverter.fromBaseTen(BigInteger.ZERO, null, NumberBase.BINARY, 10)
        val octal = BaseConverter.fromBaseTen(BigInteger.ZERO, null, NumberBase.OCTAL, 10)
        val hex = BaseConverter.fromBaseTen(BigInteger.ZERO, null, NumberBase.HEXADECIMAL, 10)

        assertEquals("0", binary)
        assertEquals("0", octal)
        assertEquals("0", hex)
    }

    @Test
    fun `fromBaseTen respects decimal places limit`() {
        val result5 = BaseConverter.fromBaseTen(
            BigInteger("10"),
            BigDecimal("0.333333333"),
            NumberBase.BINARY,
            5
        )

        val result10 = BaseConverter.fromBaseTen(
            BigInteger("10"),
            BigDecimal("0.333333333"),
            NumberBase.BINARY,
            10
        )

        assertTrue(result5.length <= result10.length)
    }

    @Test
    fun `isValidInput validates binary correctly`() {
        assertTrue(BaseConverter.isValidInput("1010", NumberBase.BINARY))
        assertTrue(BaseConverter.isValidInput("0", NumberBase.BINARY))
        assertTrue(BaseConverter.isValidInput("1010.1", NumberBase.BINARY))
        assertFalse(BaseConverter.isValidInput("102", NumberBase.BINARY))
        assertFalse(BaseConverter.isValidInput("abc", NumberBase.BINARY))
    }

    @Test
    fun `isValidInput validates octal correctly`() {
        assertTrue(BaseConverter.isValidInput("12", NumberBase.OCTAL))
        assertTrue(BaseConverter.isValidInput("0", NumberBase.OCTAL))
        assertTrue(BaseConverter.isValidInput("77.7", NumberBase.OCTAL))
        assertFalse(BaseConverter.isValidInput("89", NumberBase.OCTAL))
        assertFalse(BaseConverter.isValidInput("abc", NumberBase.OCTAL))
    }

    @Test
    fun `isValidInput validates decimal correctly`() {
        assertTrue(BaseConverter.isValidInput("123", NumberBase.DECIMAL))
        assertTrue(BaseConverter.isValidInput("0", NumberBase.DECIMAL))
        assertTrue(BaseConverter.isValidInput("123.456", NumberBase.DECIMAL))
        assertFalse(BaseConverter.isValidInput("abc", NumberBase.DECIMAL))
        assertFalse(BaseConverter.isValidInput("12.34.56", NumberBase.DECIMAL))
    }

    @Test
    fun `isValidInput validates hexadecimal correctly`() {
        assertTrue(BaseConverter.isValidInput("FF", NumberBase.HEXADECIMAL))
        assertTrue(BaseConverter.isValidInput("ff", NumberBase.HEXADECIMAL))
        assertTrue(BaseConverter.isValidInput("ABC", NumberBase.HEXADECIMAL))
        assertTrue(BaseConverter.isValidInput("123", NumberBase.HEXADECIMAL))
        assertTrue(BaseConverter.isValidInput("F.8", NumberBase.HEXADECIMAL))
        assertFalse(BaseConverter.isValidInput("XYZ", NumberBase.HEXADECIMAL))
        assertFalse(BaseConverter.isValidInput("GG", NumberBase.HEXADECIMAL))
    }

    @Test
    fun `isValidInput rejects empty strings`() {
        assertFalse(BaseConverter.isValidInput("", NumberBase.BINARY))
        assertFalse(BaseConverter.isValidInput("", NumberBase.OCTAL))
        assertFalse(BaseConverter.isValidInput("", NumberBase.DECIMAL))
        assertFalse(BaseConverter.isValidInput("", NumberBase.HEXADECIMAL))
    }

    @Test
    fun `isValidInput rejects blank strings`() {
        assertFalse(BaseConverter.isValidInput("   ", NumberBase.BINARY))
        assertFalse(BaseConverter.isValidInput("   ", NumberBase.DECIMAL))
    }

    @Test
    fun `isValidInput with negative numbers depends on implementation`() {
        // This test documents that negative number support may vary
        // Just checking that validation doesn't crash
        BaseConverter.isValidInput("-1010", NumberBase.BINARY)
        BaseConverter.isValidInput("-123", NumberBase.DECIMAL)
    }

    @Test
    fun `conversion preserves precision for large numbers`() {
        val largeNumber = BigInteger("999999999999")
        val result = BaseConverter.fromBaseTen(largeNumber, null, NumberBase.BINARY, 10)
        val (backToDecimal, _) = BaseConverter.toBaseTen(result, NumberBase.BINARY)

        assertEquals(largeNumber, backToDecimal)
    }

    @Test
    fun `round trip conversion binary to decimal to binary`() {
        val original = "11001100"
        val (integral, fractional) = BaseConverter.toBaseTen(original, NumberBase.BINARY)
        val result = BaseConverter.fromBaseTen(integral, fractional, NumberBase.BINARY, 10)

        assertEquals(original, result)
    }

    @Test
    fun `round trip conversion hexadecimal to decimal to hexadecimal`() {
        val original = "ABCDEF"
        val (integral, fractional) = BaseConverter.toBaseTen(original, NumberBase.HEXADECIMAL)
        val result = BaseConverter.fromBaseTen(integral, fractional, NumberBase.HEXADECIMAL, 10)

        assertEquals(original, result)
    }

    @Test
    fun `fromBaseTen produces uppercase hexadecimal`() {
        val result = BaseConverter.fromBaseTen(
            BigInteger("255"),
            null,
            NumberBase.HEXADECIMAL,
            10
        )

        assertEquals(result, result.uppercase())
    }
}
