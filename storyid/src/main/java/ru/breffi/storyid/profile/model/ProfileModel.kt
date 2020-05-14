package ru.breffi.storyid.profile.model

data class ProfileModel(

    val userId: String,

    val email: String? = null,

    val emailVerified: Boolean = false,

    val phone: String? = null,

    val phoneVerified: Boolean = false,

    val username: String? = null,

    val demographics: DemographicsModel,

    val itn: ItnModel,

    val snils: SnilsModel,

    val passport: PassportModel
)