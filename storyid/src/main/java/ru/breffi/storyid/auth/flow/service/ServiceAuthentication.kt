package ru.breffi.storyid.auth.flow.service

import okhttp3.FormBody
import ru.breffi.storyid.auth.common.Authentication
import ru.breffi.storyid.auth.common.model.AuthConfig
import ru.breffi.storyid.auth.common.model.IdResult
import ru.breffi.storyid.auth.common.repository.AuthRepository

internal class ServiceAuthentication(authConfig: AuthConfig, authRepository: AuthRepository) : Authentication(authConfig, authRepository),
    ServiceAuthHandler {

    override fun serviceAuth(): IdResult {
        val authForm = FormBody.Builder()
            .add(KEY_GRANT_TYPE, "client_credentials")
            .add(KEY_CLIENT_ID, authConfig.clientId)
            .add(KEY_CLIENT_SECRET, authConfig.clientSecret)
            .build()
        return performAuthRequest(authForm)
    }

    override fun refreshAccessToken() {
        serviceAuth()
    }
}