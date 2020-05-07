package ru.breffi.storyid.profile

import android.graphics.BitmapFactory
import org.joda.time.DateTime
import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.generated_api.model.*
import ru.breffi.storyid.profile.db.BankAccountDataDao
import ru.breffi.storyid.profile.db.ProfileDataDao
import ru.breffi.storyid.profile.db.dto.BankAccountDbModel
import ru.breffi.storyid.profile.db.dto.DemographicsDbModel
import ru.breffi.storyid.profile.db.dto.ProfileDbModel
import ru.breffi.storyid.profile.mapper.*
import ru.breffi.storyid.profile.model.BankAccountModel
import ru.breffi.storyid.profile.model.CreateBankAccountModel
import ru.breffi.storyid.profile.model.ProfileModel
import ru.breffi.storyid.profile.model.internal.*
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class ProfileInteractor internal constructor(
    private val apiServiceProvider: ApiServiceProvider,
    private val profileDataDao: ProfileDataDao,
    private val bankAccountDataDao: BankAccountDataDao,
    private val authDataProvider: AuthDataProvider,
    private val fileHelper: FileHelper
) {

    private val auxApi = apiServiceProvider.getAuxApi()

    private val profileLock = ReentrantLock()
    private val profileSyncInProgress = AtomicBoolean(false)
    private val bankAccountsLock = ReentrantLock()
    private val bankAccountsSyncInProgress = AtomicBoolean(false)

    /**
     *  Blocking
     */
    fun getProfile(withInitialSync: Boolean = true): ProfileModel? {
        return profileLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->

                var profileDbModel = profileDataDao.getUserProfile(userId)
                if (withInitialSync && profileDbModel == null) {
                    syncProfile()
                    profileDbModel = profileDataDao.getUserProfile(userId)
                    if (profileDbModel == null) {
                        return@let null
                    }
                }

                val metadata = Metadata(userId, System.currentTimeMillis())
                val mapper = ProfileDbMapper(fileHelper, metadata)

                val demographicsModel = mapper.getDemographicsModel(profileDataDao.getUserDemographics(userId))

                val itnModel = mapper.getItnModel(profileDataDao.getUserItn(userId))

                val snilsModel = mapper.getSnilsModel(profileDataDao.getUserSnils(userId))

                val dbPagesMap = profileDataDao.getUserPassportPages(userId)
                    .associateBy { it.page }
                val passportModel = mapper.getPassportModel(
                    profileDataDao.getUserPassport(userId),
                    mapper.getPassportPageModel(dbPagesMap[1], 1),
                    mapper.getPassportPageModel(dbPagesMap[2], 2)
                )

                mapper.getProfileModel(profileDbModel, demographicsModel, itnModel, snilsModel, passportModel)
            }
        }
    }

    /**
     *  Blocking
     */
    fun syncProfile() {
        if (profileSyncInProgress.get()) return

        try {
            profileSyncInProgress.set(true)
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val metadata = Metadata(userId, System.currentTimeMillis())
                val mapper = ProfileDtoMapper(metadata)
                val updatedProfileDbModel = syncProfileData(metadata, mapper)
                val updatedDemographicsDbModel = syncDemographicsData(metadata, mapper)
                val itnUpdateModel = syncItnData(metadata, mapper)
                val snilsUpdateModel = syncSnilsData(metadata, mapper)
                val passportUpdateModel = syncPassportData(metadata, mapper)
                profileLock.withLock {
                    executeLocalProfileUpdate(updatedProfileDbModel, updatedDemographicsDbModel, itnUpdateModel, snilsUpdateModel, passportUpdateModel)
                }
            }
        } finally {
            profileSyncInProgress.set(false)
        }
    }

    private fun executeLocalProfileUpdate(
        profileDbModel: ProfileDbModel?,
        demographicsDbModel: DemographicsDbModel?,
        itnUpdateModel: ItnUpdateModel?,
        snilsUpdateModel: SnilsUpdateModel?,
        passportUpdateModel: PassportUpdateModel?
    ) {
        profileDbModel?.let { profileDataDao.insertProfile(it) }
        demographicsDbModel?.let { profileDataDao.insertDemographics(it) }
        itnUpdateModel?.let {
            profileDataDao.insertItn(it.dbModel)
            if (it.fileAction == FileAction.COPY_FROM_TMP) {
                fileHelper.move(FileHelper.itnFilename(true), FileHelper.itnFilename())
            } else if (it.fileAction == FileAction.DELETE) {
                fileHelper.delete(FileHelper.itnFilename())
            }
        }
        snilsUpdateModel?.let {
            profileDataDao.insertSnils(it.dbModel)
            if (it.fileAction == FileAction.COPY_FROM_TMP) {
                fileHelper.move(FileHelper.snilsFilename(true), FileHelper.snilsFilename())
            } else if (it.fileAction == FileAction.DELETE) {
                fileHelper.delete(FileHelper.snilsFilename())
            }
        }
        passportUpdateModel?.let {
            profileDataDao.insertPassport(it.dbModel)
            it.pages.forEach { pageUpdateModel ->
                profileDataDao.insertPassportPage(pageUpdateModel.dbModel)
                if (pageUpdateModel.fileAction == FileAction.COPY_FROM_TMP) {
                    fileHelper.move(
                        FileHelper.passportPageFilename(pageUpdateModel.dbModel.page, true),
                        FileHelper.passportPageFilename(pageUpdateModel.dbModel.page)
                    )
                } else if (pageUpdateModel.fileAction == FileAction.DELETE) {
                    fileHelper.delete(FileHelper.passportPageFilename(pageUpdateModel.dbModel.page))
                }
            }
        }
    }

    private fun syncProfileData(metadata: Metadata, mapper: ProfileDtoMapper): ProfileDbModel? {
        var inboundDto = apiServiceProvider.getProfileApi().profile.get()
        val dbModel = profileDataDao.getUserProfile(metadata.userId)

        if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto.modifiedAt?.millis)) return null

        if (dbModel != null && dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
            val outboundDto = StoryProfileDTO()
            outboundDto.email = dbModel.email
            outboundDto.emailVerified = dbModel.emailVerified
            outboundDto.phone = dbModel.phone
            outboundDto.phoneVerified = dbModel.phoneVerified
            outboundDto.username = dbModel.username
            inboundDto = apiServiceProvider.getProfileApi().updateProfile(outboundDto).get()
        }
        return mapper.getUpdatedProfileDbModel(inboundDto, dbModel)
    }

    private fun syncDemographicsData(metadata: Metadata, mapper: ProfileDtoMapper): DemographicsDbModel? {
        var inboundDto = apiServiceProvider.getProfileDemographicsApi().demographics.get()
        val dbModel = profileDataDao.getUserDemographics(metadata.userId)

        if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto.modifiedAt?.millis)) return null

        if (dbModel != null && dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
            val outboundDto = StoryDemographicsDTO()
            outboundDto.name = dbModel.name
            outboundDto.surname = dbModel.surname
            outboundDto.patronymic = dbModel.patronymic
            outboundDto.gender = dbModel.gender
            outboundDto.birthday = DateTime(dbModel.birthday)
            inboundDto = apiServiceProvider.getProfileDemographicsApi().updateDemographics(outboundDto).get()
        }
        return mapper.getUpdatedDemographicsDbModel(inboundDto, dbModel)
    }

    private fun syncItnData(metadata: Metadata, mapper: ProfileDtoMapper): ItnUpdateModel? {
        var inboundDto = apiServiceProvider.getProfileItnApi().itn.get()
        val dbModel = profileDataDao.getUserItn(metadata.userId)

        if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto.modifiedAt?.millis)) return null

        var fileAction = FileAction.NO_OP
        var filename = dbModel?.fileName
        if (dbModel != null && dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
            val outboundDto = StoryITNDTO()
            outboundDto.itn = dbModel.itn
            inboundDto = apiServiceProvider.getProfileItnApi().setItn(outboundDto).get()
            if (dbModel.fileName != null) {
                val file = fileHelper.getFile(dbModel.fileName)
                val result = auxApi.putItnImageAsync(AuxApi.createFilePart(file)).execute()
            } else {
                val result = auxApi.deleteItnImageAsync().execute()
            }
        } else {
            if (inboundDto.size ?: 0 == 0L) {
                fileAction = FileAction.DELETE
                filename = null
            } else {
                try {
                    auxApi.getItnImageAsync().execute().body()?.let { itnImage ->
                        val bitmap = BitmapFactory.decodeStream(itnImage.byteStream())
                        fileHelper.copyFromBitmap(bitmap, FileHelper.itnFilename(true))
                        fileAction = FileAction.COPY_FROM_TMP
                        filename = FileHelper.itnFilename()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        val updatedDbModel = mapper.getUpdatedItnDbModel(inboundDto, dbModel, filename)
        return ItnUpdateModel(updatedDbModel, fileAction)
    }

    private fun syncSnilsData(metadata: Metadata, mapper: ProfileDtoMapper): SnilsUpdateModel? {
        var inboundDto = apiServiceProvider.getProfileSnilsApi().snils.get()
        val dbModel = profileDataDao.getUserSnils(metadata.userId)

        if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto.modifiedAt?.millis)) return null

        var fileAction = FileAction.NO_OP
        var filename = dbModel?.fileName
        if (dbModel != null && dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
            val outboundDto = StorySNILSDTO()
            outboundDto.snils = dbModel.snils
            inboundDto = apiServiceProvider.getProfileSnilsApi().setSnils(outboundDto).get()
            if (dbModel.fileName != null) {
                val file = fileHelper.getFile(dbModel.fileName)
                val result = auxApi.putSnilsImageAsync(AuxApi.createFilePart(file)).execute()
            } else {
                val result = auxApi.deleteSnilsImageAsync().execute()
            }
        } else {
            if (inboundDto.size ?: 0 == 0L) {
                fileAction = FileAction.DELETE
                filename = null
            } else {
                try {
                    auxApi.getSnilsImageAsync().execute().body()?.let { itnImage ->
                        val bitmap = BitmapFactory.decodeStream(itnImage.byteStream())
                        fileHelper.copyFromBitmap(bitmap, FileHelper.snilsFilename(true))
                        fileAction = FileAction.COPY_FROM_TMP
                        filename = FileHelper.snilsFilename()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        val updatedDbModel = mapper.getUpdatedSnilsDbModel(inboundDto, dbModel, filename)
        return SnilsUpdateModel(updatedDbModel, fileAction)
    }

    private fun syncPassportData(metadata: Metadata, mapper: ProfileDtoMapper): PassportUpdateModel? {
        var inboundDto = apiServiceProvider.getProfilePassportApi().pasport.get()
        val dbModel = profileDataDao.getUserPassport(metadata.userId)

        if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto.modifiedAt?.millis)) return null

        val pageDbModels = profileDataDao.getUserPassportPages(metadata.userId)
        val fileActions = mutableMapOf<Int, FileAction>()
        val pageFileNames = pageDbModels.associateBy({ it.page }, { it.fileName }).toMutableMap()
        if (dbModel != null && dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
            val outboundDto = StoryPasportDTO()
            outboundDto.code = dbModel.code
            outboundDto.sn = dbModel.sn
            outboundDto.issuedAt = DateTime(dbModel.issuedAt)
            outboundDto.issuedBy = dbModel.issuedBy
            pageDbModels.forEach { pageDbModel ->
                val outboundPageDto = StoryPasportPageViewModel()
                outboundPageDto.page = pageDbModel.page
                outboundPageDto.modifiedAt = DateTime(metadata.timestamp)
                outboundPageDto.modifiedBy = metadata.userId
                outboundDto.addPagesItem(outboundPageDto)
            }
            inboundDto = apiServiceProvider.getProfilePassportApi().setPasport(outboundDto).get()
            pageDbModels.forEach { pageDbModel ->
                if (pageDbModel.fileName != null) {
                    val file = fileHelper.getFile(pageDbModel.fileName)
                    val result = auxApi.putPassportImageAsync(pageDbModel.page, AuxApi.createFilePart(file)).execute()
                } else {
                    val result = auxApi.deletePassportImageAsync(pageDbModel.page).execute()
                }
            }
        } else {
            if (inboundDto.pages == null) {
                fileActions[1] = FileAction.DELETE
                fileActions[2] = FileAction.DELETE
                profileDataDao.deletePassportPages()
            } else {
                inboundDto.pages?.forEach { page ->
                    if (page.size ?: 0 == 0L) {
                        fileActions[page.page] = FileAction.DELETE
                        pageFileNames[page.page] = null
                    } else {
                        try {
                            auxApi.getPassportImageAsync(page.page).execute().body()?.let { pageImage ->
                                val bitmap = BitmapFactory.decodeStream(pageImage.byteStream())
                                fileHelper.copyFromBitmap(bitmap, FileHelper.passportPageFilename(page.page, true))
                                fileActions[page.page] = FileAction.COPY_FROM_TMP
                                pageFileNames[page.page] = FileHelper.passportPageFilename(page.page)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        val pageUpdateModels = inboundDto.pages
            ?.map { inboundPage ->
                mapper.getUpdatedPassportPageDbModel(inboundPage, pageDbModels.find { it.page == inboundPage.page }, pageFileNames[inboundPage.page])
            }
            ?.map { PassportPageUpdateModel(it, fileActions[it.page] ?: FileAction.NO_OP) }
            ?: listOf()
        val updatedDbModel = mapper.getUpdatedPassportDbModel(inboundDto, dbModel)
        return PassportUpdateModel(updatedDbModel, pageUpdateModels)
    }

    /**
     *  Blocking
     */
    fun syncBankAccounts() {
        if (bankAccountsSyncInProgress.get()) return

        try {
            bankAccountsSyncInProgress.set(true)
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val metadata = Metadata(userId, System.currentTimeMillis())
                val updateModel = syncBankAccountsData(metadata)
                bankAccountsLock.withLock {
                    executeLocalBankAccountUpdate(updateModel)
                }
            }
        } finally {
            bankAccountsSyncInProgress.set(false)
        }
    }

    private fun syncBankAccountsData(metadata: Metadata): BankAccountsUpdateModel {
        val mapper = BankAccountMapper(metadata)
        val inboundDtoList = apiServiceProvider.getProfileBankAccountsApi().listBankAccounts().get()
        val inboundDtoMap = inboundDtoList.associateBy { it.id }
        val dbModels = bankAccountDataDao.getUserBankAccounts(metadata.userId)
        val modelsToInsert = mutableListOf<BankAccountDbModel>()
        val modelIdsToDelete = mutableListOf<String>()
        dbModels.forEach { dbModel ->
            if (dbModel.id == null) {
                val outboundDto = mapper.getDto(dbModel)
                val resultDto = apiServiceProvider.getProfileBankAccountsApi().createBankAccount(outboundDto).get()
                modelsToInsert.add(mapper.getUpdatedDbModel(dbModel, resultDto))
            } else {
                val inboundDto = inboundDtoMap[dbModel.id]
                if (inboundDto != null) {
                    if (!dataIsUpToDate(dbModel.modifiedAt, inboundDto.modifiedAt?.millis)) {
                        if (dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
                            if (dbModel.deleted) {
                                val result = apiServiceProvider.getProfileBankAccountsApi().deleteBankAccountById(dbModel.id).execute()
                            } else {
                                val outboundDto = mapper.getDto(dbModel)
                                val resultDto = apiServiceProvider.getProfileBankAccountsApi().updateBankAccount(dbModel.id, outboundDto).get()
                                modelsToInsert.add(mapper.getUpdatedDbModel(dbModel, resultDto))
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

        return BankAccountsUpdateModel(modelsToInsert, modelIdsToDelete)
    }

    private fun executeLocalBankAccountUpdate(bankAccountsUpdateModel: BankAccountsUpdateModel) {
        bankAccountsUpdateModel.modelsToInsert.forEach {
            bankAccountDataDao.insert(it)
        }
        bankAccountsUpdateModel.modelIdsToDelete.forEach {
            bankAccountDataDao.deleteById(it)
        }
    }

    /**
     *  Blocking
     */
    fun updateProfile(profile: ProfileModel) {
        profileLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val metadata = Metadata(userId, System.currentTimeMillis())
                val mapper = ProfileModelMapper(metadata, fileHelper)
                val updatedProfileDbModel = mapper.getUpdatedProfileDbModel(
                    profile, profileDataDao.getUserProfile(userId)
                )
                val updatedDemographicsDbModel = mapper.getUpdatedDemographicsDbModel(
                    profile.demographics, profileDataDao.getUserDemographics(userId)
                )
                val updatedItnDbModel = mapper.getUpdatedItnDbModel(
                    profile.itn, profileDataDao.getUserItn(userId)
                )
                val updatedSnilsDbModel = mapper.getUpdatedSnilsDbModel(
                    profile.snils, profileDataDao.getUserSnils(userId)
                )
                val updatedPassportDbModel = mapper.getUpdatedPassportDbModel(
                    profile.passport, profileDataDao.getUserPassport(userId)
                )
                val dbPages = profileDataDao.getUserPassportPages(userId)
                    .associateBy { it.page }
                val updatedPassportPageDbModels = profile.passport.pages
                    .map { mapper.getUpdatedPassportPageDbModel(it, dbPages[it.page]) }

                profileDataDao.insertProfileData(
                    updatedProfileDbModel,
                    updatedDemographicsDbModel,
                    updatedItnDbModel,
                    updatedSnilsDbModel,
                    updatedPassportDbModel,
                    updatedPassportPageDbModels
                )
            }
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

    /**
     *  Blocking
     */
    fun removeUserData() {
        profileLock.withLock {
            bankAccountsLock.withLock {
                authDataProvider.getAuthData()?.userId?.let { userId ->
                    profileDataDao.deleteProfileData()
                    bankAccountDataDao.deleteAll()
                    fileHelper.clearFiles()
                }
            }
        }
    }

    private fun dataIsUpToDate(dbTimestamp: Long?, serverTimestamp: Long?): Boolean {
        return dbTimestamp != null && serverTimestamp != null && dbTimestamp == serverTimestamp
    }
}