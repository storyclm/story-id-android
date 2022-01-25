package ru.breffi.storyid.auth.common.model

import okhttp3.Headers

data class IdRequestDetails(
    val authData: AuthData?,
    val requestUrl: String,
    val requestHeaders: Headers,
    val requestForm: Map<String, String>?,
    val responseCode: Int,
    val responseHeaders: Headers,
    val responseMessage: String?,
    val responseBody: String?
)
