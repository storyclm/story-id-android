package ru.breffi.storyidsample.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import ru.breffi.storyidsample.api.ApiServiceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.breffi.storyid.profile.ProfileInteractor
import ru.breffi.storyid.profile.model.CreateFileModel
import ru.breffi.storyid.profile.model.FileModel
import ru.breffi.storyid.profile.model.FilePathModel
import ru.breffi.storyid.profile.model.ProfileModel
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject
constructor(profileInteractor: ProfileInteractor) {

    companion object {
        private val avatarPath = FilePathModel("image", "avatar")
    }

    private val profileHandler = profileInteractor.profileHandler
    private val fileHandler = profileInteractor.fileHandler

    private val profileChannel = MutableLiveData<ProfileModel?>()
    private val avatarFileChannel = MutableLiveData<FileModel?>()

    fun getProfile(): LiveData<ProfileModel?> {
        return profileChannel
    }

    suspend fun saveProfile(profile: ProfileModel) {
        withContext(Dispatchers.IO) {
            profileHandler.updateProfile(profile)
        }
    }

    fun syncProfile() {
        profileHandler.syncProfile()
        profileChannel.postValue(profileHandler.getProfile())
    }

    fun syncFiles() {
        fileHandler.syncFiles()
        avatarFileChannel.postValue(fileHandler.getFile(avatarPath))
    }

    suspend fun setAvatar(file: File) {
        withContext(Dispatchers.IO) {
            fileHandler.createFile(CreateFileModel(avatarPath, file))
            fileHandler.syncFiles()
        }
    }

    fun getAvatar(): LiveData<FileModel?> {
        return avatarFileChannel
    }

    suspend fun deleteAvatar() {
        return withContext(Dispatchers.IO) {
            fileHandler.removeFile(avatarPath)
            fileHandler.syncFiles()
        }
    }
}

