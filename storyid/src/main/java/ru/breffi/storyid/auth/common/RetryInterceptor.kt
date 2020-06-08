package ru.breffi.storyid.auth.common

import okhttp3.*
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class RetryInterceptor(private val retryCount: Int = 3) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var retry = 0
        while (!response.isSuccessful && response.code() in 500..599 && retry < retryCount) {
            response = chain.proceed(request)
            retry++
        }
        return response
    }
}
