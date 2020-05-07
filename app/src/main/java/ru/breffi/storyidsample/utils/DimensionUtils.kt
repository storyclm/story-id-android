package ru.breffi.storyidsample.utils

import android.content.Context
import android.util.DisplayMetrics

fun Float.dpToPx(context: Context): Int {
    return (this * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}