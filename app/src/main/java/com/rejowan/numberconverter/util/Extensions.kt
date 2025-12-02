package com.rejowan.numberconverter.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.rejowan.numberconverter.domain.model.NumberBase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.isValidForBase(base: NumberBase): Boolean {
    if (this.isEmpty()) return true

    val validChars = base.getValidChars()
    var decimalPointCount = 0

    for (char in this) {
        if (char == '.') {
            decimalPointCount++
            if (decimalPointCount > 1) return false
        } else if (!validChars.contains(char, ignoreCase = true)) {
            return false
        }
    }
    return true
}

fun String.removeLeadingZeros(): String {
    if (this.isEmpty()) return "0"

    val parts = this.split(".")
    val integerPart = parts[0].trimStart('0').ifEmpty { "0" }

    return if (parts.size > 1) {
        "$integerPart.${parts[1]}"
    } else {
        integerPart
    }
}

fun String.formatWithPrefix(base: NumberBase): String {
    return "${base.prefix}$this"
}

fun Long.toFormattedDate(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(this))
}

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> toFormattedDate()
    }
}

fun Context.copyToClipboard(text: String, label: String = "Copied") {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Int.toOrdinal(): String {
    return when {
        this % 100 in 11..13 -> "${this}th"
        this % 10 == 1 -> "${this}st"
        this % 10 == 2 -> "${this}nd"
        this % 10 == 3 -> "${this}rd"
        else -> "${this}th"
    }
}

fun Float.toPercentageString(): String {
    return "${(this * 100).toInt()}%"
}
