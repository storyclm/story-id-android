package ru.breffi.storyid.auth.flow.passwordless.model

internal class PasswordlessResponse(

    val expiration: String,

    val signature: String
)