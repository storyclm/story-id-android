package ru.breffi.storyid.profile.model

data class ProfileModel(

    val profileId: ProfileIdModel,

    val demographics: DemographicsModel,

    val itn: ItnModel,

    val snils: SnilsModel,

    val passport: PassportModel
)