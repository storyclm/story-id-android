package ru.breffi.storyid.profile.handler

import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.profile.api.ApiServiceProvider
import ru.breffi.storyid.profile.util.dataIsUpToDate
import ru.breffi.storyid.profile.db.BankAccountDataDao
import ru.breffi.storyid.profile.db.dto.BankAccountDbModel
import ru.breffi.storyid.profile.util.get
import ru.breffi.storyid.profile.mapper.BankAccountMapper
import ru.breffi.storyid.profile.mapper.BankAccountModelMapper
import ru.breffi.storyid.profile.model.BankAccountModel
import ru.breffi.storyid.profile.model.CreateBankAccountModel
import ru.breffi.storyid.profile.model.internal.BankAccountsUpdateModel
import ru.breffi.storyid.profile.model.internal.Metadata
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class BankAccountsHandler internal constructor(
    private val apiServiceProvider: ApiServiceProvider,
    private val bankAccountDataDao: BankAccountDataDao,
    private val authDataProvider: AuthDataProvider
) {
    private val bankAccountsLock = ReentrantLock()
    private val bankAccountsSyncInProgress = AtomicBoolean(false)

    /**
     *  Blocking
     */
    fun syncBankAccounts() {
        if (bankAccountsSyncInProgress.get()) return

        try {
            bankAccountsSyncInProgress.set(true)
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val metadata = Metadata(userId, System.currentTimeMillis())
                syncBankAccountsData(metadata)?.let {
                    bankAccountsLock.withLock {
                        executeLocalBankAccountUpdate(it)
                    }
                }
            }
        } finally {
            bankAccountsSyncInProgress.set(false)
        }
    }


    /**
     *  Blocking
     */
    fun getBankAccounts(): List<BankAccountModel> {
        return bankAccountsLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val mapper = BankAccountModelMapper()
                bankAccountDataDao.getUserBankAccounts(userId)
                    .filter { !it.deleted }
                    .map { mapper.mapBankAccountModel(it) }
            } ?: listOf()
        }
    }

    /**
     *  Blocking
     */
    fun getBankAccount(internalId: String): BankAccountModel? {
        return bankAccountsLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                bankAccountDataDao.getByInternalId(internalId)?.let { bankAccount ->
                    if (!bankAccount.deleted) {
                        BankAccountModelMapper().mapBankAccountModel(bankAccount)
                    } else {
                        null
                    }
                }
            }
        }
    }

    /**
     *  Blocking
     */
    fun createBankAccount(createBankAccountModel: CreateBankAccountModel): BankAccountModel? {
        return bankAccountsLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val metadata = Metadata(userId, System.currentTimeMillis())
                val mapper = BankAccountMapper(metadata)
                val dbModel = mapper.mapNewDbModel(createBankAccountModel)
                bankAccountDataDao.insert(dbModel)
                getBankAccount(dbModel.internalId)
            }
        }
    }

    /**
     *  Blocking
     */
    fun updateBankAccount(bankAccountModel: BankAccountModel) {
        bankAccountsLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                bankAccountDataDao.getByInternalId(bankAccountModel.internalId)?.let { dbModel ->
                    val metadata = Metadata(userId, System.currentTimeMillis())
                    val mapper = BankAccountMapper(metadata)
                    bankAccountDataDao.insert(mapper.getUpdatedDbModel(bankAccountModel, dbModel))
                }
            }
        }
    }

    /**
     *  Blocking
     */
    fun removeBankAccount(internalId: String) {
        bankAccountsLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                bankAccountDataDao.getByInternalId(internalId)?.let { dbModel ->
                    val metadata = Metadata(userId, System.currentTimeMillis())
                    val mapper = BankAccountMapper(metadata)
                    bankAccountDataDao.insert(mapper.getDeletedDbModel(dbModel))
                }
            }
        }
    }

    private fun syncBankAccountsData(metadata: Metadata): BankAccountsUpdateModel? {
        return apiServiceProvider.getProfileBankAccountsApi().listBankAccounts().get()?.data?.let { inboundDtoList ->
            val inboundDtoMap = inboundDtoList.associateBy { it.id }
            val dbModels = bankAccountDataDao.getUserBankAccounts(metadata.userId)
            val modelsToInsert = mutableListOf<BankAccountDbModel>()
            val modelIdsToDelete = mutableListOf<String>()
            val mapper = BankAccountMapper(metadata)
            dbModels.forEach { dbModel ->
                if (dbModel.id == null) {
                    val outboundDto = mapper.getDto(dbModel)
                    apiServiceProvider.getProfileBankAccountsApi().createBankAccount(outboundDto).get()?.data?.let { resultDto ->
                        modelsToInsert.add(mapper.getUpdatedDbModel(dbModel, resultDto))
                    }
                } else {
                    val inboundDto = inboundDtoMap[dbModel.id]
                    if (inboundDto != null) {
                        if (!dataIsUpToDate(dbModel.modifiedAt, inboundDto.modifiedAt?.millis)) {
                            if (dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
                                if (dbModel.deleted) {
                                    if (!apiServiceProvider.getProfileBankAccountsApi().deleteBankAccountById(dbModel.id).execute().isSuccessful) {
                                        return null
                                    }
                                } else {
                                    val outboundDto = mapper.getDto(dbModel)
                                    apiServiceProvider.getProfileBankAccountsApi().updateBankAccount(dbModel.id, outboundDto).get()?.data?.let { resultDto ->
                                        modelsToInsert.add(mapper.getUpdatedDbModel(dbModel, resultDto))
                                    }
                                }
                            } else {
                                modelsToInsert.add(mapper.getUpdatedDbModel(dbModel, inboundDto))
                            }
                        }
                    } else {
                        modelIdsToDelete.add(dbModel.id)
                    }
                }
            }

            val dbModelMap = dbModels.associateBy { it.id }
            inboundDtoList
                .filter { !dbModelMap.containsKey(it.id) }
                .forEach { inboundDto ->
                    modelsToInsert.add(mapper.getUpdatedDbModel(null, inboundDto))
                }
            BankAccountsUpdateModel(modelsToInsert, modelIdsToDelete)
        }
    }

    private fun executeLocalBankAccountUpdate(bankAccountsUpdateModel: BankAccountsUpdateModel) {
        bankAccountsUpdateModel.modelsToInsert.forEach {
            bankAccountDataDao.insert(it)
        }
        bankAccountsUpdateModel.modelIdsToDelete.forEach {
            bankAccountDataDao.deleteById(it)
        }
    }

    internal fun getLock(): ReentrantLock {
        return bankAccountsLock
    }
}