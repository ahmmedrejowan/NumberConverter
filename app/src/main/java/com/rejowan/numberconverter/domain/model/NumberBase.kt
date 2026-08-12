package com.rejowan.numberconverter.domain.model

enum class NumberBase(
    val value: Int,
    val displayName: String,
    val prefix: String
) {
    BINARY(2, "Binary", "0b"),
    OCTAL(8, "Octal", "0o"),
    DECIMAL(10, "Decimal", ""),
    HEXADECIMAL(16, "Hexadecimal", "0x");

    fun getValidChars(): String = when (this) {
        BINARY -> "01"
        OCTAL -> "01234567"
        DECIMAL -> "0123456789"
        HEXADECIMAL -> "0123456789ABCDEFabcdef"
    }

    fun isValidChar(char: Char): Boolean {
        return getValidChars().contains(char, ignoreCase = true)
    }

    /**
     * Strips everything that can't appear in a number of this base, keeping at
     * most one decimal point. Shared by every input field so the Converter and
     * Calculator can never disagree about what a user is allowed to type.
     */
    fun filterInput(raw: String): String {
        val valid = getValidChars()
        val out = StringBuilder(raw.length)
        var seenPoint = false
        for (char in raw) {
            when {
                char == '.' && !seenPoint -> {
                    out.append(char)
                    seenPoint = true
                }
                valid.contains(char, ignoreCase = true) -> out.append(char)
            }
        }
        return out.toString()
    }

    companion object {
        fun fromValue(value: Int): NumberBase = entries.first { it.value == value }
    }
}
