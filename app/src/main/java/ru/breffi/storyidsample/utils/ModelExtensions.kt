package ru.breffi.storyidsample.utils

import ru.breffi.storyid.profile.model.DemographicsModel
import ru.breffi.storyid.profile.model.ProfileModel

fun ProfileModel.isFull(): Boolean {
    return !email.isNullOrEmpty() && !phone.isNullOrEmpty()
}

fun DemographicsModel.fioIsEmpty(): Boolean {
    return name.isNullOrEmpty() && surname.isNullOrEmpty()
}

fun DemographicsModel.isFull(): Boolean {
    return !name.isNullOrEmpty() && !surname.isNullOrEmpty() && !patronymic.isNullOrEmpty()
}