package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import org.junit.Assert.*
import org.junit.Test

class CalculatorExplanationGeneratorTest {

    // ==================== Basic Generation Tests ====================

    @Test
    fun `generates explanation for simple decimal addition`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertNotNull(result)
        assertEquals("Calculation Explanation", result.title)
        assertNotNull(result.operation)
        assertNotNull(result.summary)
    }

    @Test
    fun `generates explanation for all four operations`() {
        Operation.entries.forEach { operation ->
            val result = CalculatorExplanationGenerator.generate(
                input1 = "10",
                input1Base = NumberBase.DECIMAL,
                input2 = "5",
                input2Base = NumberBase.DECIMAL,
                operation = operation,
                outputBase = NumberBase.DECIMAL,
                result = "15"
            )

            assertNotNull(result)
            assertEquals(operation.displayName + " Operation", result.operation.title)
        }
    }

    // ==================== Input Conversion Tests ====================

    @Test
    fun `no input1 conversion when input1 is decimal`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "101",
            input2Base = NumberBase.BINARY,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertNull(result.input1Conversion)
        assertNotNull(result.input2Conversion)
    }

    @Test
    fun `no input2 conversion when input2 is decimal`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1010",
            input1Base = NumberBase.BINARY,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertNotNull(result.input1Conversion)
        assertNull(result.input2Conversion)
    }

    @Test
    fun `both input conversions when neither is decimal`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1010",
            input1Base = NumberBase.BINARY,
            input2 = "F",
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "25"
        )

        assertNotNull(result.input1Conversion)
        assertNotNull(result.input2Conversion)
        assertEquals("First Number: Binary → Decimal", result.input1Conversion?.title)
        assertEquals("Second Number: Hexadecimal → Decimal", result.input2Conversion?.title)
    }

    @Test
    fun `no input conversions when both are decimal`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertNull(result.input1Conversion)
        assertNull(result.input2Conversion)
    }

    // ==================== Output Conversion Tests ====================

    @Test
    fun `no output conversion when output is decimal`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertNull(result.outputConversion)
    }

    @Test
    fun `output conversion when output is binary`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.BINARY,
            result = "1111"
        )

        assertNotNull(result.outputConversion)
        assertEquals("Result: Decimal → Binary", result.outputConversion?.title)
    }

    @Test
    fun `output conversion when output is hexadecimal`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.HEXADECIMAL,
            result = "F"
        )

        assertNotNull(result.outputConversion)
        assertEquals("Result: Decimal → Hexadecimal", result.outputConversion?.title)
    }

    @Test
    fun `output conversion when output is octal`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.OCTAL,
            result = "17"
        )

        assertNotNull(result.outputConversion)
        assertEquals("Result: Decimal → Octal", result.outputConversion?.title)
    }

    // ==================== Operation Explanation Tests ====================

    @Test
    fun `operation explanation shows addition symbol`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertEquals("Addition Operation", result.operation.title)
        assertTrue(result.operation.description.text.contains("+"))
    }

    @Test
    fun `operation explanation shows subtraction symbol`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.DECIMAL,
            result = "5"
        )

        assertEquals("Subtraction Operation", result.operation.title)
        assertTrue(result.operation.description.text.contains("-"))
    }

    @Test
    fun `operation explanation shows multiplication symbol`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.DECIMAL,
            result = "50"
        )

        assertEquals("Multiplication Operation", result.operation.title)
        assertTrue(result.operation.description.text.contains("×"))
    }

    @Test
    fun `operation explanation shows division symbol`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.DIVIDE,
            outputBase = NumberBase.DECIMAL,
            result = "2"
        )

        assertEquals("Division Operation", result.operation.title)
        assertTrue(result.operation.description.text.contains("÷"))
    }

    // ==================== Cross-Base Tests ====================

    @Test
    fun `all different bases generates all conversions`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1010",
            input1Base = NumberBase.BINARY,
            input2 = "12",
            input2Base = NumberBase.OCTAL,
            operation = Operation.ADD,
            outputBase = NumberBase.HEXADECIMAL,
            result = "14"
        )

        assertNotNull(result.input1Conversion)
        assertNotNull(result.input2Conversion)
        assertNotNull(result.outputConversion)
        assertNotNull(result.operation)
    }

    @Test
    fun `binary to hex calculation explanation`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1111",
            input1Base = NumberBase.BINARY,
            input2 = "1",
            input2Base = NumberBase.BINARY,
            operation = Operation.ADD,
            outputBase = NumberBase.HEXADECIMAL,
            result = "10"
        )

        assertNotNull(result.input1Conversion)
        assertNotNull(result.input2Conversion)
        assertNotNull(result.outputConversion)
        assertEquals("First Number: Binary → Decimal", result.input1Conversion?.title)
    }

    // ==================== Summary Tests ====================

    @Test
    fun `summary contains input values`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        val summaryText = result.summary.text
        assertTrue(summaryText.contains("10"))
        assertTrue(summaryText.contains("5"))
        assertTrue(summaryText.contains("15"))
    }

    @Test
    fun `summary contains base names`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1010",
            input1Base = NumberBase.BINARY,
            input2 = "F",
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.OCTAL,
            result = "31"
        )

        val summaryText = result.summary.text
        assertTrue(summaryText.contains("Binary"))
        assertTrue(summaryText.contains("Hexadecimal"))
        assertTrue(summaryText.contains("Octal"))
    }

    @Test
    fun `summary contains operation symbol`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.MULTIPLY,
            outputBase = NumberBase.DECIMAL,
            result = "50"
        )

        val summaryText = result.summary.text
        assertTrue(summaryText.contains("×"))
    }

    // ==================== Step Content Tests ====================

    @Test
    fun `input conversion has steps`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1010",
            input1Base = NumberBase.BINARY,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertNotNull(result.input1Conversion)
        assertTrue(result.input1Conversion!!.steps.isNotEmpty())
    }

    @Test
    fun `output conversion has steps`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.BINARY,
            result = "1111"
        )

        assertNotNull(result.outputConversion)
        assertTrue(result.outputConversion!!.steps.isNotEmpty())
    }

    @Test
    fun `conversion steps have descriptions`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1010",
            input1Base = NumberBase.BINARY,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        result.input1Conversion?.steps?.forEach { step ->
            assertFalse(step.description.text.isEmpty())
        }
    }

    @Test
    fun `conversion result is not empty`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "1010",
            input1Base = NumberBase.BINARY,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertTrue(result.input1Conversion?.result?.text?.isNotEmpty() == true)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `handles zero input`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "0",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "5"
        )

        assertNotNull(result)
        assertNotNull(result.operation)
    }

    @Test
    fun `handles negative result`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "5",
            input1Base = NumberBase.DECIMAL,
            input2 = "10",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.SUBTRACT,
            outputBase = NumberBase.DECIMAL,
            result = "-5"
        )

        assertNotNull(result)
        assertTrue(result.summary.text.contains("-5"))
    }

    @Test
    fun `handles large numbers`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "999999",
            input1Base = NumberBase.DECIMAL,
            input2 = "1",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "1000000"
        )

        assertNotNull(result)
        assertNotNull(result.operation)
    }

    @Test
    fun `handles lowercase hex input`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "ff",
            input1Base = NumberBase.HEXADECIMAL,
            input2 = "1",
            input2Base = NumberBase.HEXADECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "256"
        )

        assertNotNull(result)
        assertNotNull(result.input1Conversion)
        assertNotNull(result.input2Conversion)
    }

    @Test
    fun `operation result is stored`() {
        val result = CalculatorExplanationGenerator.generate(
            input1 = "10",
            input1Base = NumberBase.DECIMAL,
            input2 = "5",
            input2Base = NumberBase.DECIMAL,
            operation = Operation.ADD,
            outputBase = NumberBase.DECIMAL,
            result = "15"
        )

        assertEquals("15", result.operation.result)
    }
}
