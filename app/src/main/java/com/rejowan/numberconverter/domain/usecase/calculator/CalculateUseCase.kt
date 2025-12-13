package com.rejowan.numberconverter.domain.usecase.calculator

import com.rejowan.numberconverter.data.converter.BaseConverter
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import java.math.BigDecimal
import java.math.RoundingMode

class CalculateUseCase {

    operator fun invoke(
        input1: String,
        input1Base: NumberBase,
        input2: String,
        input2Base: NumberBase,
        operation: Operation,
        outputBase: NumberBase,
        decimalPlaces: Int = 15
    ): Result<String> {
        return try {
            // Convert both inputs to decimal (base 10)
            val (integral1, fractional1) = BaseConverter.toBaseTen(input1, input1Base)
            val (integral2, fractional2) = BaseConverter.toBaseTen(input2, input2Base)

            // Combine integral and fractional parts into BigDecimal for arithmetic
            val value1 = combineToBigDecimal(integral1, fractional1)
            val value2 = combineToBigDecimal(integral2, fractional2)

            // Perform the arithmetic operation
            val result = when (operation) {
                Operation.ADD -> value1.add(value2)
                Operation.SUBTRACT -> value1.subtract(value2)
                Operation.MULTIPLY -> value1.multiply(value2)
                Operation.DIVIDE -> {
                    if (value2.compareTo(BigDecimal.ZERO) == 0) {
                        return Result.failure(ArithmeticException("Division by zero"))
                    }
                    value1.divide(value2, decimalPlaces + 5, RoundingMode.HALF_UP)
                }
            }

            // Convert result to the output base
            val output = convertToBase(result, outputBase, decimalPlaces)
            Result.success(output)
        } catch (e: Exception) {
            Result.failure(e)
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

    private fun convertToBase(
        value: BigDecimal,
        toBase: NumberBase,
        decimalPlaces: Int
    ): String {
        val isNegative = value < BigDecimal.ZERO
        val absoluteValue = value.abs()

        // Split into integral and fractional parts
        val integral = absoluteValue.toBigInteger()
        val fractional = absoluteValue.subtract(BigDecimal(integral))

        // Convert to target base
        val result = BaseConverter.fromBaseTen(
            integral = integral,
            fractional = if (fractional > BigDecimal.ZERO) fractional else null,
            toBase = toBase,
            decimalPlaces = decimalPlaces
        )

        return if (isNegative && result != "0") "-$result" else result
    }
}
