package ru.breffi.storyid.auth.common.model

data class AuthConfig(val openIdConfigUrl: String, val clientId: String, val clientSecret: String, val authStorageName: String = "default_auth_data_storage")