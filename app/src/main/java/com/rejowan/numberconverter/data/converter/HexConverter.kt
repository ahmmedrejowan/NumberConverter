package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase

/**
 * Handles conversions from hexadecimal (base 16) to other bases.
 */
object HexConverter {

    /**
     * Converts a hexadecimal number to decimal (base 10).
     */
    fun toDecimal(input: String): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.HEXADECIMAL)
        return if (fractional != null && fractional.compareTo(java.math.BigDecimal.ZERO) > 0) {
            "$integral.$fractional".trimEnd('0').trimEnd('.')
        } else {
            integral.toString()
        }
    }

    /**
     * Converts a hexadecimal number to binary (base 2).
     */
    fun toBinary(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.HEXADECIMAL)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.BINARY, decimalPlaces)
    }

    /**
     * Converts a hexadecimal number to octal (base 8).
     */
    fun toOctal(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.HEXADECIMAL)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.OCTAL, decimalPlaces)
    }
}
