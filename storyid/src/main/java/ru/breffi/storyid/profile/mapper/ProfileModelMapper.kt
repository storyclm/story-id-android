package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.profile.util.FileHelper
import ru.breffi.storyid.profile.db.dto.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyid.profile.model.internal.Metadata
import ru.breffi.storyid.profile.util.newId
import java.io.File

internal class ProfileModelMapper(private val metadata: Metadata, private val fileHelper: FileHelper) {

    fun getUpdatedProfileDbModel(model: ProfileModel, dbModel: ProfileDbModel?): ProfileDbModel {
        val updatedDbModel = dbModel ?: ProfileDbModel(
            internalId = newId(),
            createdAt = metadata.timestamp,
            createdBy = metadata.userId,
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            phone = model.phone,
            phoneVerified = model.phoneVerified,
            username = model.username,
            email = model.email,
            emailVerified = model.emailVerified
        )
    }

    fun getUpdatedDemographicsDbModel(model: DemographicsModel, dbModel: DemographicsDbModel?): DemographicsDbModel {
        val updatedDbModel = dbModel ?: DemographicsDbModel(
            internalId = newId(),
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            name = model.name,
            surname = model.surname,
            patronymic = model.patronymic,
            birthday = model.birthday,
            gender = model.gender,
            verified = model.verified
        )
    }

    fun getUpdatedItnDbModel(model: ItnModel, dbModel: ItnDbModel?): ItnDbModel {
        val updatedDbModel = dbModel ?: ItnDbModel(
            internalId = newId(),
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            userId = metadata.userId
        )
        updateFile(model.file, dbModel?.fileName, FileHelper.itnFilename())
        return updatedDbModel.copy(
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            verified = model.verified,
            itn = model.itn,
            fileName = model.file?.let { FileHelper.itnFilename() }
        )
    }

    fun getUpdatedSnilsDbModel(model: SnilsModel, dbModel: SnilsDbModel?): SnilsDbModel {
        val updatedDbModel = dbModel ?: SnilsDbModel(
            internalId = newId(),
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            userId = metadata.userId
        )
        updateFile(model.file, dbModel?.fileName, FileHelper.snilsFilename())
        return updatedDbModel.copy(
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            verified = model.verified,
            snils = model.snils,
            fileName = model.file?.let { FileHelper.snilsFilename() }
        )
    }

    fun getUpdatedPassportDbModel(model: PassportModel, dbModel: PassportDbModel?): PassportDbModel {
        val updatedDbModel = dbModel ?: PassportDbModel(
            internalId = newId(),
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            verified = model.verified,
            code = model.code,
            issuedBy = model.issuedBy,
            issuedAt = model.issuedAt,
            sn = model.sn
        )
    }

    fun getUpdatedPassportPageDbModel(model: PassportPageModel, dbModel: PassportPageDbModel?): PassportPageDbModel {
        val updatedDbModel = dbModel ?: PassportPageDbModel(
            internalId = newId(),
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            page = model.page,
            userId = metadata.userId
        )
        updateFile(model.file, dbModel?.fileName, FileHelper.passportPageFilename(model.page))
        return updatedDbModel.copy(
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            fileName = model.file?.let { FileHelper.passportPageFilename(model.page) }
        )
    }

    private fun updateFile(updateFile: File?, dbFileName: String?, storyFileName: String) {
        if (updateFile != null && updateFile.name != storyFileName) {
            dbFileName?.let { fileHelper.delete(it) }
            fileHelper.copy(updateFile, storyFileName)
        } else if (updateFile == null && dbFileName != null) {
            fileHelper.delete(dbFileName)
        }
    }
}