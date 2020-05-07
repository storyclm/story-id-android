package ru.breffi.storyidsample.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.view.children

class SnappingHorizontalScrollView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : HorizontalScrollView(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null, 0, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    private val runnable = Runnable {
        getChildAt(0)?.let { view ->
            (view as? ViewGroup)?.let { vg ->
                val children = vg.children
                if (children.count() > 0) {
                    val x = (children.find { (it.x + it.width / 2) - scrollX > 0 } ?: children.last())
                            .x.toInt() - snapOffset
                    smoothScrollTo(x, 0)
                }
            }
        }
    }

    var snapOffset: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    removeCallbacks(runnable)
                    postDelayed(runnable, 500)
                }
                MotionEvent.ACTION_DOWN -> {
                    removeCallbacks(runnable)
                }
                else -> {}
            }
        }
        return super.onTouchEvent(ev)
    }
}