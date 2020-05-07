package ru.breffi.storyidsample.ui.widget.vercode

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import ru.breffi.storyidsample.R
import java.util.*


class VerificationCodeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr), VerificationAction, TextWatcher {

    private var mFigures: Int = 0
    private var mVerCodeMargin: Int = 0
    private var mBottomSelectedColor: Int = 0
    private var mBottomNormalColor: Int = 0
    private var mBottomLineHeight: Float = 0.toFloat()
    private var mBackgroundColor: Int = 0
    private var mSelectedBackgroundColor: Int = 0
    private var mCursorWidth: Int = 0
    private var mCursorColor: Int = 0
    private var mCursorDuration: Int = 0
    private var textColorNormal: Int = 0

    private var onCodeChangedListener: VerificationAction.OnVerificationCodeChangedListener? = null
    private var mCurrentPosition = 0
    private var mEachRectLength = 0
    private var mMarginsBackgroundPaint: Paint? = null
    private var mSelectedBackgroundPaint: Paint? = null
    private var mErrorBackgroundPaint: Paint? = null
    private var mNormalBackgroundPaint: Paint? = null
    private var mBottomSelectedPaint: Paint? = null
    private var mBottomNormalPaint: Paint? = null
    private var mCursorPaint: Paint? = null

    private var isErrorMode: Boolean = false

    //Мигание курсора
    private var isCursorShowing: Boolean = false
    private var mCursorTimerTask: TimerTask? = null
    private var mCursorTimer: Timer? = null

    init {
        initAttrs(attrs)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        initPaint()
        initCursorTimer()
        isFocusableInTouchMode = true
        super.addTextChangedListener(this)
    }

    private fun initPaint() {
        mSelectedBackgroundPaint = Paint()
        mSelectedBackgroundPaint!!.color = mSelectedBackgroundColor
        mErrorBackgroundPaint = Paint()
        mMarginsBackgroundPaint = Paint()
        mMarginsBackgroundPaint!!.color = ContextCompat.getColor(context, R.color.background)
        mNormalBackgroundPaint = Paint()
        mNormalBackgroundPaint!!.color = mBackgroundColor

        mBottomSelectedPaint = Paint()
        mBottomNormalPaint = Paint()
        mBottomSelectedPaint!!.color = mBottomSelectedColor
        mBottomNormalPaint!!.color = mBottomNormalColor
        mBottomSelectedPaint!!.strokeWidth = mBottomLineHeight
        mBottomNormalPaint!!.strokeWidth = mBottomLineHeight

        mCursorPaint = Paint()
        mCursorPaint!!.isAntiAlias = true
        mCursorPaint!!.color = mCursorColor
        mCursorPaint!!.style = Paint.Style.FILL_AND_STROKE
        mCursorPaint!!.strokeWidth = mCursorWidth.toFloat()

        textColorNormal = currentTextColor
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VerCodeEditText)
        mFigures = ta.getInteger(R.styleable.VerCodeEditText_figures, 4)
        mVerCodeMargin = ta.getDimension(R.styleable.VerCodeEditText_verCodeMargin, 0f).toInt()
        mBottomSelectedColor = ta.getColor(
            R.styleable.VerCodeEditText_bottomLineSelectedColor,
            currentTextColor
        )
        mBottomNormalColor = ta.getColor(
            R.styleable.VerCodeEditText_bottomLineNormalColor,
            getColor(android.R.color.white)
        )
        mBottomLineHeight = ta.getDimension(
            R.styleable.VerCodeEditText_bottomLineHeight,
            dp2px(5).toFloat()
        )
        mBackgroundColor = ta.getColor(
            R.styleable.VerCodeEditText_verBackgroundColor,
            getColor(android.R.color.white)
        )
        mSelectedBackgroundColor = ta.getColor(
            R.styleable.VerCodeEditText_selectedBackgroundColor,
            getColor(android.R.color.white)
        )
        mCursorWidth =
            ta.getDimension(R.styleable.VerCodeEditText_cursorWidth, dp2px(1).toFloat()).toInt()
        mCursorColor =
            ta.getColor(R.styleable.VerCodeEditText_cursorColor, getColor(android.R.color.white))
        mCursorDuration =
            ta.getInteger(R.styleable.VerCodeEditText_cursorDuration, DEFAULT_CURSOR_DURATION)
        ta.recycle()

        // force LTR because of bug: https://github.com/JustKiddingBaby/VercodeEditText/issues/4
        layoutDirection = View.LAYOUT_DIRECTION_LTR
    }

    private fun initCursorTimer() {
        mCursorTimerTask = object : TimerTask() {
            override fun run() {
                isCursorShowing = !isCursorShowing
                postInvalidate()
            }
        }
        mCursorTimer = Timer()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mCursorTimer!!.scheduleAtFixedRate(mCursorTimerTask, 0, mCursorDuration.toLong())
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mCursorTimer!!.cancel()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthResult: Int
        val heightResult: Int

        //Конечная ширина
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        widthResult = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            getScreenWidth(context)
        }
        //Ширина каждого прямоугольника
        mEachRectLength = (widthResult - mVerCodeMargin * (mFigures - 1)) / mFigures
        //Конечная высота
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        heightResult = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            mEachRectLength
        }
        setMeasuredDimension(widthResult, heightResult)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            requestFocus()
            setSelection(text!!.length)
            showKeyBoard(context)
            return false
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        mCurrentPosition = text!!.length
        val width = mEachRectLength - paddingLeft - paddingRight
        val height = measuredHeight - paddingTop - paddingBottom
        for (i in 0 until mFigures) {
            canvas.save()
            val start = width * i + i * mVerCodeMargin
            val end = width + start
            //прямоугольник
            if (isErrorMode) {
                mErrorBackgroundPaint!!.style = Paint.Style.FILL
                mErrorBackgroundPaint!!.color = mBackgroundColor
                canvas.drawRect(
                    start.toFloat(),
                    0f,
                    end.toFloat(),
                    height.toFloat(),
                    mErrorBackgroundPaint!!
                )

                mErrorBackgroundPaint!!.style = Paint.Style.STROKE
                mErrorBackgroundPaint!!.color =
                    ContextCompat.getColor(context, R.color.edit_text_error_color)
                mErrorBackgroundPaint!!.strokeWidth = dp2px(2).toFloat()
                canvas.drawRect(
                    start.toFloat(),
                    0f,
                    end.toFloat(),
                    height.toFloat(),
                    mErrorBackgroundPaint!!
                )
            } else {
                if (i == mCurrentPosition) {//Выбран следующий прямоугольник
                    mSelectedBackgroundPaint!!.color = mSelectedBackgroundColor
                    canvas.drawRect(
                        start.toFloat(),
                        0f,
                        end.toFloat(),
                        height.toFloat(),
                        mSelectedBackgroundPaint!!
                    )
                } else {
                    mNormalBackgroundPaint!!.color = mBackgroundColor
                    canvas.drawRect(
                        start.toFloat(),
                        0f,
                        end.toFloat(),
                        height.toFloat(),
                        mNormalBackgroundPaint!!
                    )
                }
            }

            canvas.restore()
        }

        for (i in 0 until mFigures - 1) {
            canvas.save()
            val start = width + (width * i + i * mVerCodeMargin)
            val end = start + mVerCodeMargin
            canvas.drawRect(
                start.toFloat(),
                0f,
                end.toFloat(),
                height.toFloat(),
                mMarginsBackgroundPaint!!
            )
            canvas.restore()
        }

        //текст
        val value = text!!.toString()
        for (i in value.indices) {
            canvas.save()
            val start = width * i + i * mVerCodeMargin
            val x = (start + width / 2).toFloat()
            val paint = paint
            paint.textAlign = Paint.Align.CENTER
            paint.color = currentTextColor
            val fontMetrics = paint.fontMetrics
            val baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
            canvas.drawText(value[i].toString(), x, baseline, paint)
            canvas.restore()
        }
        //нижняя линия
        for (i in 0 until mFigures) {
            canvas.save()
            val lineY = height - mBottomLineHeight / 2
            val start = width * i + i * mVerCodeMargin
            val end = width + start
            if (i < mCurrentPosition) {
                canvas.drawLine(
                    start.toFloat(),
                    lineY,
                    end.toFloat(),
                    lineY,
                    mBottomSelectedPaint!!
                )
            } else {
                canvas.drawLine(start.toFloat(), lineY, end.toFloat(), lineY, mBottomNormalPaint!!)
            }
            canvas.restore()
        }
        //курсор
        if (!isCursorShowing && isCursorVisible && mCurrentPosition < mFigures && hasFocus()) {
            canvas.save()
            val startX = mCurrentPosition * (width + mVerCodeMargin) + width / 2
            val startY = height / 4
            val endY = height - height / 4
            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                startX.toFloat(),
                endY.toFloat(),
                mCursorPaint!!
            )
            canvas.restore()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        mCurrentPosition = text!!.length
        postInvalidate()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        mCurrentPosition = text!!.length
        postInvalidate()
        if (onCodeChangedListener != null) {
            onCodeChangedListener!!.onVerCodeChanged(text!!, start, before, count)
        }
    }

    override fun afterTextChanged(s: Editable) {
        mCurrentPosition = text!!.length
        postInvalidate()
        if (text!!.length == mFigures) {
            if (onCodeChangedListener != null) {
                onCodeChangedListener!!.onInputCompleted(text!!)
            }
        } else if (text!!.length > mFigures) {
            text!!.delete(mFigures, text!!.length)
        }
    }

    override fun setFigures(figures: Int) {
        mFigures = figures
        postInvalidate()
    }

    override fun setVerCodeMargin(margin: Int) {
        mVerCodeMargin = margin
        postInvalidate()
    }

    override fun setBottomSelectedColor(@ColorRes bottomSelectedColor: Int) {
        mBottomSelectedColor = getColor(bottomSelectedColor)
        postInvalidate()
    }

    override fun setBottomNormalColor(@ColorRes bottomNormalColor: Int) {
        mBottomSelectedColor = getColor(bottomNormalColor)
        postInvalidate()
    }

    override fun setSelectedBackgroundColor(@ColorRes selectedBackground: Int) {
        mSelectedBackgroundColor = getColor(selectedBackground)
        postInvalidate()
    }

    override fun setBottomLineHeight(bottomLineHeight: Int) {
        this.mBottomLineHeight = bottomLineHeight.toFloat()
        postInvalidate()
    }

    override fun setOnVerificationCodeChangedListener(listener: VerificationAction.OnVerificationCodeChangedListener) {
        this.onCodeChangedListener = listener
    }

    private fun getColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun showKeyBoard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

    fun setError(isError: Boolean) {
        if (!isError) {
            isErrorMode = false
            setTextColor(Color.parseColor(String.format("#%06X", (0xFFFFFF and textColorNormal))))
        } else {
            isErrorMode = true
            setTextColor(ContextCompat.getColor(context, R.color.edit_text_error_color))
            shakeAnimation()
        }
    }

    private fun shakeAnimation() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        startAnimation(shake)
    }

    companion object {
        private const val DEFAULT_CURSOR_DURATION = 400

        internal fun getScreenWidth(context: Context): Int {
            val metrics = DisplayMetrics()
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }
    }
}