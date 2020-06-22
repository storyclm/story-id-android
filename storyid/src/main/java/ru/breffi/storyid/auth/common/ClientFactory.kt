package ru.breffi.storyid.auth.common

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

internal object ClientFactory {

    private const val LOGGER_TAG = "HTTPLogger"
    private const val TIMEOUT_SECONDS = 5L

    fun createDefaultClient(tokenHandler: TokenHandler): OkHttpClient {
        val logger = HttpLoggingInterceptor.Logger { message ->
            if (message.length < 5000) {
                Log.d(LOGGER_TAG, message)
            } else {
                Log.d(LOGGER_TAG, "Message is too long...")
            }
        }
        val httpLoggingInterceptor = HttpLoggingInterceptor(logger)
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = AuthInterceptor(tokenHandler)
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .authenticator(authInterceptor)
            .addInterceptor(RetryInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }
}