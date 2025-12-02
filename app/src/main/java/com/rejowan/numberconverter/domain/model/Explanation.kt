package com.rejowan.numberconverter.domain.model

import androidx.compose.ui.text.AnnotatedString

data class Explanation(
    val title: String,
    val integralPart: ExplanationPart?,
    val fractionalPart: ExplanationPart?,
    val summary: AnnotatedString
)

data class ExplanationPart(
    val title: String,
    val steps: List<Step>,
    val result: AnnotatedString
)

data class Step(
    val stepNumber: Int,
    val description: AnnotatedString,
    val calculation: AnnotatedString? = null,
    val result: String? = null
)
