package ru.breffi.storyid.auth.common

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException

class RetryInterceptor(private val retryCount: Int = 3, private val retryDelayMillis: Long = 1000) : Interceptor {

    private val retryCodes = (500..599) + 402 + (405..499)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response?
        var retry = 0
        do {
            if (retry > 0) {
                Thread.sleep(retryDelayMillis)
            }
            try {
                response = chain.proceed(request)
            } catch (e: SocketTimeoutException) {
                response = null
                e.printStackTrace()
            }
        } while ((response == null || !response.isSuccessful && response.code() in retryCodes) && retry++ < retryCount)
        return response ?: throw SocketTimeoutException()
    }
}
