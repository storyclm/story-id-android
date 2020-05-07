package ru.breffi.storyidsample.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.digit_keyboard.view.*
import ru.breffi.storyidsample.R

class DigitKeyboard : ConstraintLayout {

    var onDigitInput: (i: Int) -> Unit = {}


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val view = LayoutInflater.from(context).inflate(R.layout.digit_keyboard, this, true)

        view.digit1.onClick = {
            onDigitInput.invoke(1)
        }

        view.digit2.onClick = {
            onDigitInput.invoke(2)
        }

        view.digit3.onClick = {
            onDigitInput.invoke(3)
        }

        view.digit4.onClick = {
            onDigitInput.invoke(4)
        }

        view.digit5.onClick = {
            onDigitInput.invoke(5)
        }

        view.digit6.onClick = {
            onDigitInput.invoke(6)
        }

        view.digit7.onClick = {
            onDigitInput.invoke(7)
        }

        view.digit8.onClick = {
            onDigitInput.invoke(8)
        }

        view.digit9.onClick = {
            onDigitInput.invoke(9)
        }

        view.digit0.onClick = {
            onDigitInput.invoke(0)
        }

    }

}