package com.rejowan.numberconverter.data.repository

import androidx.compose.ui.text.AnnotatedString
import com.rejowan.numberconverter.data.converter.BaseConverter
import com.rejowan.numberconverter.data.converter.BinaryConverter
import com.rejowan.numberconverter.data.converter.DecimalConverter
import com.rejowan.numberconverter.data.converter.HexConverter
import com.rejowan.numberconverter.data.converter.OctalConverter
import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.domain.model.ConversionResult
import com.rejowan.numberconverter.domain.model.Explanation
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.repository.ConverterRepository
import kotlinx.coroutines.flow.first

class ConverterRepositoryImpl(
    private val preferencesManager: PreferencesManager
) : ConverterRepository {

    override suspend fun convert(
        input: String,
        fromBase: NumberBase,
        toBase: NumberBase
    ): Result<ConversionResult> {
        return try {
            // Validate input
            if (!BaseConverter.isValidInput(input, fromBase)) {
                return Result.failure(IllegalArgumentException("Invalid input for base $fromBase"))
            }

            // Same base conversion (just return input)
            if (fromBase == toBase) {
                return Result.success(
                    ConversionResult(
                        input = input,
                        output = input,
                        fromBase = fromBase,
                        toBase = toBase
                    )
                )
            }

            // Get decimal places from preferences
            val decimalPlaces = preferencesManager.decimalPlaces.first()

            // Perform conversion
            val output = performConversion(input, fromBase, toBase, decimalPlaces)

            Result.success(
                ConversionResult(
                    input = input,
                    output = output,
                    fromBase = fromBase,
                    toBase = toBase
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun explain(
        input: String,
        fromBase: NumberBase,
        toBase: NumberBase
    ): Result<Explanation> {
        return try {
            // Basic explanation placeholder
            // Will be implemented with ExplanationGenerator later
            Result.success(
                Explanation(
                    title = "Converting $input from ${fromBase.displayName} to ${toBase.displayName}",
                    integralPart = null,
                    fractionalPart = null,
                    summary = AnnotatedString("Detailed explanation coming soon!")
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun performConversion(
        input: String,
        fromBase: NumberBase,
        toBase: NumberBase,
        decimalPlaces: Int
    ): String {
        return when (fromBase) {
            NumberBase.BINARY -> when (toBase) {
                NumberBase.DECIMAL -> BinaryConverter.toDecimal(input)
                NumberBase.OCTAL -> BinaryConverter.toOctal(input, decimalPlaces)
                NumberBase.HEXADECIMAL -> BinaryConverter.toHexadecimal(input, decimalPlaces)
                else -> input
            }
            NumberBase.OCTAL -> when (toBase) {
                NumberBase.BINARY -> OctalConverter.toBinary(input, decimalPlaces)
                NumberBase.DECIMAL -> OctalConverter.toDecimal(input)
                NumberBase.HEXADECIMAL -> OctalConverter.toHexadecimal(input, decimalPlaces)
                else -> input
            }
            NumberBase.DECIMAL -> when (toBase) {
                NumberBase.BINARY -> DecimalConverter.toBinary(input, decimalPlaces)
                NumberBase.OCTAL -> DecimalConverter.toOctal(input, decimalPlaces)
                NumberBase.HEXADECIMAL -> DecimalConverter.toHexadecimal(input, decimalPlaces)
                else -> input
            }
            NumberBase.HEXADECIMAL -> when (toBase) {
                NumberBase.BINARY -> HexConverter.toBinary(input, decimalPlaces)
                NumberBase.OCTAL -> HexConverter.toOctal(input, decimalPlaces)
                NumberBase.DECIMAL -> HexConverter.toDecimal(input)
                else -> input
            }
        }
    }
}
