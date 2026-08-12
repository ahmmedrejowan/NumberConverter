package com.rejowan.numberconverter.util

import com.rejowan.numberconverter.domain.model.NumberBase

object Validator {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateInput(input: String, base: NumberBase): ValidationResult {
        if (input.isEmpty()) {
            return ValidationResult(false, "Please enter a number")
        }

        if (input == "." || input == "-" || input == "-.") {
            return ValidationResult(false, "Invalid input")
        }

        val validChars = base.getValidChars()
        var decimalPointCount = 0
        var hasMinusSign = false

        for ((index, char) in input.withIndex()) {
            when {
                char == '.' -> {
                    decimalPointCount++
                    if (decimalPointCount > 1) {
                        return ValidationResult(false, "Multiple decimal points not allowed")
                    }
                }
                char == '-' -> {
                    if (index != 0) {
                        return ValidationResult(false, "Minus sign must be at the beginning")
                    }
                    hasMinusSign = true
                }
                !validChars.contains(char, ignoreCase = true) -> {
                    return ValidationResult(
                        false,
                        "Invalid character '$char' for ${base.displayName}"
                    )
                }
            }
        }

        // Check if there's at least one digit
        val hasDigits = input.any { validChars.contains(it, ignoreCase = true) }
        if (!hasDigits) {
            return ValidationResult(false, "Please enter at least one digit")
        }

        return ValidationResult(true)
    }

    fun isValidChar(char: Char, base: NumberBase): Boolean {
        return char == '.' || char == '-' || base.getValidChars().contains(char, ignoreCase = true)
    }

    fun filterInvalidChars(input: String, base: NumberBase): String {
        val validChars = base.getValidChars()
        val result = StringBuilder()
        var hasDecimalPoint = false
        var hasMinusSign = false

        for ((index, char) in input.withIndex()) {
            when {
                char == '-' && index == 0 && !hasMinusSign -> {
                    result.append(char)
                    hasMinusSign = true
                }
                char == '.' && !hasDecimalPoint -> {
                    result.append(char)
                    hasDecimalPoint = true
                }
                validChars.contains(char, ignoreCase = true) -> {
                    result.append(char)
                }
            }
        }

        return result.toString()
    }
}
