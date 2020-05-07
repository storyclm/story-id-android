package ru.breffi.storyidsample.ui.widget.vercode

import androidx.annotation.ColorRes

interface VerificationAction {

    fun setFigures(figures: Int)

    fun setVerCodeMargin(margin: Int)

    fun setBottomSelectedColor(@ColorRes bottomSelectedColor: Int)

    fun setBottomNormalColor(@ColorRes bottomNormalColor: Int)

    fun setSelectedBackgroundColor(@ColorRes selectedBackground: Int)

    fun setBottomLineHeight(bottomLineHeight: Int)

    fun setOnVerificationCodeChangedListener(listener: OnVerificationCodeChangedListener)

    interface OnVerificationCodeChangedListener {

        fun onVerCodeChanged(s: CharSequence, start: Int, before: Int, count: Int)

        fun onInputCompleted(s: CharSequence)
    }
}