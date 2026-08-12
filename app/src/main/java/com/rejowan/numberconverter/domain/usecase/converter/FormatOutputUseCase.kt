package com.rejowan.numberconverter.domain.usecase.converter

class FormatOutputUseCase {
    operator fun invoke(output: String, maxDecimalPlaces: Int? = null): String {
        if (!output.contains('.')) {
            return output
        }

        val parts = output.split('.')
        val integral = parts[0]
        var fractional = parts[1]

        // Limit decimal places if specified
        if (maxDecimalPlaces != null && fractional.length > maxDecimalPlaces) {
            fractional = fractional.substring(0, maxDecimalPlaces)
        }

        // Strip trailing zeros
        fractional = fractional.trimEnd('0')

        return if (fractional.isEmpty()) {
            integral
        } else {
            "$integral.$fractional"
        }
    }
}
