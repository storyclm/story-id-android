package ru.breffi.storyidsample.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.breffi.storyidsample.R

fun TextView.setTextViewWordClickable(textResId: Int, onClickListener: View.OnClickListener) {
    var text = context.getString(textResId)
    val start = text.indexOf("<click>")
    text = text.replace("<click>".toRegex(), "")
    val end = text.indexOf("</click>")
    text = text.replace("</click>".toRegex(), "")

    val ss = SpannableString(text)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(view: View) {
            onClickListener.onClick(view)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }
    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    //ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    setText(ss)
    movementMethod = LinkMovementMethod.getInstance()
    isSaveEnabled = false

    setLinkTextColor(ContextCompat.getColor(context, R.color.text_link))
}

fun TextView.stripUnderlines() {
    val s = SpannableString(text)
    val spans = s.getSpans(0, s.length, URLSpan::class.java)
    for (span in spans) {
        val start = s.getSpanStart(span)
        val end = s.getSpanEnd(span)
        s.removeSpan(span)

        val newSpan = URLSpanNoUnderline(span.url)
        s.setSpan(newSpan, start, end, 0)
    }
    text = s
}

private class URLSpanNoUnderline(url: String) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}


