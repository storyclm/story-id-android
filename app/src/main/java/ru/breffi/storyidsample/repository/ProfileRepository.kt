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
    }

    /**/
//    suspend fun insertImageSync(imageSync: ImageSync) {
//        profileDao.insertImageSync(imageSync)
//    }
//
//    suspend fun getImageSync(image: String): ImageSync? {
//        return profileDao.getImageSync(image)
//    }

    /*АВАТАРКА*/
    suspend fun syncAvatarImage(): Int {
        var hasErrors = 0

//        val imageSync = getImageSync(ImageSync.AVATAR)
//        val file = fileRepository.getFile(AVATAR_IMAGE_FILE)
//        if (imageSync == null || imageSync.isSynced != 0) {
//            try {
//                val avatarImage = apiServiceId.getAvatarImageAsync().await()
//                val bitmap = BitmapFactory.decodeStream(avatarImage.byteStream())
//                fileRepository.copyFromBitmap(bitmap, file.name)
//            } catch (t: Throwable) {
//                fileRepository.delete(file)
//            }
//        } else {
//            try {
//                if (imageSync.deleted == 0) {
//                    apiServiceId.putAvatarImageAsync(createFilePart(file)).await()
//                    imageSync.isSynced = 1
//                    insertImageSync(imageSync)
//                } else {
//                    val id = apiServiceId.getAvatarIdAsync().await().id
//                    apiServiceId.deleteAvatarImageAsync(id).await()
//                    imageSync.isSynced = 1
//                    insertImageSync(imageSync)
//                    fileRepository.delete(file)
//                }
//            } catch (t: Throwable) {
//                if (t.getApiErrorCode() == 404) {
//                    imageSync.isSynced = 1
//                    insertImageSync(imageSync)
//                }
//                hasErrors = 1
//            }
//        }

        return hasErrors
    }

    private fun createFilePart(file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            "file",
            file.name,
            RequestBody.create(MediaType.parse(getMimeType(file)), file)
        )
    }

    private fun getMimeType(file: File): String {
        var type: String? = null
        val url = file.toString()
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
        }
        if (type == null) {
            type = "image/*" // fallback type. You might set it to */*
        }
        return type
    }

    suspend fun profileIsFull(): Boolean {
        val profile = withContext(Dispatchers.IO) { profileInteractor.getProfile() }
        val profileDemographics = profile?.demographics

        if (profile == null || !profile.isFull()) {
            return false
        }
        if (profileDemographics == null || !profileDemographics.isFull()) {
            return false
        }

        if (profile.snils.file == null) {
            return false
        }

        if (profile.itn.file == null) {
            return false
        }

        val passport = profile.passport
        if (passport.pages.size < 2) {
            return false
        } else {
            for (passportPage in passport.pages) {
                if (passportPage.file == null) {
                    return false
                }
            }
        }

        return true
    }
}

