package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.profile.db.dto.BankAccountDbModel
import ru.breffi.storyid.profile.model.BankAccountModel

internal class BankAccountModelMapper() {

    fun mapBankAccountModel(dbModel: BankAccountDbModel): BankAccountModel {
        return BankAccountModel(
            internalId = dbModel.internalId,
            name = dbModel.name,
            description = dbModel.description,
            bic = dbModel.bic,
            bank = dbModel.bank,
            profileId = dbModel.profileId,
            correspondentAccount = dbModel.correspondentAccount,
            settlementAccount = dbModel.settlementAccount,
            verified = dbModel.verified
        )
    }
}