package ru.breffi.storyid.auth.flow.password

import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.auth.common.AuthHandler
import ru.breffi.storyid.auth.common.model.IdResult

interface PasswordAuthHandler : AuthHandler, AuthDataProvider {

    fun passwordAuth(username: String, password: String): IdResult
}