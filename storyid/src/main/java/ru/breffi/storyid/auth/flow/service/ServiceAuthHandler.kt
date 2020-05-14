package ru.breffi.storyid.auth.flow.service

import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.auth.common.AuthHandler
import ru.breffi.storyid.auth.common.model.AuthState

interface ServiceAuthHandler : AuthHandler, AuthDataProvider {

    fun serviceAuth(): AuthState
}