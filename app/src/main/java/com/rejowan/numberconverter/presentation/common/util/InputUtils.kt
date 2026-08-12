package com.rejowan.numberconverter.presentation.common.util

import androidx.compose.ui.text.input.KeyboardType
import com.rejowan.numberconverter.domain.model.NumberBase

/**
 * Soft-keyboard type for a base's input field. Hex needs the full text keyboard
 * for A–F; every other base is digits-only.
 */
fun keyboardTypeFor(base: NumberBase): KeyboardType = when (base) {
    NumberBase.BINARY, NumberBase.OCTAL, NumberBase.DECIMAL -> KeyboardType.Number
    NumberBase.HEXADECIMAL -> KeyboardType.Text
}
