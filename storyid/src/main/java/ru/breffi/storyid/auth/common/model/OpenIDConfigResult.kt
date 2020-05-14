package ru.breffi.storyid.auth.common.model

internal data class OpenIDConfigResult(val authState: AuthState, val openIDConfiguration: OpenIDConfiguration? = null)