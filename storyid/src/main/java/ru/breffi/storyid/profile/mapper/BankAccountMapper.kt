package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.generated_api.model.StoryBankAccount
import ru.breffi.storyid.generated_api.model.StoryBankAccountDTO
import ru.breffi.storyid.profile.db.dto.BankAccountDbModel
import ru.breffi.storyid.profile.model.BankAccountModel
import ru.breffi.storyid.profile.model.CreateBankAccountModel
import ru.breffi.storyid.profile.model.internal.Metadata
import ru.breffi.storyid.profile.util.newId

internal class BankAccountMapper(private val metadata: Metadata) {

    fun mapNewDbModel(createModel: CreateBankAccountModel): BankAccountDbModel {
        return BankAccountDbModel(
            internalId = newId(),
            name = createModel.name,
            description = createModel.description,
            bic = createModel.bic,
            bank = createModel.bank,
            correspondentAccount = createModel.correspondentAccount,
            settlementAccount = createModel.settlementAccount,
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId,
            userId = metadata.userId
        )
    }

    fun getUpdatedDbModel(model: BankAccountModel, dbModel: BankAccountDbModel): BankAccountDbModel {
        return dbModel.copy(
            name = model.name,
            description = model.description,
            bic = model.bic,
            bank = model.bank,
            correspondentAccount = model.correspondentAccount,
            settlementAccount = model.settlementAccount,
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId
        )
    }

    fun getDeletedDbModel(dbModel: BankAccountDbModel): BankAccountDbModel {
        return dbModel.copy(
            deleted = true,
            modifiedAt = metadata.timestamp,
            modifiedBy = metadata.userId
        )
    }

    fun getUpdatedDbModel(dbModel: BankAccountDbModel?, dto: StoryBankAccount): BankAccountDbModel {
        val modifiedAt = dto.modifiedAt?.millis ?: metadata.timestamp
        val modifiedBy = dto.modifiedBy ?: metadata.userId
        return dbModel?.copy(
            id = dto.id,
            profileId = dto.profileId,
            name = dto.name,
            description = dto.description,
            bic = dto.bic ?: "",
            bank = dto.bank ?: "",
            correspondentAccount = dto.correspondentAccount ?: "",
            settlementAccount = dto.settlementAccount ?: "",
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy
        ) ?: BankAccountDbModel(
            internalId = newId(),
            userId = metadata.userId,
            modifiedAt = modifiedAt,
            modifiedBy = modifiedBy,
            id = dto.id,
            profileId = dto.profileId,
            name = dto.name,
            description = dto.description,
            bic = dto.bic ?: "",
            bank = dto.bank ?: "",
            correspondentAccount = dto.correspondentAccount ?: "",
            settlementAccount = dto.settlementAccount ?: ""
        )
    }

    fun getDto(dbModel: BankAccountDbModel): StoryBankAccountDTO {
        val outboundDto = StoryBankAccountDTO()
        outboundDto.name = dbModel.name
        outboundDto.description = dbModel.description
        outboundDto.bank = dbModel.bank
        outboundDto.bic = dbModel.bic
        outboundDto.correspondentAccount = dbModel.correspondentAccount
        outboundDto.settlementAccount = dbModel.settlementAccount
        return outboundDto
    }
}