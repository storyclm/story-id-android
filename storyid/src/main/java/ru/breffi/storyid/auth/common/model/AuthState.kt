package ru.breffi.storyid.auth.common.model

sealed class AuthState

object AuthSuccess : AuthState()

data class AuthError(val error: Exception? = null) : AuthState() {

    constructor(code: Int = 0, message: String? = null, bodyString: String? = null) : this(AuthException(code, message, bodyString))
}