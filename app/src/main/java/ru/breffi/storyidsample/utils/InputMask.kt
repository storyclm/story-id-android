package ru.breffi.storyidsample.utils

import android.widget.EditText
import android.widget.TextView
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

fun EditText.applyMask(mask: String, nonTerminated: Boolean = false) {
    val slots = UnderscoreDigitSlotsParser().parseSlots(mask)
    if (nonTerminated) {
        MaskFormatWatcher(MaskImpl.createNonTerminated(slots)).installOn(this)
    } else {
        MaskFormatWatcher(MaskImpl.createTerminated(slots)).installOn(this)
    }
}

fun TextView.applyMask(mask: String, nonTerminated: Boolean = false) {
    val slots = UnderscoreDigitSlotsParser().parseSlots(mask)
    if (nonTerminated) {
        MaskFormatWatcher(MaskImpl.createNonTerminated(slots)).installOn(this)
    } else {
        MaskFormatWatcher(MaskImpl.createTerminated(slots)).installOn(this)
    }
}