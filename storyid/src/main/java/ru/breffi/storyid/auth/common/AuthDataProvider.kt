package ru.breffi.storyid.auth.common

import ru.breffi.storyid.auth.common.model.AuthData

interface AuthDataProvider {

    fun getAuthData(): AuthData?

    fun isAuthenticated(): Boolean
}