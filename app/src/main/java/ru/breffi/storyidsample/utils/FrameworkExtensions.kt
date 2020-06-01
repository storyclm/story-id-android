package ru.breffi.storyidsample.utils

import android.widget.Button

fun Button.setButtonEnabled(enabled: Boolean) {
    alpha = if (enabled) {
        1f
    } else {
        0.4f
    }
    isEnabled = enabled
}

fun <T> T?.orNull(): T? where T : CharSequence {
    return if (this.isNullOrEmpty()) null else this
}
