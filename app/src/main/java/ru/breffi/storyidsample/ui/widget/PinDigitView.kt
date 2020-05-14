package ru.breffi.storyidsample.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.digit_view.view.*
import ru.breffi.storyidsample.R

class PinDigitView : LinearLayout {

    var onClick: (i: Int) -> Unit = {}

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
        val view = LayoutInflater.from(context).inflate(R.layout.digit_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.PinDigitView, 0, 0)

            digit.text = a.getText(R.styleable.PinDigitView_number)
            chars.text = a.getText(R.styleable.PinDigitView_chars)

            view.digitView.setOnClickListener {
                onClick.invoke(digit.text[0].toInt())
            }

            a.recycle()
        }
    }


}