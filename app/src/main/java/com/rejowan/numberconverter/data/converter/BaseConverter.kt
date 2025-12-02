package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Core converter class that handles conversion between different number bases.
 * Supports integer and fractional parts conversion.
 */
object BaseConverter {

    /**
     * Converts a number from any base to decimal (base 10).
     *
     * @param input The input number as a string
     * @param fromBase The base of the input number
     * @return Pair of (integral part, fractional part) in decimal
     */
    fun toBaseTen(input: String, fromBase: NumberBase): Pair<BigInteger, BigDecimal?> {
        val parts = input.uppercase().split(".")
        val integralPart = parts[0]
        val fractionalPart = parts.getOrNull(1)

        // Convert integral part
        val integralDecimal = integralPart.fold(BigInteger.ZERO) { acc, char ->
            val digitValue = charToDigitValue(char, fromBase)
            acc.multiply(fromBase.value.toBigInteger()) + digitValue.toBigInteger()
        }

        // Convert fractional part if exists
        val fractionalDecimal = fractionalPart?.let { fraction ->
            var result = BigDecimal.ZERO
            var divisor = BigDecimal.ONE

            fraction.forEach { char ->
                divisor = divisor.divide(fromBase.value.toBigDecimal(), 50, RoundingMode.HALF_UP)
                val digitValue = charToDigitValue(char, fromBase)
                result = result.add(digitValue.toBigDecimal().multiply(divisor))
            }
            result
        }

        return Pair(integralDecimal, fractionalDecimal)
    }

    /**
     * Converts a decimal number to any base.
     *
     * @param integral The integral part in decimal
     * @param fractional The fractional part in decimal (nullable)
     * @param toBase The target base
     * @param decimalPlaces Maximum decimal places for fractional part
     * @return The converted number as a string
     */
    fun fromBaseTen(
        integral: BigInteger,
        fractional: BigDecimal?,
        toBase: NumberBase,
        decimalPlaces: Int = 15
    ): String {
        val integralPart = convertIntegralToBase(integral, toBase)
        val fractionalPart = fractional?.let {
            convertFractionalToBase(it, toBase, decimalPlaces)
        }

        return if (fractionalPart.isNullOrEmpty()) {
            integralPart
        } else {
            "$integralPart.$fractionalPart"
        }
    }

    /**
     * Converts an integral part from decimal to the target base.
     */
    fun convertIntegralToBase(decimal: BigInteger, toBase: NumberBase): String {
        if (decimal == BigInteger.ZERO) return "0"

        val result = StringBuilder()
        var quotient = decimal
        val base = toBase.value.toBigInteger()

        while (quotient > BigInteger.ZERO) {
            val remainder = quotient.mod(base)
            result.insert(0, digitValueToChar(remainder.toInt()))
            quotient = quotient.divide(base)
        }

        return result.toString()
    }

    /**
     * Converts a fractional part from decimal to the target base.
     */
    fun convertFractionalToBase(
        decimal: BigDecimal,
        toBase: NumberBase,
        decimalPlaces: Int
    ): String {
        if (decimal == BigDecimal.ZERO) return ""

        val result = StringBuilder()
        var fraction = decimal
        val base = toBase.value.toBigDecimal()
        var places = 0

        while (fraction > BigDecimal.ZERO && places < decimalPlaces) {
            fraction = fraction.multiply(base)
            val digit = fraction.toInt()
            result.append(digitValueToChar(digit))
            fraction = fraction.subtract(digit.toBigDecimal())
            places++
        }

        // Remove trailing zeros
        return result.toString().trimEnd('0')
    }

    /**
     * Converts a character to its digit value in the given base.
     *
     * @param char The character (0-9, A-Z)
     * @param base The number base
     * @return The digit value
     * @throws IllegalArgumentException if character is invalid for the base
     */
    private fun charToDigitValue(char: Char, base: NumberBase): Int {
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
     * Converts a digit value to its character representation.
     *
     * @param value The digit value (0-35)
     * @return The character representation (0-9, A-Z)
     */
    private fun digitValueToChar(value: Int): Char {
        return when (value) {
            in 0..9 -> '0' + value
            in 10..35 -> 'A' + (value - 10)
            else -> throw IllegalArgumentException("Invalid digit value: $value")
        }
    }

    /**
     * Validates if the input string is valid for the given base.
     *
     * @param input The input string
     * @param base The number base
     * @return true if valid, false otherwise
     */
    fun isValidInput(input: String, base: NumberBase): Boolean {
        if (input.isBlank()) return false

        val validChars = when (base) {
            NumberBase.BINARY -> "01."
            NumberBase.OCTAL -> "01234567."
            NumberBase.DECIMAL -> "0123456789."
            NumberBase.HEXADECIMAL -> "0123456789ABCDEFabcdef."
        }

        // Check if all characters are valid
        if (!input.all { it in validChars }) return false

        // Check for multiple decimal points
        if (input.count { it == '.' } > 1) return false

        // Check for empty parts
        val parts = input.split(".")
        if (parts.any { it.isEmpty() }) return false

        return true
    }
}
