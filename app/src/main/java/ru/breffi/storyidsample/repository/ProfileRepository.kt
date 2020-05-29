package ru.breffi.storyidsample.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import ru.breffi.storyidsample.api.ApiServiceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.breffi.storyid.profile.ProfileInteractor
import ru.breffi.storyid.profile.model.CreateFileModel
import ru.breffi.storyid.profile.model.FileModel
import ru.breffi.storyid.profile.model.ProfileModel
import ru.breffi.storyidsample.utils.getApiErrorCode
import ru.breffi.storyidsample.utils.isFull
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject
constructor(
    private val context: Context,
//    private val profileDao: ProfileDao,
    private val fileRepository: FilesRepository,
    private val apiServiceId: ApiServiceId,
    private val profileInteractor: ProfileInteractor
) {

    /*ФИО*/
    fun getProfile(): LiveData<ProfileModel?> {
        return liveData {
            val profile = withContext(Dispatchers.IO) {
                profileInteractor.getProfile()
            }
            emit(profile)
        }
    }

    suspend fun saveProfile(profile: ProfileModel) {
        withContext(Dispatchers.IO) {
            profileInteractor.updateProfile(profile)
        }
    }

    fun syncProfile() {
        profileInteractor.syncProfile()
        profileInteractor.syncFiles()
    }

    //

    suspend fun setAvatar(file: File) {
        withContext(Dispatchers.IO) {
            profileInteractor.createFile(CreateFileModel("image", "avatar", file))
            profileInteractor.syncFiles()
        }
    }

    suspend fun getAvatar(): FileModel? {
        return withContext(Dispatchers.IO) {
            profileInteractor.getFile("image", "avatar")
        }
    }

    suspend fun deleteAvatar() {
        return withContext(Dispatchers.IO) {
            profileInteractor.removeFile("image", "avatar")
            profileInteractor.syncFiles()
        }
    }
}

