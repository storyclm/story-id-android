package ru.breffi.storyidsample.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyidsample.R

class StaticHintEditText : LinearLayout {

    lateinit var view: View
    var wasChanged: Boolean = false
    private var colorNormal: Int = 0

    constructor(context: Context) : super(context) {
        initialize(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        view = LayoutInflater.from(context).inflate(R.layout.static_hint_edittext, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.StaticHintEditText, 0, 0)

            view.hint.text = a.getText(R.styleable.StaticHintEditText_hint)
            view.text.setText(a.getText(R.styleable.StaticHintEditText_text))
            view.text.hint = a.getText(R.styleable.StaticHintEditText_textHint)
            view.line.setBackgroundColor(getColor(context, R.color.light_red))

            colorNormal = view.text.currentTextColor

            if (view.text.text == null || view.text.text.isEmpty()) {
                view.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_13))
            }

            view.text.addTextChangedListener {
                if (it == null || it.isEmpty()) {
                    //text size for hint
                    view.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_13))
                } else {
                    view.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_20))
                    //text size for text
                }
            }

            view.text.addTextChangedListener {
                wasChanged = true
            }

            a.recycle()
        }
    }

    fun setText(text: String?) {
        view.text.setText(text)
        wasChanged = false
    }

    fun getText(): String {
        return if (view.text.text.toString().isEmpty()) {
            ""
        } else {
            view.text.text.toString()
        }
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener) {
        view.text.onFocusChangeListener = l
    }

    fun setError(isError: Boolean) {
        if (!isError) {
            view.line.setBackgroundColor(getColor(context, R.color.light_red))
            view.text.setTextColor(Color.parseColor(String.format("#%06X", 0xFFFFFF and colorNormal)))
        } else {
            view.line.setBackgroundColor(Color.parseColor("#FB0000"))
            view.text.setTextColor(ContextCompat.getColor(context, R.color.edit_text_error_color))
        }
    }

    fun isEmpty(): Boolean {
        if (text.text == null) {
            return true
        }

        return text.text.isEmpty()
    }

}