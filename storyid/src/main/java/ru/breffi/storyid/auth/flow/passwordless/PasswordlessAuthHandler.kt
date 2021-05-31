package ru.breffi.storyid.auth.flow.passwordless

import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.auth.common.AuthHandler
import ru.breffi.storyid.auth.common.model.IdResult

interface PasswordlessAuthHandler : AuthHandler, AuthDataProvider {

    fun passwordlessAuth(username: String): IdResult

    fun passwordlessProceedWithCode(code: String): IdResult
}