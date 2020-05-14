package ru.breffi.storyidsample.utils

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher

class PhoneNumberWatcher : TextWatcher {

    private var formatting: Boolean = false
    private var removing = false

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        removing = after < count
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!formatting) {
            formatting = true
            var formatted = s.toString()
            formatted = formatted.getDigits()
            var inx = s.absIndex(formatted)
            formatted = formatted.formattingPhone()
            if (inx == 0) {
                inx += 2
            } else if (inx < 3) {
                inx += 4
            } else if (inx == 3 && removing) {
                inx += 4
            } else if (inx < 7) {
                inx += 6
            } else if (inx < 9) {
                inx += 7
            } else if (inx < 11) {
                inx += 8
            }

            s.clear()

            s.append(formatted)
            Selection.setSelection(s, inx)
            formatting = false
        }
    }
}

private fun Editable.absIndex(s: String): Int {
    val index = Selection.getSelectionEnd(this)
    if (s.isEmpty()) {
        return 0
    }
    val sub: String
    sub = if (index == length) {
        toString().getDigits()
    } else {
        toString().substring(0, index).getDigits()
    }

    return sub.length
}

fun String.getDigits(): String {
    var s = this
    return if (s.length > 1) {
        s = s.replace("+7", "7")
        s.getOnlyNumeric()
    } else {
        s.replace("+", "")
    }
}

private fun String.formattingPhone(): String {
    var formatted = this
    when {
        formatted.isEmpty() -> formatted = "+7"
        formatted.length < 4 -> formatted = String.format("+7 (%s) ", formatted)
        formatted.length < 7 -> formatted = String.format(
            "+7 (%s) %s",
            formatted.substring(0, 3),
            formatted.substring(3, formatted.length)
        )
        formatted.length < 9 -> formatted = String.format(
            "+7 (%s) %s-%s",
            formatted.substring(0, 3),
            formatted.substring(3, 6),
            formatted.substring(6, formatted.length)
        )
        formatted.length < 11 -> formatted = String.format(
            "+7 (%s) %s-%s-%s",
            formatted.substring(0, 3),
            formatted.substring(3, 6),
            formatted.substring(6, 8),
            formatted.substring(8, formatted.length)
        )
        formatted.length == 11 -> formatted = String.format(
            "+7 (%s) %s-%s-%s",
            formatted.substring(1, 4),
            formatted.substring(4, 7),
            formatted.substring(7, 9),
            formatted.substring(9, 11)
        )
    }

    return formatted
}

fun String.stringAsPhone(): String {
    var f = getDigits()
    if (f.length == 11 && f[0] == '7') {
        f = f.substring(1)
    }
    return f.formattingPhone()
}

private fun String.getOnlyNumeric(): String {
    return replace("[^\\d]".toRegex(), "")
}