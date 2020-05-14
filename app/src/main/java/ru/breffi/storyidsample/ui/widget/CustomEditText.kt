package ru.breffi.storyidsample.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import ru.breffi.storyidsample.R

class CustomEditText : TextInputEditText {

    private var colorNormal: Int = 0

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {
        colorNormal = currentTextColor
//        background = ContextCompat.getDrawable(context, R.drawable.edittext_normal)
    }


    fun setError(isError: Boolean) {
        if (!isError) {
            background = ContextCompat.getDrawable(context, R.drawable.edittext_normal)
            setTextColor(Color.parseColor(String.format("#%06X", 0xFFFFFF and colorNormal)))
        } else {
            background = ContextCompat.getDrawable(context, R.drawable.edittext_error)
            setTextColor(ContextCompat.getColor(context, R.color.edit_text_error_color))
            shakeAnimation()
        }
    }

    private fun shakeAnimation() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        startAnimation(shake)
    }

}
