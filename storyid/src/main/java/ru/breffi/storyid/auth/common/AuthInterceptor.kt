package ru.breffi.storyid.auth.common

import okhttp3.*
import ru.breffi.storyid.auth.common.model.IdException
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class AuthInterceptor(private val tokenHandler: TokenHandler) : Interceptor, Authenticator {

    companion object {
        private const val AUTH_HEADER_NAME = "Authorization"
    }

    private val lock = ReentrantLock()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        getAuthHeader()?.let { authHeader ->
            val request = chain.request()
                .newBuilder()
                .header(AUTH_HEADER_NAME, authHeader)
                .build()
            return chain.proceed(request)
        } ?: throw IdException(code = 401, message = "Missing authentication data")
    }

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        return if (response.code() == 401) {
            getAuthHeader()
                ?.let { authHeader ->
                    response.request()
                        .newBuilder()
                        .header(AUTH_HEADER_NAME, authHeader)
                        .build()
                }
        } else {
            null
        }
    }

    private fun getAuthHeader(): String? {
        return lock.withLock {
            if (tokenHandler.isAccessTokenExpired()) {
                tokenHandler.refreshAccessToken()
            }
            tokenHandler.getAuthHeaderValue()
        }
    }
}
