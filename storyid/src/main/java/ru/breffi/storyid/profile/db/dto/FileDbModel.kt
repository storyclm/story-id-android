package ru.breffi.storyid.profile.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files_data")
data class FileDbModel(

    @PrimaryKey
    val internalId: String,

    val id: String? = null,

    val profileId: String? = null,

    val createdAt: Long,

    val modifiedAt: Long,

    val uploaded: Boolean? = null,

    val category: String,

    val name: String,

    val fileName: String?,

    val size: Long? = null,

    val description: String? = null,

    val mimeType: String? = null,

    val userId: String,

    val deleted: Boolean = false
)