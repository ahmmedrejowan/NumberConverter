package com.rejowan.numberconverter.domain.model

data class ConversionResult(
    val input: String,
    val output: String,
    val fromBase: NumberBase,
    val toBase: NumberBase
)
