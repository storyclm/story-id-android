package ru.breffi.storyid.profile.model

import java.io.File

data class ItnModel(

    val profileId: String? = null,

    val verified: Boolean? = null,

    val file: File? = null,

    val itn: String? = null
)