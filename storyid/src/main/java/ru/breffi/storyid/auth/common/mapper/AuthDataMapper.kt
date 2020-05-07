package ru.breffi.storyid.auth.common.mapper

import android.text.TextUtils
import com.auth0.android.jwt.JWT
import ru.breffi.storyid.auth.common.model.AuthData
import ru.breffi.storyid.auth.common.model.AuthResponse

internal object AuthDataMapper {

    fun map(authResponse: AuthResponse): AuthData {
        val sub = JWT(authResponse.accessToken).subject
        val userId = if (TextUtils.isEmpty(sub)) null else sub
        return AuthData(
            userId = userId,
            idToken = authResponse.idToken,
            accessToken = authResponse.accessToken,
            expiresIn = authResponse.expiresIn,
            tokenType = authResponse.tokenType,
            refreshToken = authResponse.refreshToken
        )
    }
}