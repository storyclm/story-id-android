package ru.breffi.storyid.profile.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "demographics_data")
data class DemographicsDbModel(

    @PrimaryKey
    val internalId: String,

    val id: String? = null,

    val profileId: String? = null,

    val name: String? = null,

    val surname: String? = null,

    val patronymic: String? = null,

    val gender: Boolean? = null,

    val birthday: Long? = null,

    val verifiedAt: Long? = null,

    val verifiedBy: String? = null,

    val verified: Boolean = false,

    val modifiedAt: Long,

    val modifiedBy: String,

    val userId: String

)