package ru.breffi.storyid.profile.handler

import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.generated_api.model.FileViewModel
import ru.breffi.storyid.profile.api.ApiServiceProvider
import ru.breffi.storyid.profile.util.FileHelper
import ru.breffi.storyid.profile.api.AuxApi
import ru.breffi.storyid.profile.util.dataIsUpToDate
import ru.breffi.storyid.profile.db.FilesDataDao
import ru.breffi.storyid.profile.mapper.FileMapper
import ru.breffi.storyid.profile.mapper.FileModelMapper
import ru.breffi.storyid.profile.model.CreateFileModel
import ru.breffi.storyid.profile.model.FileModel
import ru.breffi.storyid.profile.model.FilePathModel
import ru.breffi.storyid.profile.model.internal.FileAction
import ru.breffi.storyid.profile.model.internal.FileUpdateModel
import ru.breffi.storyid.profile.model.internal.Metadata
import ru.breffi.storyid.profile.util.get
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class FileHandler internal constructor(
    private val apiServiceProvider: ApiServiceProvider,
    private val filesDataDao: FilesDataDao,
    private val authDataProvider: AuthDataProvider,
    private val fileHelper: FileHelper
) {

    private val auxApi = apiServiceProvider.getAuxApi()
    private val filesLock = ReentrantLock()
    private val filesSyncInProgress = AtomicBoolean(false)
    /**
     *  Blocking
     */
    fun getFiles(): List<FileModel> {
        return filesLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val mapper = FileModelMapper(fileHelper)
                filesDataDao.getUserFiles(userId)
                    .filter { !it.deleted }
                    .map { mapper.mapFileModel(it) }
            } ?: listOf()
        }
    }

    /**
     *  Blocking
     */
    fun getFile(internalId: String): FileModel? {
        return filesLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                filesDataDao.getByInternalId(internalId)?.let { fileDbModel ->
                    if (!fileDbModel.deleted) {
                        FileModelMapper(fileHelper).mapFileModel(fileDbModel)
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
    fun getFile(path: FilePathModel): FileModel? {
        return filesLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                filesDataDao.getByPath(path.category, path.name)?.let { fileDbModel ->
                    if (!fileDbModel.deleted) {
                        FileModelMapper(fileHelper).mapFileModel(fileDbModel)
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
    fun createFile(createFileModel: CreateFileModel): FileModel? {
        return filesLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val metadata = Metadata(userId, System.currentTimeMillis())
                val mapper = FileMapper(fileHelper, metadata)
                val currentDbModel = filesDataDao.getByPath(createFileModel.path.category, createFileModel.path.name)
                val dbModel = mapper.mapNewOrUpdatedDbModel(createFileModel, currentDbModel)
                filesDataDao.insert(dbModel)
                getFile(dbModel.internalId)
            }
        }
    }

    /**
     *  Blocking
     */
    fun removeFile(path: FilePathModel) {
        filesLock.withLock {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                filesDataDao.getByPath(path.category, path.name)?.let { dbModel ->
                    val metadata = Metadata(userId, System.currentTimeMillis())
                    val mapper = FileMapper(fileHelper, metadata)
                    filesDataDao.insert(mapper.getDeletedDbModel(dbModel))
                }
            }
        }
    }

    /**
     *  Blocking
     */
    fun syncFiles() {
        if (filesSyncInProgress.get()) return

        try {
            filesSyncInProgress.set(true)
            authDataProvider.getAuthData()?.userId?.let { userId ->
                val metadata = Metadata(userId, System.currentTimeMillis())
                syncFilesData(metadata)?.let {
                    filesLock.withLock {
                        executeLocalFilesUpdate(it)
                    }
                }
            }
        } finally {
            filesSyncInProgress.set(false)
        }
    }

    private fun syncFilesData(metadata: Metadata): List<FileUpdateModel>? {
        return apiServiceProvider.getProfileFilesApi().listFiles().get()?.data?.let { inboundDtoList ->
            val fileUpdateModels = mutableListOf<FileUpdateModel>()
            val inboundDtoMap = inboundDtoList.associateBy { it.id }
            val dbModels = filesDataDao.getUserFiles(metadata.userId)
            val mapper = FileMapper(fileHelper, metadata)
            dbModels.forEach { dbModel ->
                if (dbModel.id == null) {
                    if (dbModel.fileName != null) {
                        if (!auxApi.putFileAsync(dbModel.category, dbModel.name, fileHelper.getFilePart(dbModel.fileName)).execute().isSuccessful) {
                            return null
                        }
                        apiServiceProvider.getProfileFilesApi().getCategoryFileByName(dbModel.category, dbModel.name).get()?.data?.let {
                            val updatedDbModel = mapper.getUpdatedDbModel(dbModel, it)
                            fileUpdateModels.add(FileUpdateModel(updatedDbModel, FileAction.NO_OP))
                        }
                    } else {
                        fileUpdateModels.add(FileUpdateModel(dbModel, FileAction.DELETE))
                    }
                } else {
                    val inboundDto = inboundDtoMap[dbModel.id]
                    if (inboundDto != null) {
                        if (!dataIsUpToDate(dbModel.modifiedAt, inboundDto.modifiedAt?.millis)) {
                            if (dbModel.modifiedAt > inboundDto.modifiedAt?.millis ?: 0) {
                                if (dbModel.deleted) {
                                    if (!apiServiceProvider.getProfileFilesApi().deleteFile(dbModel.id).execute().isSuccessful) {
                                        return null
                                    }
                                    fileUpdateModels.add(FileUpdateModel(dbModel, FileAction.DELETE))
                                } else if (dbModel.fileName != null) {
                                    if (!auxApi.putFileAsync(dbModel.category, dbModel.name, fileHelper.getFilePart(dbModel.fileName)).execute().isSuccessful) {
                                        return null
                                    }
                                    apiServiceProvider.getProfileFilesApi().getCategoryFileByName(dbModel.category, dbModel.name).get()?.data?.let {
                                        val updatedDbModel = mapper.getUpdatedDbModel(dbModel, it)
                                        fileUpdateModels.add(FileUpdateModel(updatedDbModel, FileAction.NO_OP))
                                    }
                                }
                            } else {
                                downloadFile(mapper, inboundDto)?.let {
                                    fileUpdateModels.add(it)
                                }
                            }
                        }
                    } else {
                        fileUpdateModels.add(FileUpdateModel(dbModel, FileAction.DELETE))
                    }
                }
            }

            val dbModelMap = dbModels.associateBy { it.id }
            inboundDtoList
                .filter { !dbModelMap.containsKey(it.id) }
                .forEach { inboundDto ->
                    downloadFile(mapper, inboundDto)?.let {
                        fileUpdateModels.add(it)
                    }
                }
            fileUpdateModels
        }
    }

    private fun downloadFile(mapper: FileMapper, inboundDto: FileViewModel): FileUpdateModel? {
        return inboundDto.category?.let { category ->
            try {
                auxApi.getFileAsync(category, inboundDto.name).execute().body()?.let { file ->
                    val fileName = FileHelper.filename(category, inboundDto.name, true)
                    fileHelper.copyFromResponse(file, fileName)
                    return FileUpdateModel(mapper.getUpdatedDbModel(null, inboundDto), FileAction.COPY_FROM_TMP)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
    }

    private fun executeLocalFilesUpdate(fileUpdateModels: List<FileUpdateModel>) {
        fileUpdateModels.forEach { fileUpdateModel ->
            filesDataDao.insert(fileUpdateModel.dbModel)
            if (fileUpdateModel.fileAction == FileAction.COPY_FROM_TMP) {
                fileHelper.move(
                    FileHelper.filename(fileUpdateModel.dbModel.category, fileUpdateModel.dbModel.name, true),
                    FileHelper.filename(fileUpdateModel.dbModel.category, fileUpdateModel.dbModel.name)
                )
            } else if (fileUpdateModel.fileAction == FileAction.DELETE) {
                filesDataDao.deleteByInternalId(fileUpdateModel.dbModel.internalId)
                fileHelper.delete(FileHelper.filename(fileUpdateModel.dbModel.category, fileUpdateModel.dbModel.name))
            }
        }
    }

    internal fun getLock(): ReentrantLock {
        return filesLock
    }
}