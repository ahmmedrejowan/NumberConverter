package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase

/**
 * Handles conversions from binary (base 2) to other bases.
 */
object BinaryConverter {

    /**
     * Converts a binary number to decimal (base 10).
     */
    fun toDecimal(input: String): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.BINARY)
        return if (fractional != null && fractional.compareTo(java.math.BigDecimal.ZERO) > 0) {
            "$integral.$fractional".trimEnd('0').trimEnd('.')
        } else {
            integral.toString()
        }
    }

    /**
     * Converts a binary number to octal (base 8).
     */
    fun toOctal(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.BINARY)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.OCTAL, decimalPlaces)
    }

    /**
     * Converts a binary number to hexadecimal (base 16).
     */
    fun toHexadecimal(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.BINARY)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.HEXADECIMAL, decimalPlaces)
    }
}
