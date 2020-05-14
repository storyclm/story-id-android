package ru.breffi.storyidsample.api.base

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException


class ApiError {

    @SerializedName("error")
    var error: String? = null
    @SerializedName("error_description")
    val errorDescription: String? = null

    companion object {

        fun fromThrowable(throwable: Throwable): ApiError? {
            var apiError: ApiError? = null
            if (throwable is HttpException) {
                val code = throwable.code()

                val response = throwable.response()
                if (code == 500) {
                    apiError = Gson().fromJson(response.errorBody()!!.charStream(), ApiError::class.java)
                }
            }

            return apiError
        }
    }

}
