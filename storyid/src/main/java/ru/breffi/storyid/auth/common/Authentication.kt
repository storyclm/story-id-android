package ru.breffi.storyid.auth.common

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import ru.breffi.storyid.auth.common.mapper.AuthDataMapper
import ru.breffi.storyid.auth.common.model.*
import ru.breffi.storyid.auth.common.repository.AuthRepository
import java.io.IOException

internal open class Authentication(protected val authConfig: AuthConfig, protected val authRepository: AuthRepository)
    : AuthHandler, AuthDataProvider, TokenHandler {

    companion object {
        const val KEY_GRANT_TYPE = "grant_type"
        const val KEY_CLIENT_ID = "client_id"
        const val KEY_CLIENT_SECRET = "client_secret"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        const val KEY_EXPIRATION = "expiration"
        const val KEY_SIGNATURE = "signature"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    protected val gson = Gson()
    protected val internalClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    override fun getAuthData(): AuthData? {
        return authRepository.getAuthData()
    }

    override fun isAuthenticated(): Boolean {
        return authRepository.getAuthData() != null
    }

    override fun logout() {
        authRepository.clearAuthData()
    }

    protected fun performAuthRequest(authForm: FormBody): AuthState {
        try {
            val configResult = getConfiguration()
            configResult.openIDConfiguration?.let { openIDConfiguration ->
                val request = Request.Builder()
                    .url(openIDConfiguration.tokenEndpoint)
                    .post(authForm)
                    .build()
                val response = internalClient.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        gson.fromJson(body.string(), AuthResponse::class.java)?.let { authResponse ->
                            val authData = AuthDataMapper.map(authResponse)
                            authRepository.saveAuthData(authData)
                            return AuthSuccess
                        }
                    }
                    return AuthError(code = response.code(), message = "null body")
                } else {
                    authRepository.clearAuthData()
                    return AuthError(code = response.code(), message = response.message(), bodyString = response.body()?.string())
                }
            } ?: configResult.authState
        } catch (e: IOException) {
            e.printStackTrace()
            return AuthError(e)
        }
        return AuthError()
    }

    override fun refreshAccessToken() {
        authRepository.getAuthData()?.refreshToken?.let { refreshToken ->
            val configResult = getConfiguration()
            configResult.openIDConfiguration?.let { openIDConfiguration ->
                val formBody = FormBody.Builder()
                    .addEncoded(KEY_GRANT_TYPE, "refresh_token")
                    .addEncoded(KEY_REFRESH_TOKEN, refreshToken)
                    .addEncoded(KEY_CLIENT_ID, authConfig.clientId)
                    .addEncoded(KEY_CLIENT_SECRET, authConfig.clientSecret)
                    .build()
                val request = Request.Builder()
                    .url(openIDConfiguration.tokenEndpoint)
                    .post(formBody)
                    .build()
                val response = internalClient.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        gson.fromJson(body.string(), AuthResponse::class.java)?.let { authResponse ->
                            val authData = AuthDataMapper.map(authResponse)
                            authRepository.saveAuthData(authData)
                        }
                    }
                } else {
                    authRepository.clearAuthData()
                }
            }
        }
    }

    override fun isAccessTokenExpired(): Boolean {
        return authRepository.getAuthData()?.isAccessTokenExpired() ?: true
    }

    override fun getAuthHeaderValue(): String? {
        return authRepository.getAuthData()?.getAuthHeaderValue()
    }

    @Throws(IOException::class)
    protected fun getConfiguration(): OpenIDConfigResult {
        val request = Request.Builder()
            .url(authConfig.openIdConfigUrl)
            .get()
            .build()
        val response = internalClient.newCall(request).execute()
        return if (response.isSuccessful) {
            response.body()?.let { body ->
                val config = gson.fromJson(body.string(), OpenIDConfiguration::class.java)
                OpenIDConfigResult(AuthSuccess, config)
            } ?: OpenIDConfigResult(AuthError(code = response.code(), message = "null config body"))
        } else {
            OpenIDConfigResult(AuthError(code = response.code(), message = response.message(), bodyString = response.body()?.string()))
        }
    }
}