package ru.breffi.storyid.profile.handler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import org.joda.time.DateTime
import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.generated_api.model.*
import ru.breffi.storyid.profile.api.ApiServiceProvider
import ru.breffi.storyid.profile.util.FileHelper
import ru.breffi.storyid.profile.api.AuxApi
import ru.breffi.storyid.profile.util.dataIsUpToDate
import ru.breffi.storyid.profile.db.ProfileDataDao
import ru.breffi.storyid.profile.db.dto.DemographicsDbModel
import ru.breffi.storyid.profile.db.dto.ProfileDbModel
import ru.breffi.storyid.profile.mapper.ProfileDbMapper
import ru.breffi.storyid.profile.mapper.ProfileDtoMapper
import ru.breffi.storyid.profile.mapper.ProfileModelMapper
import ru.breffi.storyid.profile.model.ProfileModel
import ru.breffi.storyid.profile.model.internal.*
import ru.breffi.storyid.profile.util.get
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Stream
import kotlin.concurrent.withLock

class ProfileHandler internal constructor(
    private val apiServiceProvider: ApiServiceProvider,
    private val profileDataDao: ProfileDataDao,
    private val authDataProvider: AuthDataProvider,
    private val fileHelper: FileHelper
) {
    private val auxApi = apiServiceProvider.getAuxApi()

    private val profileLock = ReentrantLock()
    private val profileSyncInProgress = AtomicBoolean(false)

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

                val dbPages = profileDataDao.getUserPassportPages(userId)
                    .map { mapper.getPassportPageModel(it) }
                    .sortedBy { it.page }
                val passportModel = mapper.getPassportModel(profileDataDao.getUserPassport(userId), dbPages)

                mapper.getProfileModel(profileDbModel, demographicsModel, itnModel, snilsModel, passportModel)
            }
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
                    .distinctBy { it.page }
                    .filter { dbPages.contains(it.page) || it.file != null }
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
                fileHelper.move(
                    FileHelper.snilsFilename(true),
                    FileHelper.snilsFilename()
                )
            } else if (it.fileAction == FileAction.DELETE) {
                fileHelper.delete(FileHelper.snilsFilename())
            }
        }
        passportUpdateModel?.let {
            profileDataDao.insertPassport(it.dbModel)
            it.pages.forEach { pageUpdateModel ->
                if (pageUpdateModel.dbModel != null) {
                    profileDataDao.insertPassportPage(pageUpdateModel.dbModel)
                } else {
                    profileDataDao.deletePassportPage(pageUpdateModel.page)
                }
                if (pageUpdateModel.fileAction == FileAction.COPY_FROM_TMP) {
                    fileHelper.move(
                        FileHelper.passportPageFilename(pageUpdateModel.page, true),
                        FileHelper.passportPageFilename(pageUpdateModel.page)
                    )
                } else if (pageUpdateModel.fileAction == FileAction.DELETE) {
                    fileHelper.delete(FileHelper.passportPageFilename(pageUpdateModel.page))
                }
            }
        }
    }

    private fun syncProfileData(metadata: Metadata, mapper: ProfileDtoMapper): ProfileDbModel? {
        val result = apiServiceProvider.getProfileApi().profile.get()
        if (result != null) {
            val inboundDto = result.data
            val dbModel = profileDataDao.getUserProfile(metadata.userId)

            if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto?.modifiedAt?.millis)) return null

            return if (dbModel != null && dbModel.modifiedAt > inboundDto?.modifiedAt?.millis ?: 0) {
                val outboundDto = StoryProfileDTO()
                outboundDto.email = dbModel.email
                outboundDto.emailVerified = dbModel.emailVerified
                outboundDto.phone = dbModel.phone
                outboundDto.phoneVerified = dbModel.phoneVerified
                outboundDto.username = dbModel.username
                apiServiceProvider.getProfileApi().updateProfile(outboundDto).get()?.data?.let {
                    mapper.getUpdatedProfileDbModel(it, dbModel)
                }
            } else if (inboundDto != null) {
                mapper.getUpdatedProfileDbModel(inboundDto, dbModel)
            } else {
                null
            }
        } else {
            return null
        }
    }

    private fun syncDemographicsData(metadata: Metadata, mapper: ProfileDtoMapper): DemographicsDbModel? {
        val result = apiServiceProvider.getProfileDemographicsApi().demographics.get()
        if (result != null) {
            val inboundDto = result.data
            val dbModel = profileDataDao.getUserDemographics(metadata.userId)

            if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto?.modifiedAt?.millis)) return null

            return if (dbModel != null && dbModel.modifiedAt > inboundDto?.modifiedAt?.millis ?: 0) {
                val outboundDto = StoryDemographicsDTO()
                outboundDto.name = dbModel.name
                outboundDto.surname = dbModel.surname
                outboundDto.patronymic = dbModel.patronymic
                outboundDto.gender = dbModel.gender
                outboundDto.birthday = DateTime(dbModel.birthday)
                apiServiceProvider.getProfileDemographicsApi().updateDemographics(outboundDto).get()?.data?.let {
                    mapper.getUpdatedDemographicsDbModel(it, dbModel)
                }
            } else if (inboundDto != null) {
                mapper.getUpdatedDemographicsDbModel(inboundDto, dbModel)
            } else {
                null
            }
        } else {
            return null
        }
    }

    private fun syncItnData(metadata: Metadata, mapper: ProfileDtoMapper): ItnUpdateModel? {
        val result = apiServiceProvider.getProfileItnApi().itn.get()
        if (result != null) {
            val inboundDto = result.data
            val dbModel = profileDataDao.getUserItn(metadata.userId)

            if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto?.modifiedAt?.millis)) return null

            var fileAction = FileAction.NO_OP
            var filename = dbModel?.fileName
            if (dbModel != null && dbModel.modifiedAt > inboundDto?.modifiedAt?.millis ?: 0) {
                val outboundDto = StoryITNDTO()
                outboundDto.itn = dbModel.itn
                return apiServiceProvider.getProfileItnApi().setItn(outboundDto).get()?.data?.let {
                    if (dbModel.fileName != null) {
                        if (!auxApi.putItnImageAsync(fileHelper.getFilePart(dbModel.fileName)).execute().isSuccessful) {
                            return null
                        }
                    } else {
                        if (!auxApi.deleteItnImageAsync().execute().isSuccessful) {
                            return null
                        }
                    }
                    val updatedDbModel = mapper.getUpdatedItnDbModel(it, dbModel, filename)
                    ItnUpdateModel(updatedDbModel, fileAction)
                }
            } else if (inboundDto != null) {
                if (inboundDto.size ?: 0 == 0L) {
                    fileAction = FileAction.DELETE
                    filename = null
                } else {
                    try {
                        auxApi.getItnImageAsync().execute().body()?.let { itnImage ->
                            val bitmap: Bitmap? = BitmapFactory.decodeStream(itnImage.byteStream())
                            if (bitmap != null) {
                                fileHelper.copyFromBitmap(bitmap, FileHelper.itnFilename(true))
                                fileAction = FileAction.COPY_FROM_TMP
                                filename = FileHelper.itnFilename()
                            } else {
                                if (!auxApi.deleteItnImageAsync().execute().isSuccessful) {
                                    return null
                                }
                                fileAction = FileAction.DELETE
                                filename = null
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return null
                    }
                }
                val updatedDbModel = mapper.getUpdatedItnDbModel(inboundDto, dbModel, filename)
                return ItnUpdateModel(updatedDbModel, fileAction)
            } else {
                return null
            }
        } else {
            return null
        }
    }

    private fun syncSnilsData(metadata: Metadata, mapper: ProfileDtoMapper): SnilsUpdateModel? {
        val result = apiServiceProvider.getProfileSnilsApi().snils.get()
        if (result != null) {
            val inboundDto = result.data
            val dbModel = profileDataDao.getUserSnils(metadata.userId)

            if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto?.modifiedAt?.millis)) return null

            var fileAction = FileAction.NO_OP
            var filename = dbModel?.fileName
            if (dbModel != null && dbModel.modifiedAt > inboundDto?.modifiedAt?.millis ?: 0) {
                val outboundDto = StorySNILSDTO()
                outboundDto.snils = dbModel.snils
                return apiServiceProvider.getProfileSnilsApi().setSnils(outboundDto).get()?.data?.let {
                    if (dbModel.fileName != null) {
                        if (!auxApi.putSnilsImageAsync(fileHelper.getFilePart(dbModel.fileName)).execute().isSuccessful) {
                            return null
                        }
                    } else {
                        if (!auxApi.deleteSnilsImageAsync().execute().isSuccessful) {
                            return null
                        }
                    }
                    val updatedDbModel = mapper.getUpdatedSnilsDbModel(it, dbModel, filename)
                    return SnilsUpdateModel(updatedDbModel, fileAction)
                }
            } else if (inboundDto != null) {
                if (inboundDto.size ?: 0 == 0L) {
                    fileAction = FileAction.DELETE
                    filename = null
                } else {
                    try {
                        auxApi.getSnilsImageAsync().execute().body()?.let { snilsImage ->
                            val bitmap: Bitmap? = BitmapFactory.decodeStream(snilsImage.byteStream())
                            if (bitmap != null) {
                                fileHelper.copyFromBitmap(bitmap, FileHelper.snilsFilename(true))
                                fileAction = FileAction.COPY_FROM_TMP
                                filename = FileHelper.snilsFilename()
                            } else {
                                if (!auxApi.deleteSnilsImageAsync().execute().isSuccessful) {
                                    return null
                                }
                                fileAction = FileAction.DELETE
                                filename = null
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return null
                    }
                }
                val updatedDbModel = mapper.getUpdatedSnilsDbModel(inboundDto, dbModel, filename)
                return SnilsUpdateModel(updatedDbModel, fileAction)
            } else {
                return null
            }
        } else {
            return null
        }
    }

    private fun syncPassportData(metadata: Metadata, mapper: ProfileDtoMapper): PassportUpdateModel? {
        val result = apiServiceProvider.getProfilePassportApi().pasport.get()
        if (result != null) {
            val inboundDto = result.data
            val dbModel = profileDataDao.getUserPassport(metadata.userId)

            if (dataIsUpToDate(dbModel?.modifiedAt, inboundDto?.modifiedAt?.millis)) return null

            val pageDbModels = profileDataDao.getUserPassportPages(metadata.userId)
            if (dbModel != null && dbModel.modifiedAt > inboundDto?.modifiedAt?.millis ?: 0) {
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
                return apiServiceProvider.getProfilePassportApi().setPasport(outboundDto).get()?.data?.let { setResultDto ->
                    pageDbModels.forEach { pageDbModel ->
                        if (pageDbModel.fileName != null) {
                            if (!auxApi.putPassportImageAsync(pageDbModel.page, fileHelper.getFilePart(pageDbModel.fileName)).execute().isSuccessful) {
                                return null
                            }
                        } else {
                            if (!auxApi.deletePassportImageAsync(pageDbModel.page).execute().isSuccessful) {
                                return null
                            }
                        }
                    }
                    val localPagesMap = pageDbModels.associateBy { it.page }
                        .toMutableMap()
                    val pageUpdateModels = setResultDto.pages
                        ?.map { pageDto ->
                            val localPage = localPagesMap.remove(pageDto.page)
                            val updatedDto = mapper.getUpdatedPassportPageDbModel(pageDto, localPage, localPage?.fileName)
                            PassportPageUpdateModel(pageDto.page, updatedDto, FileAction.NO_OP)
                        }
                        ?: listOf()
                    val updatedDbModel = mapper.getUpdatedPassportDbModel(setResultDto, dbModel)
                    PassportUpdateModel(updatedDbModel, pageUpdateModels)
                }
            } else if (inboundDto != null) {
                val pageUpdateModels = mutableListOf<PassportPageUpdateModel>()
                if (inboundDto.pages == null) {
                    pageUpdateModels += pageDbModels.map {
                        PassportPageUpdateModel(it.page, null, FileAction.DELETE)
                    }
                } else {
                    val localPagesMap = pageDbModels.associateBy { it.page }
                        .toMutableMap()
                    inboundDto.pages?.forEach { pageDto ->
                        if (pageDto.size ?: 0 == 0L) {
                            pageUpdateModels += PassportPageUpdateModel(pageDto.page, null, FileAction.DELETE)
                        } else {
                            try {
                                auxApi.getPassportImageAsync(pageDto.page).execute().body()?.let { pageImage ->
                                    val bitmap: Bitmap? = BitmapFactory.decodeStream(pageImage.byteStream())
                                    if (bitmap != null) {
                                        fileHelper.copyFromBitmap(bitmap, FileHelper.passportPageFilename(pageDto.page, true))
                                        val localPage = localPagesMap.remove(pageDto.page)
                                        val updatedDto = mapper.getUpdatedPassportPageDbModel(pageDto, localPage, FileHelper.passportPageFilename(pageDto.page))
                                        pageUpdateModels += PassportPageUpdateModel(pageDto.page, updatedDto, FileAction.COPY_FROM_TMP)
                                    } else {
                                        if (!auxApi.deletePassportImageAsync(pageDto.page).execute().isSuccessful) {
                                            return null
                                        }
                                        pageUpdateModels += PassportPageUpdateModel(pageDto.page, null, FileAction.DELETE)
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                return null
                            }
                        }
                    }
                    pageUpdateModels += localPagesMap.values.map {
                        PassportPageUpdateModel(it.page, null, FileAction.DELETE)
                    }
                }
                val updatedDbModel = mapper.getUpdatedPassportDbModel(inboundDto, dbModel)
                return PassportUpdateModel(updatedDbModel, pageUpdateModels)
            } else {
                return null
            }
        } else {
            return null
        }
    }

    internal fun getLock(): ReentrantLock {
        return profileLock
    }
}