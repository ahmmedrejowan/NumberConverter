package com.rejowan.numberconverter.domain.model

data class HistoryItem(
    val id: Long = 0,
    val input: String,
    val output: String,
    val fromBase: NumberBase,
    val toBase: NumberBase,
    val timestamp: Long,
    val isBookmarked: Boolean = false
)
