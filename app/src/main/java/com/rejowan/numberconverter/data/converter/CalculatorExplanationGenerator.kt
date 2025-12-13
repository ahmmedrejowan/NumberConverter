package com.rejowan.numberconverter.data.converter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.rejowan.numberconverter.domain.model.CalculatorExplanation
import com.rejowan.numberconverter.domain.model.ExplanationPart
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import com.rejowan.numberconverter.domain.model.OperationExplanation
import com.rejowan.numberconverter.domain.model.Step
import java.math.BigDecimal
import java.math.RoundingMode

object CalculatorExplanationGenerator {

    fun generate(
        input1: String,
        input1Base: NumberBase,
        input2: String,
        input2Base: NumberBase,
        operation: Operation,
        outputBase: NumberBase,
        result: String
    ): CalculatorExplanation {
        // Convert inputs to decimal for explanation
        val (integral1, fractional1) = BaseConverter.toBaseTen(input1, input1Base)
        val (integral2, fractional2) = BaseConverter.toBaseTen(input2, input2Base)

        val decimal1 = combineToBigDecimal(integral1, fractional1)
        val decimal2 = combineToBigDecimal(integral2, fractional2)

        // Generate explanations for each step
        val input1Conversion = if (input1Base != NumberBase.DECIMAL) {
            generateInputConversionExplanation(input1, input1Base, decimal1, "First Number")
        } else null

        val input2Conversion = if (input2Base != NumberBase.DECIMAL) {
            generateInputConversionExplanation(input2, input2Base, decimal2, "Second Number")
        } else null

        val operationExplanation = generateOperationExplanation(
            decimal1, decimal2, operation, input1Base, input2Base
        )

        val outputConversion = if (outputBase != NumberBase.DECIMAL) {
            generateOutputConversionExplanation(operationExplanation.result, outputBase, result)
        } else null

        val summary = generateSummary(
            input1, input1Base,
            input2, input2Base,
            operation,
            result, outputBase
        )

        return CalculatorExplanation(
            title = "Calculation Explanation",
            input1Conversion = input1Conversion,
            input2Conversion = input2Conversion,
            operation = operationExplanation,
            outputConversion = outputConversion,
            summary = summary
        )
    }

    private fun generateInputConversionExplanation(
        input: String,
        fromBase: NumberBase,
        decimalValue: BigDecimal,
        label: String
    ): ExplanationPart {
        val steps = mutableListOf<Step>()

        val stepDescription = buildAnnotatedString {
            append("Convert $label from ${fromBase.displayName} to Decimal:\n")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(input)
            }
            append(" (${fromBase.displayName})")
        }
        steps.add(Step(stepNumber = 1, description = stepDescription))

        // Show positional notation
        val parts = input.uppercase().split(".")
        val integral = parts[0]
        val calculations = mutableListOf<String>()

        integral.reversed().forEachIndexed { index, char ->
            val digitValue = charToDigitValue(char, fromBase)
            val positionValue = Math.pow(fromBase.value.toDouble(), index.toDouble()).toLong()
            val contribution = digitValue * positionValue
            calculations.add("$char × ${fromBase.value}^$index = $contribution")
        }

        val calculationStep = buildAnnotatedString {
            append("Using positional notation:\n")
            calculations.reversed().forEach { calc ->
                append("  $calc\n")
            }
            append("\nResult: ")
            withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                append(formatDecimal(decimalValue))
            }
        }
        steps.add(Step(stepNumber = 2, description = calculationStep, result = formatDecimal(decimalValue)))

        val result = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(input)
            }
            append(" (${fromBase.displayName}) = ")
            withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                append(formatDecimal(decimalValue))
            }
            append(" (Decimal)")
        }

        return ExplanationPart(
            title = "$label: ${fromBase.displayName} → Decimal",
            steps = steps,
            result = result
        )
    }

    private fun generateOperationExplanation(
        value1: BigDecimal,
        value2: BigDecimal,
        operation: Operation,
        input1Base: NumberBase,
        input2Base: NumberBase
    ): OperationExplanation {
        val result = when (operation) {
            Operation.ADD -> value1.add(value2)
            Operation.SUBTRACT -> value1.subtract(value2)
            Operation.MULTIPLY -> value1.multiply(value2)
            Operation.DIVIDE -> value1.divide(value2, 20, RoundingMode.HALF_UP)
        }

        val description = buildAnnotatedString {
            append("Perform ${operation.displayName} in Decimal:\n\n")

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(formatDecimal(value1))
            }
            if (input1Base != NumberBase.DECIMAL) {
                append(" (from ${input1Base.displayName})")
            }

            append("\n  ${operation.symbol}  ")

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(formatDecimal(value2))
            }
            if (input2Base != NumberBase.DECIMAL) {
                append(" (from ${input2Base.displayName})")
            }

            append("\n────────────\n  = ")
            withStyle(SpanStyle(color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)) {
                append(formatDecimal(result))
            }
        }

        return OperationExplanation(
            title = "${operation.displayName} Operation",
            description = description,
            result = formatDecimal(result)
        )
    }

    private fun generateOutputConversionExplanation(
        decimalResult: String,
        toBase: NumberBase,
        finalResult: String
    ): ExplanationPart {
        val steps = mutableListOf<Step>()

        val stepDescription = buildAnnotatedString {
            append("Convert result from Decimal to ${toBase.displayName}:\n")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(decimalResult)
            }
            append(" (Decimal)")
        }
        steps.add(Step(stepNumber = 1, description = stepDescription))

        // Show division method for integer part
        val decimalValue = BigDecimal(decimalResult)
        val integerPart = decimalValue.toBigInteger()

        if (integerPart.abs() > java.math.BigInteger.ZERO) {
            val divisions = mutableListOf<String>()
            var quotient = integerPart.abs()
            val base = toBase.value.toBigInteger()

            while (quotient > java.math.BigInteger.ZERO) {
                val remainder = quotient.mod(base)
                val remainderChar = digitValueToChar(remainder.toInt())
                divisions.add("$quotient ÷ ${toBase.value} = ${quotient.divide(base)} remainder $remainderChar")
                quotient = quotient.divide(base)
            }

            val divisionStep = buildAnnotatedString {
                append("Division method (read remainders bottom-up):\n")
                divisions.forEach { div ->
                    append("  $div\n")
                }
            }
            steps.add(Step(stepNumber = 2, description = divisionStep))
        }

        val resultAnnotated = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(decimalResult)
            }
            append(" (Decimal) = ")
            withStyle(SpanStyle(color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)) {
                append(finalResult)
            }
            append(" (${toBase.displayName})")
        }

        return ExplanationPart(
            title = "Result: Decimal → ${toBase.displayName}",
            steps = steps,
            result = resultAnnotated
        )
    }

    private fun generateSummary(
        input1: String,
        input1Base: NumberBase,
        input2: String,
        input2Base: NumberBase,
        operation: Operation,
        result: String,
        outputBase: NumberBase
    ): AnnotatedString {
        return buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Summary\n\n")
            }

            // First number
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(input1)
            }
            append(" (${input1Base.displayName})")

            // Operation
            append("  ${operation.symbol}  ")

            // Second number
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(input2)
            }
            append(" (${input2Base.displayName})")

            append("\n\n= ")
            withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                append(result)
            }
            append(" (${outputBase.displayName})")
        }
    }

    private fun combineToBigDecimal(
        integral: java.math.BigInteger,
        fractional: BigDecimal?
    ): BigDecimal {
        val integralDecimal = BigDecimal(integral)
        return if (fractional != null) {
            integralDecimal.add(fractional)
        } else {
            integralDecimal
        }
    }

    private fun formatDecimal(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        return if (stripped.scale() <= 0) {
            stripped.toBigInteger().toString()
        } else {
            stripped.toPlainString()
        }
    }

    private fun charToDigitValue(char: Char, base: NumberBase): Int {
        return when (char) {
            in '0'..'9' -> char - '0'
            in 'A'..'Z' -> char - 'A' + 10
            in 'a'..'z' -> char - 'a' + 10
            else -> 0
        }
    }

    private fun digitValueToChar(value: Int): Char {
        return when (value) {
            in 0..9 -> '0' + value
            in 10..35 -> 'A' + (value - 10)
            else -> '0'
        }
    }
}
