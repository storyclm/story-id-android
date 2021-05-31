package ru.breffi.storyidsample.utils

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.breffi.storyidsample.api.base.ApiError
import retrofit2.HttpException
import ru.breffi.storyid.auth.common.model.IdException
import ru.breffi.storyidsample.R
import java.io.IOException
import java.net.SocketTimeoutException


fun Throwable.showDialog(context: Context, buttonNameRes: Int) {
    this.showDialog(context, null, buttonNameRes)
}

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun ApiError.showDialog(context: Context) {
    AlertDialog.Builder(context, R.style.DialogTheme)
        .setMessage(errorDescription)
        .setPositiveButton(R.string.button_understand) { dialog, _ -> dialog.dismiss() }
        .setOnDismissListener(null)
        .show()
}

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun Throwable.showDialog(
    context: Context,
    onDismissListener: DialogInterface.OnDismissListener?,
    buttonNameRes: Int
) {
    AlertDialog.Builder(context, R.style.DialogTheme)
        .setMessage(this.getMessage(context))
        .setPositiveButton(buttonNameRes) { dialog, _ -> dialog.dismiss() }
        .setCancelable(false)
        .setOnDismissListener(onDismissListener)
        .show()
}

fun Throwable.getMessage(context: Context): String {
    val message: String

    val apiErrorCode = this.getApiErrorCode()
    if (apiErrorCode == 502 || apiErrorCode == 504) {
        message = context.getString(R.string.error_service_unavaliable)
    } else if (this is SocketTimeoutException) {
        //message = context.getString(R.string.error_timeout)
        message = context.getString(R.string.error_network)
    } else if (this is IOException) {
        message = context.getString(R.string.error_network)
    } else if (this is HttpException || this is IdException) {
        val apiError = this.getApiError()

        message = if (apiError?.errorDescription != null) {
            when (apiError.errorDescription) {
                "Code is expired" -> context.getString(R.string.error_code_is_expired)
                "Wrong signature" -> context.getString(R.string.label_wrong_code)
                else -> apiError.errorDescription
            }
        } else {
            context.getString(R.string.error_not_available)
        }
    } else {
        message = context.getString(R.string.error_unknown)
    }

    return message
}

fun Throwable.getApiError(): ApiError? {
    val bodyString = (this as? HttpException)?.response()?.errorBody()?.string()
        ?: (this as? IdException)?.bodyString

    return bodyString?.let {
        try {
            Gson().fromJson(it, ApiError::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }
}

fun Throwable.getApiErrorCode(): Int {
    return (this as? HttpException)?.code()
        ?: (this as? IdException)?.code
        ?: 0
}

fun Throwable.showErrorDialog(
    context: FragmentActivity
) {
    showDialog(context, R.string.button_ok)
}

