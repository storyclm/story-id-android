package ru.breffi.storyid.profile.model

import java.io.File

data class FileModel(
    val internalId: String,
    val category: String,
    val name: String,
    val file: File?
)