package ru.breffi.storyid.profile.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_data")
data class ProfileDbModel(

    @PrimaryKey
    val internalId: String,

    val id: String? = null,

    val email: String? = null,

    val emailVerified: Boolean = false,

    val phone: String? = null,

    val phoneVerified: Boolean = false,

    val username: String? = null,

    val createdAt: Long,

    val createdBy: String,

    val modifiedAt: Long,

    val modifiedBy: String,

    val userId: String
)