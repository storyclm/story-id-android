package ru.breffi.storyidsample.ui.common

import android.app.AlertDialog
import android.content.Context
import androidx.annotation.StringRes

class PositiveOrDismissDialog(context: Context, themeResId: Int? = null) {

    private val dialogBuilder: AlertDialog.Builder = if (themeResId == null ) AlertDialog.Builder(context) else AlertDialog.Builder(context, themeResId)

    private var positiveClicked = false

    fun setMessage(@StringRes textId: Int): PositiveOrDismissDialog {
        dialogBuilder.setMessage(textId)
        return this
    }

    fun setPositive(@StringRes textId: Int, action: () -> Unit): PositiveOrDismissDialog {
        dialogBuilder.setPositiveButton(textId) { dialog, which ->
            positiveClicked = true
            dialog.dismiss()
            action.invoke()
        }
        return this
    }

    fun setDismiss(@StringRes textId: Int, action: () -> Unit): PositiveOrDismissDialog {
        dialogBuilder.setNegativeButton(textId, null)
        dialogBuilder.setOnDismissListener {
            if (!positiveClicked) {
                action.invoke()
            }
        }
        return this
    }

    fun show() {
        positiveClicked = false
        dialogBuilder.show()
    }
}