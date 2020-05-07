package ru.breffi.storyid.profile.model.internal

import ru.breffi.storyid.profile.db.dto.BankAccountDbModel

internal data class BankAccountsUpdateModel(val modelsToInsert: List<BankAccountDbModel>, val modelIdsToDelete: List<String>)