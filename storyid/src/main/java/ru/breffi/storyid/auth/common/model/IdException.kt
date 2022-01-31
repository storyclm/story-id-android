package ru.breffi.storyid.auth.common.model

import java.io.IOException

data class IdException(val code: Int, override val message: String? = null, val bodyString: String? = null) : IOException(message)