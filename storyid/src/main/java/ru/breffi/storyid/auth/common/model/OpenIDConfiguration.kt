package ru.breffi.storyid.auth.common.model

import com.google.gson.annotations.SerializedName

internal class OpenIDConfiguration(

    @SerializedName("issuer")
    val issuer: String,

    @SerializedName("token_endpoint")
    val tokenEndpoint: String
)