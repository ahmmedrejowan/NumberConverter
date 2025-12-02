package com.rejowan.numberconverter.domain.usecase.converter

import com.rejowan.numberconverter.data.converter.BaseConverter
import com.rejowan.numberconverter.domain.model.NumberBase

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

class ValidateInputUseCase {
    operator fun invoke(input: String, base: NumberBase): ValidationResult {
        if (input.isBlank()) {
            return ValidationResult(false, "Input cannot be empty")
        }

        if (!BaseConverter.isValidInput(input, base)) {
            return ValidationResult(false, "Invalid input for ${base.displayName}")
        }

        return ValidationResult(true)
    }
}
