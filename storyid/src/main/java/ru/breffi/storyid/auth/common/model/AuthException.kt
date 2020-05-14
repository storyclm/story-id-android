package ru.breffi.storyid.auth.common.model

class AuthException(val code: Int, msg: String? = null, val bodyString: String? = null) : RuntimeException(msg)