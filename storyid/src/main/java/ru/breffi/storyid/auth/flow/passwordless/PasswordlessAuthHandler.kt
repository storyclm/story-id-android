package ru.breffi.storyid.auth.flow.passwordless

import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.auth.common.AuthHandler
import ru.breffi.storyid.auth.common.model.AuthState

interface PasswordlessAuthHandler : AuthHandler, AuthDataProvider {

    fun passwordlessAuth(username: String): AuthState

    fun passwordlessProceedWithCode(code: String): AuthState
}