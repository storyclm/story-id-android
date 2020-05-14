package ru.breffi.storyid.profile.model

data class DemographicsModel(

    val profileId: String? = null,

    val name: String? = null,

    val surname: String? = null,

    val patronymic: String? = null,

    val gender: Boolean? = null,

    val birthday: Long? = null,

    val verified: Boolean = false
)