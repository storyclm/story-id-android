package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.generated_api.model.*
import ru.breffi.storyid.profile.db.dto.*
import ru.breffi.storyid.profile.model.internal.Metadata
import ru.breffi.storyid.profile.newId

internal class ProfileDtoMapper(private val metadata: Metadata) {

    fun getUpdatedProfileDbModel(dto: StoryProfile, dbModel: ProfileDbModel?): ProfileDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val modifiedBy = dto.modifiedBy ?: metadata.userId
        val updatedDbModel = dbModel ?: ProfileDbModel(
            internalId = newId(),
            createdAt = dto.createdAt?.millis ?: metadata.timestamp,
            createdBy = dto.createdBy ?: metadata.userId,
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            id = dto.id,
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            phone = dto.phone,
            phoneVerified = dto.phoneVerified ?: false,
            username = dto.username,
            email = dto.email,
            emailVerified = dto.emailVerified ?: false
        )
    }

    fun getUpdatedDemographicsDbModel(dto: StoryDemographics, dbModel: DemographicsDbModel?): DemographicsDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val modifiedBy = dto.modifiedBy ?: metadata.userId
        val updatedDbModel = dbModel ?: DemographicsDbModel(
            internalId = newId(),
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            name = dto.name,
            surname = dto.surname,
            patronymic = dto.patronymic,
            birthday = dto.birthday?.millis,
            gender = dto.gender,
            verified = dto.verified ?: false
        )
    }

    fun getUpdatedItnDbModel(dto: StoryITN, dbModel: ItnDbModel?, filename: String?): ItnDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val modifiedBy = dto.modifiedBy ?: metadata.userId
        val updatedDbModel = dbModel ?: ItnDbModel(
            internalId = newId(),
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            verified = dto.verified,
            itn = dto.itn,
            fileName = filename
        )
    }

    fun getUpdatedSnilsDbModel(dto: StorySNILS, dbModel: SnilsDbModel?, filename: String?): SnilsDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val modifiedBy = dto.modifiedBy ?: metadata.userId
        val updatedDbModel = dbModel ?: SnilsDbModel(
            internalId = newId(),
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            verified = dto.verified,
            snils = dto.snils,
            fileName = filename
        )
    }

    fun getUpdatedPassportDbModel(dto: StoryPasport, dbModel: PassportDbModel?): PassportDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val modifiedBy = dto.modifiedBy ?: metadata.userId
        val updatedDbModel = dbModel ?: PassportDbModel(
            internalId = newId(),
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            verified = dto.verified,
            code = dto.code,
            issuedBy = dto.issuedBy,
            issuedAt = dto.issuedAt?.millis,
            sn = dto.sn
        )
    }

    fun getUpdatedPassportPageDbModel(dto: StoryPasportPageViewModel, dbModel: PassportPageDbModel?, filename: String?): PassportPageDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val modifiedBy = dto.modifiedBy ?: metadata.userId
        val updatedDbModel = dbModel ?: PassportPageDbModel(
            internalId = newId(),
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            page = dto.page,
            userId = metadata.userId
        )
        return updatedDbModel.copy(
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            fileName = filename
        )
    }
}