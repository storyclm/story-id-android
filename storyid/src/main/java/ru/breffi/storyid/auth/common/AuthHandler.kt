package ru.breffi.storyid.auth.common

import ru.breffi.storyid.auth.common.model.IdValueResult

interface AuthHandler {

    fun userExists(username: String): IdValueResult<Boolean>

    fun logout()
}