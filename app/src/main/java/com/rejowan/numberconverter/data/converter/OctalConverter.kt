package com.rejowan.numberconverter.data.converter

import com.rejowan.numberconverter.domain.model.NumberBase

/**
 * Handles conversions from octal (base 8) to other bases.
 */
object OctalConverter {

    /**
     * Converts an octal number to decimal (base 10).
     */
    fun toDecimal(input: String): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.OCTAL)
        return if (fractional != null && fractional.compareTo(java.math.BigDecimal.ZERO) > 0) {
            "$integral.$fractional".trimEnd('0').trimEnd('.')
        } else {
            integral.toString()
        }
    }

    /**
     * Converts an octal number to binary (base 2).
     */
    fun toBinary(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.OCTAL)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.BINARY, decimalPlaces)
    }

    /**
     * Converts an octal number to hexadecimal (base 16).
     */
    fun toHexadecimal(input: String, decimalPlaces: Int = 15): String {
        val (integral, fractional) = BaseConverter.toBaseTen(input, NumberBase.OCTAL)
        return BaseConverter.fromBaseTen(integral, fractional, NumberBase.HEXADECIMAL, decimalPlaces)
    }
}
