package ru.breffi.storyid.auth.flow.password

import okhttp3.FormBody
import ru.breffi.storyid.auth.common.Authentication
import ru.breffi.storyid.auth.common.model.AuthConfig
import ru.breffi.storyid.auth.common.model.AuthState
import ru.breffi.storyid.auth.common.repository.AuthRepository

internal class PasswordAuthentication(authConfig: AuthConfig, authRepository: AuthRepository) : Authentication(authConfig, authRepository),
    PasswordAuthHandler {

    override fun passwordAuth(username: String, password: String): AuthState {
        val authForm = FormBody.Builder()
            .add(KEY_GRANT_TYPE, "password")
            .add(KEY_USERNAME, username)
            .add(KEY_PASSWORD, password)
            .add(KEY_CLIENT_ID, authConfig.clientId)
            .add(KEY_CLIENT_SECRET, authConfig.clientSecret)
            .build()
        return performAuthRequest(authForm)
    }
}