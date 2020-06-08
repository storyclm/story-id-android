package ru.breffi.storyid.profile.model

data class PassportDataModel(

    val verified: Boolean? = null,

    var sn: String? = null,

    val issuedBy: String? = null,

    val issuedAt: Long? = null,

    var code: String? = null
)