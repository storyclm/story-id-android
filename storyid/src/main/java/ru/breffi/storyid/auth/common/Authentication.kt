package ru.breffi.storyid.auth.common

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import ru.breffi.storyid.auth.common.mapper.AuthDataMapper
import ru.breffi.storyid.auth.common.model.*
import ru.breffi.storyid.auth.common.repository.AuthRepository
import ru.breffi.storyid.auth.flow.passwordless.PasswordlessAuthentication
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
        .addInterceptor(RetryInterceptor())
        .build()

    private val mainHandler = Handler(Looper.getMainLooper())
    private var authLostListener: AuthHandler.AuthLostListener? = null

    override fun getAuthData(): AuthData? {
        return authRepository.getAuthData()
    }

    override fun isAuthenticated(): Boolean {
        return authRepository.getAuthData() != null
    }

    override fun userExists(username: String): IdValueResult<Boolean> {
        return try {
            val configResult = getConfiguration()

            if (configResult.value == null) return IdValueResult.ofFailure(configResult.exception)

            val httpUrl = HttpUrl.parse(configResult.value.issuer)
                ?.newBuilder("/verify/exists")
                ?.build()
            if (httpUrl == null) return IdValueResult.ofFailure(IdException(code = 0, message = "invalid issuer"))

            val jsonObject = JSONObject()
            jsonObject.put(PasswordlessAuthentication.KEY_LOGIN, username)
            jsonObject.put(PasswordlessAuthentication.KEY_NOPASS_CLIENT, authConfig.clientId)
            jsonObject.put(PasswordlessAuthentication.KEY_NOPASS_SECRET, authConfig.clientSecret)
            val existsBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())
            val request = Request.Builder()
                .url(httpUrl)
                .put(existsBody)
                .build()
            val response = internalClient.newCall(request).execute()
            when {
                response.isSuccessful -> IdValueResult.ofSuccess(true)
                response.code() == 404 -> IdValueResult.ofSuccess(false)
                else -> IdValueResult.ofFailure(IdException(code = response.code(), message = response.message(), bodyString = response.body()?.string()))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            IdValueResult.ofFailure(e)
        }
    }

    override fun setAuthLostListener(listener: AuthHandler.AuthLostListener?) {
        authLostListener = listener
    }

    override fun logout() {
        authRepository.clearAuthData()
    }

    protected fun performAuthRequest(authForm: FormBody): IdResult {
        try {
            val configResult = getConfiguration()
            return configResult.onSuccess { openIDConfiguration ->
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
                            IdValueResult.ofSuccess()
                        }
                    } ?: IdResult.ofFailure(IdException(code = response.code(), message = "null or empty body"))
                } else {
                    authRepository.clearAuthData()
                    IdResult.ofFailure(IdException(code = response.code(), message = response.message(), bodyString = response.body()?.string()))
                }
            } ?: IdResult.ofFailure(configResult.exception)
        } catch (e: IOException) {
            e.printStackTrace()
            return IdResult.ofFailure(e)
        }
    }

    override fun refreshAccessToken() {
        authRepository.getAuthData()?.refreshToken?.let { refreshToken ->
            getConfiguration().onSuccess { openIDConfiguration ->
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
                    val authData = authRepository.getAuthData()
                    authRepository.clearAuthData()
                    mainHandler.post {
                        val originalRequest = response.request()
                        val requestForm = (originalRequest.body() as? FormBody)?.let { body ->
                            (0 until body.size())
                                .map { index ->
                                    body.encodedName(index) to body.encodedValue(index)
                                }
                                .toMap()
                        }
                        val idRequestDetails = IdRequestDetails(
                            authData = authData,
                            requestUrl = originalRequest.url().toString(),
                            requestHeaders = originalRequest.headers(),
                            requestForm = requestForm,
                            responseCode = response.code(),
                            responseHeaders = response.headers(),
                            responseMessage = response.message(),
                            responseBody = response.body()?.string()
                        )
                        authLostListener?.onAuthLost(idRequestDetails)
                    }
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
    protected fun getConfiguration(): IdValueResult<OpenIDConfiguration> {
        val request = Request.Builder()
            .url(authConfig.openIdConfigUrl)
            .get()
            .build()
        val response = internalClient.newCall(request).execute()
        return if (response.isSuccessful) {
            response.body()?.let { body ->
                val config = gson.fromJson(body.string(), OpenIDConfiguration::class.java)
                IdValueResult.ofSuccess(config)
            } ?: IdValueResult.ofFailure(IdException(code = response.code(), message = "null config body"))
        } else {
            IdValueResult.ofFailure(IdException(code = response.code(), message = response.message(), bodyString = response.body()?.string()))
        }
    }
}