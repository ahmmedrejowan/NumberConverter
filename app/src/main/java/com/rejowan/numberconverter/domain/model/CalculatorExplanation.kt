package com.rejowan.numberconverter.domain.model

import androidx.compose.ui.text.AnnotatedString

data class CalculatorExplanation(
    val title: String,
    val input1Conversion: ExplanationPart?,
    val input2Conversion: ExplanationPart?,
    val operation: OperationExplanation,
    val outputConversion: ExplanationPart?,
    val summary: AnnotatedString
)

data class OperationExplanation(
    val title: String,
    val description: AnnotatedString,
    val result: String
)
