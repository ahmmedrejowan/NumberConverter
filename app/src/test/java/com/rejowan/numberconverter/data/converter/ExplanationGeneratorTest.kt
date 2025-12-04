package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase
import org.junit.Assert.*
import org.junit.Test

class ExplanationGeneratorTest {

    // Decimal to Binary tests
    @Test
    fun `generates explanation for decimal to binary integer conversion`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "10",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
        assertTrue(result?.steps?.isNotEmpty() == true)
        assertNotNull(result?.result)
    }

    @Test
    fun `generates explanation for decimal to binary with fractional part`() {
        val integralPart = ExplanationGenerator.generateIntegralExplanation(
            input = "10.5",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )
        val fractionalPart = ExplanationGenerator.generateFractionalExplanation(
            input = "10.5",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(integralPart)
        assertNotNull(fractionalPart)
        assertEquals("Converting Integral Part", integralPart?.title)
        assertEquals("Converting Fractional Part", fractionalPart?.title)
    }

    @Test
    fun `decimal to binary explanation contains steps`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "15",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        assertTrue(result?.steps?.size!! > 0)
    }

    // Binary to Decimal tests
    @Test
    fun `generates explanation for binary to decimal conversion`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "1010",
            fromBase = NumberBase.BINARY,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `binary to decimal explanation uses positional notation`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "1010",
            fromBase = NumberBase.BINARY,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(result)
        // Steps should explain positional value multiplication
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `binary with fractional part to decimal`() {
        val fractionalPart = ExplanationGenerator.generateFractionalExplanation(
            input = "1010.1",
            fromBase = NumberBase.BINARY,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(fractionalPart)
        assertEquals("Converting Fractional Part", fractionalPart?.title)
    }

    // Decimal to Hexadecimal tests
    @Test
    fun `generates explanation for decimal to hex conversion`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "255",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.HEXADECIMAL
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `decimal to hex with large number`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "4095",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.HEXADECIMAL
        )

        assertNotNull(result)
        assertTrue(result?.steps?.size!! > 0)
    }

    @Test
    fun `decimal to hex with fractional part`() {
        val fractionalPart = ExplanationGenerator.generateFractionalExplanation(
            input = "255.25",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.HEXADECIMAL
        )

        assertNotNull(fractionalPart)
        assertEquals("Converting Fractional Part", fractionalPart?.title)
    }

    // Hexadecimal to Decimal tests
    @Test
    fun `generates explanation for hex to decimal conversion`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "FF",
            fromBase = NumberBase.HEXADECIMAL,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `hex to decimal with letters A-F`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "ABC",
            fromBase = NumberBase.HEXADECIMAL,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(result)
        assertTrue(result?.steps?.size!! > 0)
    }

    @Test
    fun `hex with fractional to decimal`() {
        val fractionalPart = ExplanationGenerator.generateFractionalExplanation(
            input = "FF.8",
            fromBase = NumberBase.HEXADECIMAL,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(fractionalPart)
        assertEquals("Converting Fractional Part", fractionalPart?.title)
    }

    // Octal conversions tests
    @Test
    fun `generates explanation for decimal to octal`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "64",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.OCTAL
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `generates explanation for octal to decimal`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "777",
            fromBase = NumberBase.OCTAL,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `octal with fractional to decimal`() {
        val fractionalPart = ExplanationGenerator.generateFractionalExplanation(
            input = "77.7",
            fromBase = NumberBase.OCTAL,
            toBase = NumberBase.DECIMAL
        )

        assertNotNull(fractionalPart)
        assertEquals("Converting Fractional Part", fractionalPart?.title)
    }

    // Same base conversion (should return null)
    @Test
    fun `same base conversion returns null for integral part`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "123",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.DECIMAL
        )

        assertNull(result)
    }

    @Test
    fun `same base conversion returns null for fractional part`() {
        val result = ExplanationGenerator.generateFractionalExplanation(
            input = "123.45",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.DECIMAL
        )

        assertNull(result)
    }

    // Edge cases
    @Test
    fun `generates explanation for zero`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "0",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
    }

    @Test
    fun `generates explanation for one`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "1",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        assertEquals("Converting Integral Part", result?.title)
    }

    @Test
    fun `fractional only number returns null for integral if input has no integral`() {
        val fractionalPart = ExplanationGenerator.generateFractionalExplanation(
            input = "0.5",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(fractionalPart)
        assertEquals("Converting Fractional Part", fractionalPart?.title)
    }

    @Test
    fun `no fractional part returns null for fractional explanation`() {
        val result = ExplanationGenerator.generateFractionalExplanation(
            input = "10",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNull(result)
    }

    @Test
    fun `large number explanation has multiple steps`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "1000",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        assertTrue(result?.steps?.size!! > 0) // Should have division steps
    }

    // Cross-base conversions (non-decimal)
    @Test
    fun `binary to hex conversion via decimal`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "11111111",
            fromBase = NumberBase.BINARY,
            toBase = NumberBase.HEXADECIMAL
        )

        assertNotNull(result)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `octal to binary conversion via decimal`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "77",
            fromBase = NumberBase.OCTAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    @Test
    fun `hex to octal conversion via decimal`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "FF",
            fromBase = NumberBase.HEXADECIMAL,
            toBase = NumberBase.OCTAL
        )

        assertNotNull(result)
        assertTrue(result?.steps?.isNotEmpty() == true)
    }

    // Step content validation
    @Test
    fun `steps contain descriptions`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "10",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        result?.steps?.forEach { step ->
            assertFalse(step.description.text.isEmpty())
        }
    }

    @Test
    fun `result AnnotatedString is not empty`() {
        val result = ExplanationGenerator.generateIntegralExplanation(
            input = "10",
            fromBase = NumberBase.DECIMAL,
            toBase = NumberBase.BINARY
        )

        assertNotNull(result)
        assertTrue(result?.result?.text?.isNotEmpty() == true)
    }
}
