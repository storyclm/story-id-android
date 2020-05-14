package ru.breffi.storyid.profile.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itn_data")
data class ItnDbModel(

    @PrimaryKey
    val internalId: String,

    val profileId: String? = null,

    val modifiedAt: Long,

    val modifiedBy: String,

    val verified: Boolean? = null,

    val verifiedAt: Long? = null,

    val verifiedBy: String? = null,

    val itn: String? = null,

    val fileName: String? = null,

    val userId: String
)