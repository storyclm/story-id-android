package ru.breffi.storyid.auth.common.model

data class IdException(val code: Int, override val message: String? = null, val bodyString: String? = null) : Exception(message)