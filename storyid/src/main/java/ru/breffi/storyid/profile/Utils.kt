package ru.breffi.storyid.profile

import retrofit2.Call
import ru.breffi.storyid.profile.model.internal.ApiResult
import java.util.*

internal fun <T> Call<T>.get(): ApiResult<T>? {
    val response = execute()
    return when {
        response.isSuccessful -> {
            ApiResult(response.body())
        }
        response.code() == 404 -> {
            ApiResult(null)
        }
        else -> {
            null
        }
    }
}

internal fun newId(): String {
    return UUID.randomUUID().toString()
}