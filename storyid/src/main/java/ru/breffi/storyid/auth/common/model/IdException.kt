package ru.breffi.storyid.auth.common.model

class IdException(val code: Int, message: String? = null, val bodyString: String? = null) : RuntimeException(message)