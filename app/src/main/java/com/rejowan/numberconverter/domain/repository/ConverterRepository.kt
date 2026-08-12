package com.rejowan.numberconverter.domain.repository

import com.rejowan.numberconverter.domain.model.ConversionResult
import com.rejowan.numberconverter.domain.model.Explanation
import com.rejowan.numberconverter.domain.model.NumberBase

interface ConverterRepository {
    suspend fun convert(input: String, fromBase: NumberBase, toBase: NumberBase): Result<ConversionResult>
    suspend fun explain(input: String, fromBase: NumberBase, toBase: NumberBase): Result<Explanation>
}
