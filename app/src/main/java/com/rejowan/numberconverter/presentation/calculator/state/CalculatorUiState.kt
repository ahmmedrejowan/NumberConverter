package com.rejowan.numberconverter.presentation.calculator.state

import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation

data class CalculatorUiState(
    val input1: String = "",
    val input2: String = "",
    val input1Base: NumberBase = NumberBase.DECIMAL,
    val input2Base: NumberBase = NumberBase.DECIMAL,
    val outputBase: NumberBase = NumberBase.DECIMAL,
    val operation: Operation = Operation.ADD,
    val output: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val validation1Error: String? = null,
    val validation2Error: String? = null
)
