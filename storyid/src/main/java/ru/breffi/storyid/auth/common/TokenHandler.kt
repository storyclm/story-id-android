package ru.breffi.storyid.auth.common

internal interface TokenHandler {

    fun refreshAccessToken()

    fun isAccessTokenExpired(): Boolean

    fun getAuthHeaderValue(): String?
}