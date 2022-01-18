package ru.breffi.storyid.auth.common

import ru.breffi.storyid.auth.common.model.IdException
import ru.breffi.storyid.auth.common.model.IdValueResult

interface AuthHandler {

    interface AuthLostListener {

        fun onAuthLost(cause: IdException)
    }

    fun userExists(username: String): IdValueResult<Boolean>

    fun setAuthLostListener(listener: AuthLostListener?)

    fun logout()
}