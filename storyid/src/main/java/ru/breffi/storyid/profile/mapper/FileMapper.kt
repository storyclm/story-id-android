package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.generated_api.model.FileViewModel
import ru.breffi.storyid.profile.util.FileHelper
import ru.breffi.storyid.profile.db.dto.FileDbModel
import ru.breffi.storyid.profile.model.CreateFileModel
import ru.breffi.storyid.profile.model.internal.Metadata
import ru.breffi.storyid.profile.util.newId

internal class FileMapper(private val fileHelper: FileHelper, private val metadata: Metadata) {

    fun mapNewOrUpdatedDbModel(createModel: CreateFileModel, dbModel: FileDbModel?): FileDbModel {
        //todo update metadata
        val path = createModel.path
        val fileName = FileHelper.filename(path.category, path.name)
        fileHelper.copy(createModel.file, fileName)
        return dbModel?.copy(
            modifiedAt = metadata.timestamp
        ) ?: FileDbModel(
            internalId = newId(),
            category = path.category,
            name = path.name,
            createdAt = metadata.timestamp,
            modifiedAt = metadata.timestamp,
            fileName = fileName,
            userId = metadata.userId
        )
    }

    fun getDeletedDbModel(dbModel: FileDbModel): FileDbModel {
        return dbModel.copy(
            deleted = true,
            modifiedAt = metadata.timestamp
        )
    }

    fun getUpdatedDbModel(dbModel: FileDbModel?, dto: FileViewModel): FileDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val createdAt = dto.createdAt?.millis ?: metadata.timestamp
        return dbModel?.copy(
            id = dto.id,
            modifiedAt = modifiedAt,
            profileId = dto.profileId,
            name = dto.name,
            description = dto.description,
            createdAt = createdAt,
            uploaded = dto.uploaded,
            category = dto.category ?: "",
            fileName = dto.fileName,
            size = dto.size,
            mimeType = dto.mimeType
        ) ?: FileDbModel(
            internalId = newId(),
            userId = metadata.userId,
            modifiedAt = modifiedAt,
            id = dto.id,
            profileId = dto.profileId,
            name = dto.name,
            description = dto.description,
            createdAt = createdAt,
            uploaded = dto.uploaded,
            category = dto.category ?: "",
            fileName = dto.fileName,
            size = dto.size,
            mimeType = dto.mimeType
        )
    }
}