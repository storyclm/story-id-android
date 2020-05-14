package ru.breffi.storyid.profile.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passport_page_data")
data class PassportPageDbModel(

    @PrimaryKey
    val internalId: String,

    val modifiedAt: Long,

    val modifiedBy: String,

    val fileName: String? = null,

    val page: Int,

    val userId: String
)