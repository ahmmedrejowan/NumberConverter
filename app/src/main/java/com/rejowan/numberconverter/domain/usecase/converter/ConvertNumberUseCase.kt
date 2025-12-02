package com.rejowan.numberconverter.domain.usecase.converter

import com.rejowan.numberconverter.domain.model.ConversionResult
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.repository.ConverterRepository

class ConvertNumberUseCase(
    private val repository: ConverterRepository
) {
    suspend operator fun invoke(
        input: String,
        fromBase: NumberBase,
        toBase: NumberBase
    ): Result<ConversionResult> {
        if (input.isBlank()) {
            return Result.failure(IllegalArgumentException("Input cannot be empty"))
        }

        return repository.convert(input, fromBase, toBase)
    }
}
