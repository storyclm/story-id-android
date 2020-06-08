package ru.breffi.storyid.profile.util

import retrofit2.Call
import retrofit2.Response
import ru.breffi.storyid.profile.model.internal.ApiResult
import java.io.IOException
import java.util.*
import java.util.concurrent.locks.Lock

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

internal fun dataIsUpToDate(dbTimestamp: Long?, serverTimestamp: Long?): Boolean {
    return dbTimestamp != null && serverTimestamp != null && dbTimestamp == serverTimestamp
}

internal fun <T> withLocks(vararg locks: Lock, action: () -> T): T {
    locks.forEach { it.lock() }
    try {
        return action()
    } finally {
        locks.reversed().forEach { it.unlock() }
    }
}