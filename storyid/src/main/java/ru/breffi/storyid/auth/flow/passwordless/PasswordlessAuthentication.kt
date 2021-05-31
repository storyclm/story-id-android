package ru.breffi.storyid.auth.flow.passwordless

import okhttp3.*
import org.json.JSONObject
import ru.breffi.storyid.auth.common.Authentication
import ru.breffi.storyid.auth.common.model.*
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

    override fun passwordlessAuth(username: String): IdResult {
        return try {
            val configResult = getConfiguration()

            if (configResult.value == null) return IdResult.ofFailure(configResult.exception)

            val httpUrl = HttpUrl.parse(configResult.value.issuer)
                    ?.newBuilder("/verify/code")
                    ?.build()
            if (httpUrl == null) return IdResult.ofFailure(IdException(code = 0, message = "invalid issuer"))

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
            return if (response.isSuccessful) {
                response.body()?.let { body ->
                    gson.fromJson(body.string(), PasswordlessResponse::class.java)?.let { response ->
                        formValues.clear()
                        formValues[KEY_GRANT_TYPE] = "passwordless"
                        formValues[KEY_LOGIN] = username
                        formValues[KEY_EXPIRATION] = response.expiration
                        formValues[KEY_SIGNATURE] = response.signature
                        formValues[KEY_CLIENT_ID] = authConfig.clientId
                        formValues[KEY_CLIENT_SECRET] = authConfig.clientSecret
                        IdResult.ofSuccess()
                    }
                } ?: IdResult.ofFailure(IdException(code = response.code(), message = "null body"))
            } else {
                IdResult.ofFailure(IdException(code = response.code(), message = response.message(), bodyString = response.body()?.string()))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            IdResult.ofFailure(e)
        }
    }

    override fun passwordlessProceedWithCode(code: String): IdResult {
        val authForm = FormBody.Builder()
            .apply { formValues.forEach { add(it.key, it.value) } }
            .add(KEY_CODE, code)
            .build()
        return performAuthRequest(authForm)
    }
}