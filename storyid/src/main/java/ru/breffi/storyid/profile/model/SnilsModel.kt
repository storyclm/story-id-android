package ru.breffi.storyid.profile.model

import java.io.File

data class SnilsModel(

    val profileId: String? = null,

    val verified: Boolean? = null,

    val file: File? = null,

    val snils: String? = null
)