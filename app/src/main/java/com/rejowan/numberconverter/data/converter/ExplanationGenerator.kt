package com.rejowan.numberconverter.data.converter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.rejowan.numberconverter.domain.model.ExplanationPart
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Step
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Generates detailed step-by-step explanations for number base conversions.
 */
object ExplanationGenerator {

    /**
     * Generates explanation for converting integral part.
     */
    fun generateIntegralExplanation(
        input: String,
        fromBase: NumberBase,
        toBase: NumberBase
    ): ExplanationPart? {
        if (fromBase == toBase) return null

        val steps = mutableListOf<Step>()
        val integral = input.split(".")[0].uppercase()

        // Step 1: Convert to decimal if not already
        val decimalValue = if (fromBase == NumberBase.DECIMAL) {
            BigInteger(integral)
        } else {
            val value = convertToDecimalIntegral(integral, fromBase, steps)
            value
        }

        // Step 2: Convert from decimal to target base if needed
        if (toBase != NumberBase.DECIMAL) {
            convertFromDecimalIntegral(decimalValue, toBase, steps)
        }

        val result = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Integral Part: ")
            }
            append("$integral (${fromBase.displayName}) = ")
            withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                append(BaseConverter.convertIntegralToBase(decimalValue, toBase))
            }
            append(" (${toBase.displayName})")
        }

        return ExplanationPart(
            title = "Converting Integral Part",
            steps = steps,
            result = result
        )
    }

    /**
     * Generates explanation for converting fractional part.
     */
    fun generateFractionalExplanation(
        input: String,
        fromBase: NumberBase,
        toBase: NumberBase,
        decimalPlaces: Int = 15
    ): ExplanationPart? {
        val parts = input.split(".")
        if (parts.size < 2) return null
        if (fromBase == toBase) return null

        val steps = mutableListOf<Step>()
        val fractional = parts[1].uppercase()

        // Step 1: Convert to decimal if not already
        val decimalValue = if (fromBase == NumberBase.DECIMAL) {
            BigDecimal("0.$fractional")
        } else {
            convertToDecimalFractional(fractional, fromBase, steps)
        }

        // Step 2: Convert from decimal to target base if needed
        val result = if (toBase != NumberBase.DECIMAL) {
            convertFromDecimalFractional(decimalValue, toBase, decimalPlaces, steps)
        } else {
            decimalValue.toString().substring(2) // Remove "0."
        }

        val resultString = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Fractional Part: ")
            }
            append("0.$fractional (${fromBase.displayName}) = ")
            withStyle(SpanStyle(color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)) {
                append("0.$result")
            }
            append(" (${toBase.displayName})")
        }

        return ExplanationPart(
            title = "Converting Fractional Part",
            steps = steps,
            result = resultString
        )
    }

    /**
     * Converts integral part to decimal with step-by-step explanation.
     */
    private fun convertToDecimalIntegral(
        input: String,
        fromBase: NumberBase,
        steps: MutableList<Step>
    ): BigInteger {
        val stepDescription = buildAnnotatedString {
            append("Convert ${fromBase.displayName} to Decimal using positional notation:\n")
            append("Each digit is multiplied by ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${fromBase.value}ⁿ")
            }
            append(" where n is the position from right (starting at 0)")
        }

        steps.add(Step(stepNumber = steps.size + 1, description = stepDescription, result = null))

        var result = BigInteger.ZERO
        val base = fromBase.value.toBigInteger()
        val calculations = mutableListOf<String>()

        input.reversed().forEachIndexed { index, char ->
            val digitValue = BaseConverter.charToDigitValue(char, fromBase)
            val positionValue = base.pow(index)
            val contribution = digitValue.toBigInteger() * positionValue

            result += contribution

            calculations.add("($char × ${fromBase.value}^$index) = ($digitValue × $positionValue) = $contribution")
        }

        val calculationStep = buildAnnotatedString {
            append("Calculation:\n")
            calculations.forEach { calc ->
                append("  $calc\n")
            }
            append("\nSum: ")
            withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                append(result.toString())
            }
        }

        steps.add(Step(stepNumber = steps.size + 1, description = calculationStep, result = result.toString()))

        return result
    }

    /**
     * Converts fractional part to decimal with step-by-step explanation.
     */
    private fun convertToDecimalFractional(
        input: String,
        fromBase: NumberBase,
        steps: MutableList<Step>
    ): BigDecimal {
        val stepDescription = buildAnnotatedString {
            append("Convert fractional ${fromBase.displayName} to Decimal:\n")
            append("Each digit is multiplied by ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${fromBase.value}⁻ⁿ")
            }
            append(" where n is the position from left (starting at 1)")
        }

        steps.add(Step(stepNumber = steps.size + 1, description = stepDescription, result = null))

        var result = BigDecimal.ZERO
        val base = fromBase.value.toBigDecimal()
        val calculations = mutableListOf<String>()

        input.forEachIndexed { index, char ->
            val position = index + 1
            val digitValue = BaseConverter.charToDigitValue(char, fromBase)
            val positionValue = BigDecimal.ONE.divide(base.pow(position), 50, RoundingMode.HALF_UP)
            val contribution = digitValue.toBigDecimal() * positionValue

            result = result.add(contribution)

            calculations.add("($char × ${fromBase.value}^-$position) ≈ ${contribution.setScale(10, RoundingMode.HALF_UP)}")
        }

        val calculationStep = buildAnnotatedString {
            append("Calculation:\n")
            calculations.forEach { calc ->
                append("  $calc\n")
            }
            append("\nSum: ")
            withStyle(SpanStyle(color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)) {
                append(result.setScale(10, RoundingMode.HALF_UP).toString())
            }
        }

        steps.add(Step(stepNumber = steps.size + 1, description = calculationStep, result = result.toString()))

        return result
    }

    /**
     * Converts from decimal to target base (integral) with step-by-step explanation.
     */
    private fun convertFromDecimalIntegral(
        decimal: BigInteger,
        toBase: NumberBase,
        steps: MutableList<Step>
    ) {
        val stepDescription = buildAnnotatedString {
            append("Convert Decimal to ${toBase.displayName}:\n")
            append("Repeatedly divide by ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${toBase.value}")
            }
            append(" and track remainders (read bottom to top)")
        }

        steps.add(Step(stepNumber = steps.size + 1, description = stepDescription, result = null))

        val divisions = mutableListOf<String>()
        var quotient = decimal
        val base = toBase.value.toBigInteger()
        val remainders = mutableListOf<String>()

        while (quotient > BigInteger.ZERO) {
            val remainder = quotient.mod(base)
            val remainderChar = BaseConverter.digitValueToChar(remainder.toInt())
            remainders.add(0, remainderChar.toString())
            divisions.add("$quotient ÷ ${toBase.value} = ${quotient.divide(base)} remainder $remainder ($remainderChar)")
            quotient = quotient.divide(base)
        }

        val divisionStep = buildAnnotatedString {
            append("Division steps:\n")
            divisions.forEach { div ->
                append("  $div\n")
            }
            append("\nResult (read remainders bottom to top): ")
            withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                append(remainders.joinToString(""))
            }
        }

        steps.add(Step(stepNumber = steps.size + 1, description = divisionStep, result = remainders.joinToString("")))
    }

    /**
     * Converts from decimal to target base (fractional) with step-by-step explanation.
     */
    private fun convertFromDecimalFractional(
        decimal: BigDecimal,
        toBase: NumberBase,
        decimalPlaces: Int,
        steps: MutableList<Step>
    ): String {
        val stepDescription = buildAnnotatedString {
            append("Convert fractional Decimal to ${toBase.displayName}:\n")
            append("Repeatedly multiply by ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${toBase.value}")
            }
            append(" and take the integer part")
        }

        steps.add(Step(stepNumber = steps.size + 1, description = stepDescription, result = null))

        val multiplications = mutableListOf<String>()
        var fraction = decimal
        val base = toBase.value.toBigDecimal()
        val result = StringBuilder()
        var places = 0

        while (fraction > BigDecimal.ZERO && places < decimalPlaces && places < 10) {
            fraction = fraction.multiply(base)
            val digit = fraction.toInt()
            val digitChar = BaseConverter.digitValueToChar(digit)
            result.append(digitChar)

            multiplications.add("${fraction.subtract(digit.toBigDecimal()).setScale(6, RoundingMode.HALF_UP)} × ${toBase.value} = ${fraction.setScale(6, RoundingMode.HALF_UP)} → digit: $digitChar")

            fraction = fraction.subtract(digit.toBigDecimal())
            places++
        }

        val multiplicationStep = buildAnnotatedString {
            append("Multiplication steps:\n")
            multiplications.forEach { mult ->
                append("  $mult\n")
            }
            append("\nResult: ")
            withStyle(SpanStyle(color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)) {
                append(result.toString())
            }
        }

        steps.add(Step(stepNumber = steps.size + 1, description = multiplicationStep, result = result.toString()))

        return result.toString().trimEnd('0')
    }

    /**
     * Generates final summary for the complete conversion.
     */
    fun generateSummary(
        input: String,
        output: String,
        fromBase: NumberBase,
        toBase: NumberBase
    ): AnnotatedString {
        return buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("Conversion Summary\n\n")
            }

            append("Input: ")
            withStyle(SpanStyle(color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)) {
                append(input)
            }
            append(" (${fromBase.displayName})\n\n")

            append("Output: ")
            withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                append(output)
            }
            append(" (${toBase.displayName})\n\n")

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Method: ")
            }
            append(
                if (fromBase == NumberBase.DECIMAL) {
                    "Direct conversion from decimal using ${if (toBase == NumberBase.BINARY) "division" else "division"} method"
                } else if (toBase == NumberBase.DECIMAL) {
                    "Direct conversion to decimal using positional notation"
                } else {
                    "Two-step conversion: ${fromBase.displayName} → Decimal → ${toBase.displayName}"
                }
            )
        }
    }

    /**
     * Helper function to convert char to digit value (duplicated from BaseConverter for explanation context).
     */
    private fun BaseConverter.charToDigitValue(char: Char, base: NumberBase): Int {
        val value = when (char) {
            in '0'..'9' -> char - '0'
            in 'A'..'Z' -> char - 'A' + 10
            in 'a'..'z' -> char - 'a' + 10
            else -> throw IllegalArgumentException("Invalid character: $char")
        }

        if (value >= base.value) {
            throw IllegalArgumentException("Character '$char' is not valid for base ${base.value}")
        }

        return value
    }

    /**
     * Helper function to convert digit value to char (duplicated from BaseConverter for explanation context).
     */
    private fun BaseConverter.digitValueToChar(value: Int): Char {
        return when (value) {
            in 0..9 -> '0' + value
            in 10..35 -> 'A' + (value - 10)
            else -> throw IllegalArgumentException("Invalid digit value: $value")
        }
    }
}
