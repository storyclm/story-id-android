package ru.breffi.storyid.profile

import retrofit2.Call
import java.util.*

fun <T> Call<T>.get(): T {
    return execute().body()!!
}

internal fun newId(): String {
    return UUID.randomUUID().toString()
}