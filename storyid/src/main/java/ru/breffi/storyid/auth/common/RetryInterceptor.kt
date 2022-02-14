package ru.breffi.storyid.auth.common

import okhttp3.Interceptor
import okhttp3.Response
import ru.breffi.storyid.auth.common.model.RetryPolicy
import java.io.IOException
import java.net.SocketTimeoutException

class RetryInterceptor(private val retryPolicy: RetryPolicy = RetryPolicy()) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var retry = 0
        do {
            if (retry > 0) {
                Thread.sleep(retryPolicy.delayMillis)
            }
            try {
                response?.close()
                response = chain.proceed(request)
            } catch (e: SocketTimeoutException) {
                response = null
                e.printStackTrace()
            }
        } while ((response == null || !response.isSuccessful && response.code() in retryPolicy.codes) && retry++ < retryPolicy.count)
        return response ?: throw SocketTimeoutException()
    }
}
