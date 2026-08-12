package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Handles conversions from decimal (base 10) to other bases.
 */
object DecimalConverter {

    /**
     * Converts a decimal number to binary (base 2).
     */
    fun toBinary(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = parseDecimal(input)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.BINARY, decimalPlaces)
    }

    /**
     * Converts a decimal number to octal (base 8).
     */
    fun toOctal(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = parseDecimal(input)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.OCTAL, decimalPlaces)
    }

    /**
     * Converts a decimal number to hexadecimal (base 16).
     */
    fun toHexadecimal(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = parseDecimal(input)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.HEXADECIMAL, decimalPlaces)
    }

    /**
     * Parses a decimal string into integral and fractional parts.
     */
    private fun parseDecimal(input: String): Pair<BigInteger, BigDecimal?> {
        val parts = input.split(".")
        val integral = BigInteger(parts[0])
        val fractional = parts.getOrNull(1)?.let {
            BigDecimal("0.$it")
        }
        return Pair(integral, fractional)
    }
}
