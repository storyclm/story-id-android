package ru.breffi.storyid.auth.common.model

import android.os.Parcelable
import com.auth0.android.jwt.JWT
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthData(

    val userId: String?,

    val idToken: String?,

    val accessToken: String,

    val expiresIn: Long,

    val tokenType: String,

    val refreshToken: String?
) : Parcelable {

    fun getAuthHeaderValue() : String {
        return "$tokenType $accessToken"
    }

    fun isAccessTokenExpired(): Boolean {
        return JWT(accessToken).isExpired(0)
    }
}