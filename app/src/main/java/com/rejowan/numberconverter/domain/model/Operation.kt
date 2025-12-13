package com.rejowan.numberconverter.domain.model

enum class Operation(
    val symbol: String,
    val displayName: String
) {
    ADD("+", "Addition"),
    SUBTRACT("-", "Subtraction"),
    MULTIPLY("×", "Multiplication"),
    DIVIDE("÷", "Division")
}
