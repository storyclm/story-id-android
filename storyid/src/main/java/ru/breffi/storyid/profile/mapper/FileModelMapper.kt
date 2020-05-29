package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.profile.FileHelper
import ru.breffi.storyid.profile.db.dto.FileDbModel
import ru.breffi.storyid.profile.model.FileModel

internal class FileModelMapper(private val fileHelper: FileHelper) {

    fun mapFileModel(dbModel: FileDbModel): FileModel {
        return FileModel(
            internalId = dbModel.internalId,
            category = dbModel.category,
            name = dbModel.name,
            file = dbModel.fileName?.let { fileHelper.getFile(it) }
        )
    }
}