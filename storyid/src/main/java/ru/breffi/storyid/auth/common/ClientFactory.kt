package ru.breffi.storyid.auth.common

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import javax.security.cert.CertificateException

internal object ClientFactory {

    private const val LOGGER_TAG = "HTTPLogger"
    private const val TIMEOUT_SECONDS = 5L

    fun createDefaultClient(tokenHandler: TokenHandler, trustAll: Boolean): OkHttpClient {
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
            .trustAll(trustAll)
            .build()
    }

    //For test purposes only
    fun OkHttpClient.Builder.trustAll(enabled: Boolean): OkHttpClient.Builder {
        if (enabled) {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            hostnameVerifier { hostname, session -> true }
        }
        return this
    }
}