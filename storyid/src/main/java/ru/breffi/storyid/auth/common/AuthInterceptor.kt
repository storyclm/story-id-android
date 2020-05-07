package ru.breffi.storyid.auth.common

import okhttp3.*
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
        tokenHandler.getAuthHeaderValue()
            ?.let { authHeader ->
                val request = chain.request()
                    .newBuilder()
                    .header(AUTH_HEADER_NAME, authHeader)
                    .build()
                return chain.proceed(request)
            } ?: throw IOException()
    }

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        return if (response.code() == 401 && tokenHandler.isAccessTokenExpired()) {
            getRefreshedAuthHeader()
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

    private fun getRefreshedAuthHeader() : String? {
        if (lock.isLocked) {
            lock.withLock {
                return tokenHandler.getAuthHeaderValue()
            }
        } else {
            lock.withLock {
                tokenHandler.refreshAccessToken()
                return tokenHandler.getAuthHeaderValue()
            }
        }
    }
}
