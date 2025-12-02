package com.rejowan.numberconverter.presentation.converter.state

import com.rejowan.numberconverter.domain.model.NumberBase

data class ConverterUiState(
    val input: String = "",
    val output: String = "",
    val fromBase: NumberBase = NumberBase.DECIMAL,
    val toBase: NumberBase = NumberBase.BINARY,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val validationError: String? = null
)
