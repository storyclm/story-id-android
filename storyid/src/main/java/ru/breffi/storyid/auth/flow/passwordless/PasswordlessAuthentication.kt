package ru.breffi.storyid.auth.flow.passwordless

import okhttp3.*
import org.json.JSONObject
import ru.breffi.storyid.auth.common.Authentication
import ru.breffi.storyid.auth.common.model.AuthConfig
import ru.breffi.storyid.auth.common.model.AuthError
import ru.breffi.storyid.auth.common.model.AuthState
import ru.breffi.storyid.auth.common.model.AuthSuccess
import ru.breffi.storyid.auth.common.repository.AuthRepository
import ru.breffi.storyid.auth.flow.passwordless.model.PasswordlessResponse
import java.io.IOException

internal class PasswordlessAuthentication(authConfig: AuthConfig, authRepository: AuthRepository) : Authentication(authConfig, authRepository),
    PasswordlessAuthHandler {

    companion object {
        const val KEY_LOGIN = "login"
        const val KEY_NOPASS_CLIENT = "client"
        const val KEY_NOPASS_SECRET = "secret"
        const val KEY_CODE = "code"
    }

    private val formValues = mutableMapOf<String, String>()

    override fun passwordlessAuth(username: String): AuthState {
        try {
            val configResult = getConfiguration()

            val openIDConfiguration = configResult.openIDConfiguration
            if (openIDConfiguration == null) return configResult.authState

            val httpUrl = HttpUrl.parse(openIDConfiguration.issuer)
                    ?.newBuilder("/verify/code")
                    ?.build()
            if (httpUrl == null) return AuthError(code = 0, message = "invalid issuer")

            val jsonObject = JSONObject()
            jsonObject.put(KEY_LOGIN, username)
            jsonObject.put(KEY_NOPASS_CLIENT, authConfig.clientId)
            jsonObject.put(KEY_NOPASS_SECRET, authConfig.clientSecret)
            val passwordlessBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())
            val request = Request.Builder()
                .url(httpUrl)
                .post(passwordlessBody)
                .build()
            val response = internalClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    gson.fromJson(body.string(), PasswordlessResponse::class.java)?.let { response ->
                        formValues.clear()
                        formValues[KEY_GRANT_TYPE] = "passwordless"
                        formValues[KEY_LOGIN] = username
                        formValues[KEY_EXPIRATION] = response.expiration
                        formValues[KEY_SIGNATURE] = response.signature
                        formValues[KEY_CLIENT_ID] = authConfig.clientId
                        formValues[KEY_CLIENT_SECRET] = authConfig.clientSecret
                        return AuthSuccess
                    }
                    return AuthError(code = response.code(), message = "null body")
                }
            } else {
                return AuthError(code = response.code(), message = response.message(), bodyString = response.body()?.string())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return AuthError(e)
        }
        return AuthError()
    }

    override fun passwordlessProceedWithCode(code: String): AuthState {
        val authForm = FormBody.Builder()
            .apply { formValues.forEach { add(it.key, it.value) } }
            .add(KEY_CODE, code)
            .build()
        return performAuthRequest(authForm)
    }
}