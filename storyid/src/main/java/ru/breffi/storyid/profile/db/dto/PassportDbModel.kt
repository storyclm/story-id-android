package ru.breffi.storyid.profile.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passport_data")
data class PassportDbModel(

    @PrimaryKey
    val internalId: String,

    val id: String? = null,

    val profileId: String? = null,

    val modifiedAt: Long,

    val modifiedBy: String,

    val verified: Boolean? = null,

    val verifiedAt: Long? = null,

    val verifiedBy: String? = null,

    val sn: String? = null,

    val issuedBy: String? = null,

    val issuedAt: Long? = null,

    val code: String? = null,

    val userId: String
)